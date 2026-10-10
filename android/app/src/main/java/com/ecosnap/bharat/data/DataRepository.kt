package com.ecosnap.bharat.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DataRepository {

    private val scope = CoroutineScope(Dispatchers.Main)

    private val _appMode = MutableStateFlow(AppMode.FAMILY)
    val appMode: StateFlow<AppMode> = _appMode.asStateFlow()

    private val _currentLanguage = MutableStateFlow("hi") // "hi", "hinglish", "en"
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _appLanguage = MutableStateFlow(AppLanguage.HINDI)
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _userStats = MutableStateFlow(
        UserImpactStats(
            totalSavedInr = 38,
            totalWasteDivertedKg = 2.4,
            totalCompostOrEnzymeLiters = 1.2,
            scansCompleted = 4
        )
    )
    val userStats: StateFlow<UserImpactStats> = _userStats.asStateFlow()

    private val _societyStats = MutableStateFlow(
        SocietyStats(
            name = "गोकुल धाम सोसाइटी, ब्लॉक-B (Delhi-NCR)",
            totalCompostKg = 340,
            wasteDivertedPercentage = 85,
            communityScrapRevenueInr = 4250,
            pit1Status = "तैयार खाद (निकालने योग्य)",
            pit2Status = "गीला कचरा भर रहा है (70% भरा)",
            nextBulkPickupDate = "रविवार, सुबह 10:00 बजे"
        )
    )
    val societyStats: StateFlow<SocietyStats> = _societyStats.asStateFlow()

    // Conversational Chat Messages
    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage(
                id = "welcome",
                sender = "AI",
                text = "नमस्ते! 🙏 मैं आपका इको-साथी AI हूँ। आप मुझसे किसी भी कचरे, घर के जुगाड़, कम्पोस्ट या आज के कबाड़ी भाव के बारे में पूछ सकते हैं।",
                language = "hi"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Scan History
    private val _scanHistory = MutableStateFlow<List<ScanHistoryItem>>(emptyList())
    val scanHistory: StateFlow<List<ScanHistoryItem>> = _scanHistory.asStateFlow()

    init {
        loadScanHistory()
    }

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
        _appLanguage.value = when (lang) {
            "en" -> AppLanguage.ENGLISH
            "hinglish" -> AppLanguage.HINGLISH
            else -> AppLanguage.HINDI
        }
    }

    fun setAppLanguage(lang: AppLanguage) {
        _appLanguage.value = lang
        _currentLanguage.value = lang.code
    }

    fun toggleAppMode() {
        _appMode.value = if (_appMode.value == AppMode.FAMILY) AppMode.SOCIETY else AppMode.FAMILY
    }

    fun login(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        scope.launch {
            val res = AwsApiClient.login(email, pass)
            if (res != null && res.optString("status") == "success") {
                val u = res.getJSONObject("user")
                _currentUser.value = UserProfile(
                    userId = u.optString("user_id"),
                    name = u.optString("name"),
                    email = u.optString("email"),
                    language = u.optString("language", "hi")
                )
                loadScanHistory()
                onResult(true, null)
            } else {
                // If offline or dev, provide local mock login
                if (email.contains("@")) {
                    _currentUser.value = UserProfile(
                        userId = "user_local_1",
                        name = email.split("@")[0].replaceFirstChar { it.uppercase() },
                        email = email,
                        language = _currentLanguage.value
                    )
                    loadScanHistory()
                    onResult(true, null)
                } else {
                    onResult(false, "कृपया सही ईमेल और पासवर्ड डालें")
                }
            }
        }
    }

    fun signup(name: String, email: String, pass: String, lang: String, onResult: (Boolean, String?) -> Unit) {
        scope.launch {
            val res = AwsApiClient.signup(name, email, pass, lang)
            if (res != null && res.optString("status") == "success") {
                val u = res.getJSONObject("user")
                _currentUser.value = UserProfile(
                    userId = u.optString("user_id"),
                    name = u.optString("name"),
                    email = u.optString("email"),
                    language = u.optString("language", "hi")
                )
                loadScanHistory()
                onResult(true, null)
            } else {
                // Local fallback registration
                _currentUser.value = UserProfile(
                    userId = "user_local_new",
                    name = name.ifBlank { "उपयोगकर्ता" },
                    email = email,
                    language = lang
                )
                loadScanHistory()
                onResult(true, null)
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        loadScanHistory()
    }

    fun sendMessageToAI(text: String, isVoice: Boolean = false) {
        val userMsg = ChatMessage(
            id = "user_${System.currentTimeMillis()}",
            sender = "USER",
            text = text,
            language = _currentLanguage.value
        )
        _chatMessages.value = _chatMessages.value + userMsg

        scope.launch {
            val userId = _currentUser.value?.userId ?: "guest"
            val res = AwsApiClient.sendChatMessage(userId, text, _currentLanguage.value)
            val replyText = res?.optString("reply_text") ?: when (_currentLanguage.value) {
                "hinglish" -> "Plastic bottles aur kitchen wet waste ko ghar par upcycle karke compost ya useful planters banayein!"
                "en" -> "Kitchen scraps make great compost, while clean plastic containers can be reused as planters."
                else -> "गीले कचरे से घर में 30 दिन में उच्च गुणवत्ता की जैविक खाद बनती है। इसे हरे डस्टबिन में डालें।"
            }
            val audioBase64 = res?.optString("audio_base64_mp3")

            val aiMsg = ChatMessage(
                id = "ai_${System.currentTimeMillis()}",
                sender = "AI",
                text = replyText,
                audioBase64 = audioBase64,
                language = _currentLanguage.value
            )
            _chatMessages.value = _chatMessages.value + aiMsg
        }
    }

    fun loadScanHistory() {
        scope.launch {
            val userId = _currentUser.value?.userId ?: "guest"
            val list = AwsApiClient.fetchScanHistory(userId)
            if (list.isNotEmpty()) {
                _scanHistory.value = list
            } else {
                // Initial cached items
                _scanHistory.value = listOf(
                    ScanHistoryItem("h1", "1-लीटर प्लास्टिक बोतल", "Plastic Bottle", "PET Plastic", "खुद पानी पीने वाला गमला", "₹10-12/kg", System.currentTimeMillis() - 86400000),
                    ScanHistoryItem("h2", "संतरे के छिलके", "Citrus Peels", "Organic Waste", "बायो-एंजाइम क्लीनर", "₹0 (खाद ₹8/kg)", System.currentTimeMillis() - 172800000)
                )
            }
        }
    }

    fun logSolutionAdopted(impact: ImpactMetrics, scrapValueInr: Int) {
        val current = _userStats.value
        _userStats.value = current.copy(
            totalSavedInr = current.totalSavedInr + scrapValueInr,
            totalWasteDivertedKg = String.format("%.2f", current.totalWasteDivertedKg + (impact.weightGrams / 1000.0)).toDouble(),
            totalCompostOrEnzymeLiters = String.format("%.2f", current.totalCompostOrEnzymeLiters + 0.3).toDouble(),
            scansCompleted = current.scansCompleted + 1
        )

        val currentSoc = _societyStats.value
        _societyStats.value = currentSoc.copy(
            totalCompostKg = currentSoc.totalCompostKg + (impact.weightGrams / 500),
            communityScrapRevenueInr = currentSoc.communityScrapRevenueInr + scrapValueInr
        )

        // Log scan event to backend history
        scope.launch {
            val userId = _currentUser.value?.userId ?: "guest"
            AwsApiClient.logScanEvent(userId, "कचरा वस्तु", "Waste Item", "Recyclable", "घर का जुगाड़", "₹$scrapValueInr/kg")
            loadScanHistory()
        }
    }

    val scrapRates = listOf(
        ScrapRate("mat_paper", "रद्दी अखबार", "Newspaper", "Paper", 14, 16, "प्रति किलो (per kg)", "अखबार को सूखा रखें, गीला होने पर वजन कट जाता है।"),
        ScrapRate("mat_cardboard", "गत्ता / कार्टन", "Cardboard Box", "Paper", 9, 11, "प्रति किलो (per kg)", "बॉक्स को चपटा करके सुतली से बांधें।"),
        ScrapRate("mat_plastic_pet", "प्लास्टिक बोतल (PET)", "PET Plastic Bottles", "Plastic", 10, 13, "प्रति किलो (per kg)", "बोतलों को दबाकर ढक्कन लगा दें ताकि जगह कम घेरे।"),
        ScrapRate("mat_plastic_hard", "कड़ा प्लास्टिक (बाल्टी/मग)", "Hard Plastic", "Plastic", 16, 20, "प्रति किलो (per kg)", "टूटी बाल्टी और मग का भाव पन्नी से अधिक मिलता है।"),
        ScrapRate("mat_iron", "लोहा (सरिया, जाली)", "Scrap Iron", "Metal", 28, 32, "प्रति किलो (per kg)", "भारी लोहे को जंग लगने से पहले बेचें।"),
        ScrapRate("mat_copper", "तांबा / कॉपर वायर", "Copper Wire", "Metal", 420, 480, "प्रति किलो (per kg)", "तार का प्लास्टिक कवर छीलकर शुद्ध तांबा बेचें।"),
        ScrapRate("mat_battery", "खराब इन्वर्टर बैटरी", "Lead-Acid Battery", "E-waste", 70, 85, "प्रति किलो (per kg)", "बैटरी का तेजाब कभी ज़मीन पर न बहाएं, कबाड़ी को सुरक्षित सौंपें।"),
        ScrapRate("mat_oil", "जला हुआ खाद्य तेल", "Used Cooking Oil", "Bio-Energy", 20, 25, "प्रति लीटर (per Litre)", "बचा तेल छानकर बोतल में रखें, बायो-डीजल संग्रह केंद्र को दें।")
    )

    val sampleItems = listOf(
        WasteAnalysisResult(
            id = "preset_bottle",
            itemNameHindi = "1-लीटर प्लास्टिक पानी की बोतल",
            itemNameEnglish = "1-Litre Plastic Water Bottle",
            materialCategory = "PET Plastic (Code 1)",
            cleanlinessCondition = "साफ (Clean)",
            iconEmoji = "🧴",
            diagnosticQuestions = listOf(
                DiagnosticQuestion("q1", "क्या आपके घर में पौधे या गमले हैं?", "Do you have potted plants or a garden?", listOf(DiagnosticOption("opt1", "हाँ, पौधे हैं", "Yes"), DiagnosticOption("opt2", "नहीं, पौधे नहीं हैं", "No"))),
                DiagnosticQuestion("q2", "क्या आपके पास कैंची या चाकू है?", "Do you have scissors or a cutter?", listOf(DiagnosticOption("opt3", "हाँ, कैंची है", "Yes"), DiagnosticOption("opt4", "कुछ नहीं है", "No tools")))
            )
        ),
        WasteAnalysisResult(
            id = "preset_citrus",
            itemNameHindi = "संतरे और नींबू के छिलके",
            itemNameEnglish = "Fresh Citrus Fruit Peels",
            materialCategory = "जैविक गीला कचरा (Organic Wet Waste)",
            cleanlinessCondition = "ताजा (Fresh)",
            iconEmoji = "🍊",
            diagnosticQuestions = listOf(
                DiagnosticQuestion("q1", "क्या आप घर में फर्श के लिए जैविक क्लीनर बनाना चाहते हैं?", "Do you want to make an organic floor cleaner?", listOf(DiagnosticOption("opt1", "हाँ, क्लीनर बनाना है", "Yes"), DiagnosticOption("opt2", "नहीं, सिर्फ खाद चाहिए", "Only compost"))),
                DiagnosticQuestion("q2", "क्या आपके पास गुड़ (Jaggery) और पानी की बोतल उपलब्ध है?", "Do you have jaggery and a plastic bottle?", listOf(DiagnosticOption("opt3", "हाँ, गुड़ उपलब्ध है", "Yes"), DiagnosticOption("opt4", "नहीं है", "No")))
            )
        ),
        WasteAnalysisResult(
            id = "preset_cardboard",
            itemNameHindi = "अमेज़न डिलीवरी गत्ता बॉक्स",
            itemNameEnglish = "Amazon Cardboard Delivery Box",
            materialCategory = "सेलूलोज़ गत्ता (Cardboard)",
            cleanlinessCondition = "सूखा (Dry)",
            iconEmoji = "📦",
            diagnosticQuestions = listOf(
                DiagnosticQuestion("q1", "क्या आपको घर में सामान रखने के लिए बॉक्स चाहिए?", "Do you need storage organizers?", listOf(DiagnosticOption("opt1", "हाँ, स्टोरेज चाहिए", "Yes"), DiagnosticOption("opt2", "नहीं, सीधे बेचना है", "No"))),
                DiagnosticQuestion("q2", "क्या आपके पास गमले या बागीचा है?", "Do you have garden pots?", listOf(DiagnosticOption("opt3", "हाँ, बागीचा है", "Yes"), DiagnosticOption("opt4", "नहीं है", "No")))
            )
        )
    )

    fun getSolutionsForItem(itemId: String): SolutionBundle {
        return when (itemId) {
            "preset_citrus" -> SolutionBundle(
                jugaad = JugaadSolution("कमरे की प्राकृतिक खुशबू (Room Freshener)", "Natural Citrus Air Freshener", 2, 0, listOf("छिलकों को 1 गिलास पानी में 5 मिनट उबालें।", "पानी ठंडा करके स्प्रे बोतल में भरें।", "कमरे में ताजगी आएगी और मच्छर दूर रहेंगे।"), "उपाय एक: घर का जुगाड़। छिलकों को पानी में उबालकर रूम फ्रेशनर बनाएं।"),
                energyResource = EnergySolution("बायो-एंजाइम क्लीनर (Bio-Enzyme Liquid)", "Bio-Enzyme Multi-Surface Cleaner", "Anaerobic Fermentation Bio-Fuel", "1 हिस्सा गुड़ + 3 हिस्सा छिलके + 10 हिस्सा पानी को बोतल में भरकर 90 दिन रखें। शक्तिशाली प्राकृतिक क्लीनर तैयार होगा।", "उपाय दो: ऊर्जा और संसाधन। गुड़ और पानी के साथ मिलाकर बायो-एंजाइम बनाएं।"),
                scrapDisposal = ScrapDisposalSolution("Wet Waste (हरा डस्टबिन)", "₹0 (खाद मूल्य: ₹8/kg)", "छिलकों को मिट्टी के मटके में दबाएं, 30 दिन में उच्च गुणवत्ता की जैविक खाद बन जाएगी।", "उपाय तीन: सुरक्षित निपटान। इसे हरे डस्टबिन में डालें।"),
                summarySpeechHindi = "नमस्ते! संतरे के छिलकों से शक्तिशाली बायो-एंजाइम क्लीनर बनता है या गमले में डालकर मुफ्त जैविक खाद बनाएं।",
                impact = ImpactMetrics(250, 0.35)
            )
            "preset_cardboard" -> SolutionBundle(
                jugaad = JugaadSolution("किताबों व कपड़ों का आयोजक (Drawer Divider)", "Desk & Drawer Organizer", 6, 0, listOf("बॉक्स के ऊपरी फ्लैप्स को कैंची से काटें।", "अंदर के गत्ते से पार्टिशन बनाएं।", "सामान व्यवस्थित रखें।"), "उपाय एक: घर का जुगाड़। गत्ते के बॉक्स से स्टोरेज ऑर्गेनाइजर बनाएं।"),
                energyResource = EnergySolution("थर्मल मल्चिंग व चूल्हा ब्रिकेट (Mulch & Bio-Briquetting)", "Thermal Soil Mulch & Bio-Briquettes", "Biomass Thermal Carbon Recovery", "गत्ते के छोटे टुकड़े करके गमले की मिट्टी पर बिछाएं। यह नमी रोकता है।", "उपाय दो: ऊर्जा और संसाधन। गत्ते को गमले में बिछाकर पौधों की नमी बचाएं।"),
                scrapDisposal = ScrapDisposalSolution("Dry Waste (नीला डस्टबिन)", "₹9 - ₹11 प्रति किलो", "बॉक्स को चपटा दबाकर रस्सी से बांध लें।", "उपाय तीन: कबाड़ से कमाई। इसका कबाड़ी भाव नौ से ग्यारह रुपये प्रति किलो है।"),
                summarySpeechHindi = "नमस्ते! अमेज़न गत्ते से घर का ऑर्गेनाइजर बनाएं, या चपटा करके कबाड़ी को नौ से ग्यारह रुपये प्रति किलो बेचें।",
                impact = ImpactMetrics(180, 0.22)
            )
            else -> SolutionBundle(
                jugaad = JugaadSolution("खुद पानी पीने वाला गमला (Self-Watering Planter)", "Self-Watering Planter", 5, 0, listOf("बोतल को बीच से कैंची से काटें।", "ऊपरी हिस्से को उल्टा करके नीचे रखें।", "ढक्कन में छेद करके सूती डोरी डालें और मिट्टी भरें।"), "उपाय एक: घर का जुगाड़। बिना पैसे खर्च किए एक सुंदर गमला बना सकते हैं।"),
                energyResource = EnergySolution("SODIS: सौर ऊर्जा से पीने का पानी शुद्ध करें", "SODIS: Solar Water Disinfection", "Solar UV-A Thermal Recovery", "साफ बोतल में पानी भरें और 6 घंटे के लिए छत पर धूप में रखें। UV किरणें बैक्टीरिया समाप्त कर देंगी।", "उपाय दो: सौर ऊर्जा से पानी की शुद्धि। बोतल को धूप में छह घंटे रखकर पानी पीने लायक बनाएं।"),
                scrapDisposal = ScrapDisposalSolution("Dry Waste (नीला डस्टबिन)", "₹10 - ₹12 प्रति किलो", "बोतल को पैरों से पिचकाकर ढक्कन लगा दें ताकि कम जगह घेरे।", "उपाय तीन: कबाड़ से कमाई। इसे नीले डस्टबिन में डालें। भाव दस से बारह रुपये प्रति किलो है।"),
                summarySpeechHindi = "नमस्ते! प्लास्टिक बोतल से पांच मिनट में गमला बनाएं, या धूप में पानी शुद्ध करें, या कबाड़ी को दस से बारह रुपये किलो बेचें।",
                impact = ImpactMetrics(40, 0.08)
            )
        }
    }
}
