package com.ecosnap.bharat.data

enum class AppMode {
    FAMILY,   // घर मोड (Individual Family)
    SOCIETY   // समाज मोड (Residential Society / Panchayat)
}

enum class AppLanguage(val code: String, val displayName: String, val localeBadge: String = "") {
    HINDI("hi", "हिन्दी", "HI"),
    HINGLISH("hinglish", "Hinglish", "HING"),
    ENGLISH("en", "English", "EN")
}

data class UserProfile(
    val userId: String,
    val name: String,
    val email: String,
    val language: String = "hi",
    val mode: AppMode = AppMode.FAMILY,
    val totalSavedInr: Int = 0,
    val totalDivertedKg: Double = 0.0
)

data class ChatMessage(
    val id: String,
    val sender: String, // "USER" or "AI"
    val text: String,
    val audioBase64: String? = null,
    val language: String = "hi",
    val timestamp: Long = System.currentTimeMillis()
)

data class ScanHistoryItem(
    val scanId: String,
    val itemNameHindi: String,
    val itemNameEnglish: String,
    val category: String,
    val jugaadTitle: String,
    val scrapRate: String,
    val timestamp: Long
)

data class DiagnosticOption(
    val id: String,
    val labelHindi: String,
    val labelEnglish: String
) {
    fun getLabel(lang: AppLanguage): String = if (lang == AppLanguage.ENGLISH) labelEnglish else labelHindi
}

data class DiagnosticQuestion(
    val questionId: String,
    val questionHindi: String,
    val questionEnglish: String,
    val options: List<DiagnosticOption>
) {
    fun getQuestion(lang: AppLanguage): String = if (lang == AppLanguage.ENGLISH) questionEnglish else questionHindi
}

data class WasteAnalysisResult(
    val id: String,
    val itemNameHindi: String,
    val itemNameEnglish: String,
    val materialCategory: String,
    val cleanlinessCondition: String,
    val iconEmoji: String,
    val diagnosticQuestions: List<DiagnosticQuestion>
) {
    fun getItemName(lang: AppLanguage): String = if (lang == AppLanguage.ENGLISH) itemNameEnglish else itemNameHindi
    fun getCategory(lang: AppLanguage): String = if (lang == AppLanguage.ENGLISH) "Category: $materialCategory" else materialCategory
    fun getCondition(lang: AppLanguage): String = cleanlinessCondition
}

data class JugaadSolution(
    val titleHindi: String,
    val titleEnglish: String,
    val timeMinutes: Int,
    val costInr: Int,
    val stepsHindi: List<String>,
    val speechScriptHindi: String
) {
    fun getTitle(lang: AppLanguage): String = if (lang == AppLanguage.ENGLISH) titleEnglish else titleHindi
    fun getSteps(lang: AppLanguage): List<String> = stepsHindi
}

data class EnergySolution(
    val titleHindi: String,
    val titleEnglish: String,
    val energyType: String,
    val methodHindi: String,
    val speechScriptHindi: String
) {
    fun getTitle(lang: AppLanguage): String = if (lang == AppLanguage.ENGLISH) titleEnglish else titleHindi
    fun getType(lang: AppLanguage): String = energyType
    fun getMethod(lang: AppLanguage): String = methodHindi
}

data class ScrapDisposalSolution(
    val binType: String,
    val marketScrapRateInrPerKg: String,
    val preparationTipHindi: String,
    val speechScriptHindi: String
) {
    fun getRate(lang: AppLanguage): String = marketScrapRateInrPerKg
    fun getBinType(lang: AppLanguage): String = binType
    fun getTip(lang: AppLanguage): String = preparationTipHindi
}

data class ImpactMetrics(
    val weightGrams: Int,
    val co2PreventedKg: Double
)

data class SolutionBundle(
    val jugaad: JugaadSolution,
    val energyResource: EnergySolution,
    val scrapDisposal: ScrapDisposalSolution,
    val summarySpeechHindi: String,
    val impact: ImpactMetrics
) {
    fun getSummarySpeech(lang: AppLanguage): String = summarySpeechHindi
}

data class ScrapRate(
    val id: String,
    val nameHindi: String,
    val nameEnglish: String,
    val category: String,
    val minPriceInr: Int,
    val maxPriceInr: Int,
    val unit: String,
    val tipHindi: String
) {
    fun getName(lang: AppLanguage): String = if (lang == AppLanguage.ENGLISH) nameEnglish else nameHindi
    fun getTip(lang: AppLanguage): String = tipHindi
}

data class UserImpactStats(
    val totalSavedInr: Int,
    val totalWasteDivertedKg: Double,
    val totalCompostOrEnzymeLiters: Double,
    val scansCompleted: Int
)

data class SocietyStats(
    val name: String,
    val totalCompostKg: Int,
    val wasteDivertedPercentage: Int,
    val communityScrapRevenueInr: Int,
    val pit1Status: String,
    val pit2Status: String,
    val nextBulkPickupDate: String
) {
    fun getName(lang: AppLanguage): String = name
    fun getPit1Status(lang: AppLanguage): String = pit1Status
    fun getPit2Status(lang: AppLanguage): String = pit2Status
    fun getNextBulkPickupDate(lang: AppLanguage): String = nextBulkPickupDate
}
