# Design Specification Document (DESIGN.md)

**Project Codename:** *EcoSnap Bharat* (Temporary Name)  
**Target Platform:** Native Android Application (APK) with Material Design 3  
**Target Audience:** Indian Families, Rural & Semi-Urban Households, Gram Panchayats & Residential Societies (RWAs)  
**Supported Languages:** Hindi (हिन्दी), Hinglish, and English  

---

## 1. Design Philosophy & Core Principles

### 1.1 "Bharat-First" UI/UX
* **Zero Cognitive Load:** Designed for users with varying levels of literacy. Icons, colors, and audio guidance replace dense technical text.
* **Large Touch Targets:** Minimum button touch target of 48×48 dp with high visual affordance (raised elevations, tactile feedback).
* **Culturally Familiar Metaphors:** Visual cues draw from everyday Indian life—*Gamla* (planter), *Matka/Balti* (bucket/storage), *Chulha/Urja* (energy), *Kabadiwala* (scrap collector), and *Bachat* (savings).

### 1.2 "Voice as a First-Class Citizen" (VUI)
* Every screen and recommendation provides a prominent **"सुनिए (Listen)"** audio button powered by Amazon Polly.
* The user can either tap quick-reply option chips or speak naturally into the microphone in Hindi or Hinglish.
* Voice pacing is calibrated for easy comprehension: slightly slower speaking rate (0.95x) with clear North Indian/Devanagari phonetics.

### 1.3 High-Contrast & Sunlight Readability
* Strong contrast ratios (WCAG AAA compliant: > 7:1 for text) so the app remains legible under harsh Indian afternoon sunlight in village courtyards or terraces.

---

## 2. Visual Design System

### 2.1 Color Palette

| Role | Color Name | Hex Code | Semantic Meaning |
| :--- | :--- | :--- | :--- |
| **Primary** | Neem Leaf Green | `#1E7E34` | Sustainability, organic compost, freshness |
| **Primary Container** | Pale Mint | `#E8F5E9` | Card backgrounds, success states |
| **Secondary (Energy)** | Surya Amber | `#FF8F00` | Micro-energy, solar, thermal heat, bio-enzymes |
| **Accent (Cash/Value)** | Rupee Gold | `#D4AF37` | Scrap value (₹/kg), monetary savings |
| **Urgent / Disposal** | Terracotta Red | `#C62828` | Hazardous waste, battery disposal, non-recyclable warnings |
| **Surface (Light)** | Off-White / Khadi | `#FBFBF9` | Clean, glare-resistant main background |
| **Text Primary** | Deep Charcoal | `#1A1C1E` | High-contrast readable typography |
| **Text Muted** | Slate Gray | `#5A6065` | Secondary notes, timestamps |

### 2.2 Typography
* **Primary Vernacular Font:** `Noto Sans Devanagari` (for Hindi and bilingual text).
* **Primary Latin Font:** `Roboto` / `Inter` (for English, numbers, and currency values).
* **Font Scale:**
  * **Hero Title / Scrap Value:** 28sp Bold (`₹12 / kg`)
  * **Screen Headers:** 22sp SemiBold
  * **Card Titles:** 18sp Medium
  * **Body / Instructions:** 16sp Regular (larger than typical 14sp for domestic readability)
  * **Voice Captions:** 15sp Medium Italic

---

## 3. Information Architecture & Navigation

The app is organized into a clean bottom navigation bar with an omnipresent **Top Mode Switcher**:

```
┌─────────────────────────────────────────────────────────────┐
│ [🏡 घर मोड (Family) ▼]    [🔊 आवाज चालू]    [🌐 हिन्दी ▼]   │  <- Top Bar
├─────────────────────────────────────────────────────────────┤
│                                                             │
│                                                             │
│                      ACTIVE SCREEN VIEW                     │
│                                                             │
│                                                             │
├─────────────────────────────────────────────────────────────┤
│  [🏠 होम]    [📸 स्कैन करें (HERO)]    [💰 कबाड़ भाव]   [👥 समाज] │  <- Bottom Bar
└─────────────────────────────────────────────────────────────┘
```

1. **Top Bar:**
   * **Mode Switcher Dropdown:** Toggle between **Ghar Mode** (Family) and **Samaj Mode** (Society / Panchayat).
   * **Voice Toggle Button:** Quick mute/unmute of auto-read narrator.
   * **Language Selector:** Instant switch between `हिन्दी`, `Hinglish`, and `English`.
2. **Bottom Navigation:**
   * **Home (`होम`):** Recent scans, daily carbon saved, quick shortcuts.
   * **Camera Scan (`स्कैन करें`):** High-prominence central floating action button.
   * **Scrap Price Index (`कबाड़ भाव`):** Live fair market rates per kg across 15+ materials.
   * **Community Hub (`समाज`):** Society compost pit tracker, community scrap collection, leaderboard.

---

## 4. Screen-by-Screen Wireframes & Specifications

### 4.1 Screen 1: Home Dashboard (Ghar Mode)

```
┌─────────────────────────────────────────────────────────┐
│ 🏡 घर मोड (Family)             🔊 On          🌐 हिन्दी │
├─────────────────────────────────────────────────────────┤
│ नमस्ते, सुनीता जी! 🙏                                    │
│ आज आपने 400g कचरा रिसाइकिल किया।                       │
│                                                         │
│ ┌─────────────────────────────────────────────────────┐ │
│ │ 🌟 आपकी बचत और पर्यावरण स्कोर                       │ │
│ │ • बचाए गए पैसे: ₹38                                 │ │
│ │ • लैंडफिल से रोका कचरा: 2.4 kg                      │ │
│ │ • बनाई गई खाद / एंजाइम: 1.2 लीटर                    │ │
│ └─────────────────────────────────────────────────────┘ │
│                                                         │
│  [ 📸  कचरे का फोटो खींचें (Tap to Scan)  ]              │
│  (बटन दबाएं और कचरा दिखाएं - AI सब बता देगा)            │
│                                                         │
│ 📋 हाल ही में स्कैन किए गए आइटम:                         │
│ ┌─────────────────────────────────────────────────────┐ │
│ │ 🧴 प्लास्टिक तेल की बोतल    -> गमला बनाया (View)    │ │
│ │ 🍊 संतरे के छिलके           -> बायो-एंजाइम तैयार    │ │
│ └─────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

---

### 4.2 Screen 2: Native Camera Viewfinder

```
┌─────────────────────────────────────────────────────────┐
│ [✕ वापस]                      [⚡ फ़्लैश]      [🖼️ गैलरी] │
├─────────────────────────────────────────────────────────┤
│                                                         │
│                ┌───────────────────────┐                │
│                │                       │                │
│                │     [ Target Box ]    │                │
│                │   वस्तु को बीच में    │                │
│                │       रखें...         │                │
│                │                       │                │
│                └───────────────────────┘                │
│                                                         │
│ 💡 सलाह: साफ़ रोशनी में एक समय में एक ही चीज़ दिखाएं   │
│                                                         │
│                     [ 🔘 SNAP PHOTO ]                   │
└─────────────────────────────────────────────────────────┘
```
* **Behaviors:**
  * Auto-focus trigger on viewport center.
  * Instant local thumbnail preview upon capture.
  * Image compressed to WebP/JPEG (< 350 KB) prior to upload.

---

### 4.3 Screen 3: AI Identification & Interactive Voice Q&A

After snapping, the AI identifies the object within 1.5 seconds, displays the item name, and initiates a natural **2-question diagnostic**:

```
┌─────────────────────────────────────────────────────────┐
│ [✕ वापस]                                                │
├─────────────────────────────────────────────────────────┤
│ 📸 फोटो: [Thumbnail]                                    │
│ ✅ पहचान: 1-लीटर प्लास्टिक की बोतल (PET Plastic)         │
│                                                         │
│ 🤖 AI दोस्त:                                            │
│ "यह साफ प्लास्टिक की बोतल है! इसे फेंकने के बजाय हम     │
│ उपयोगी चीज़ बना सकते हैं। बस दो बातें बताइए:"           │
│ [🔊 सुनें]                                              │
│                                                         │
│ ❓ सवाल 1: क्या आपके घर में पौधे या गमले हैं?           │
│   [ (✓) हाँ, गमले हैं ]       [ ( ) नहीं, पौधे नहीं हैं ]│
│                                                         │
│ ❓ सवाल 2: क्या आपके पास कैंची या कटर है?              │
│   [ (✓) हाँ, कैंची है ]       [ ( ) कुछ नहीं है ]       │
│                                                         │
│                [ ✨ समाधान देखें (View Solutions) ]     │
│                 (या बोलकर बताएं: 🎙️ बोलने के लिए दबाएं)  │
└─────────────────────────────────────────────────────────┘
```

---

### 4.4 Screen 4: The 3-Pathway Solution Screen (Core Screen)

The core deliverable presenting the 3 distinct solutions based on the user's answers:

```
┌─────────────────────────────────────────────────────────┐
│ [← वापस]      बोतल का समाधान (3 उपाय)       [🔊 सब सुनें] │
├─────────────────────────────────────────────────────────┤
│                                                         │
│ ┌─ 🏡 उपाय 1: घर का जुगाड़ (DIY Upcycle) ──────────────┐ │
│ │ 🪴 "खुद पानी पीने वाला गमला (Self-Watering Planter)" │ │
│ │ समय: 5 मिनट  |  खर्च: ₹0                             │ │
│ │ 1. बोतल को बीच से कैंची से काटें।                    │ │
│ │ 2. ऊपरी हिस्से को उल्टा करके नीचे रखें।              │ │
│ │ 3. ढक्कन में छेद करके सूती डोरी डालें और मिट्टी भरें।│ │
│ │ [▶️ ऑडियो सुनें]        [⭐ सेव करें]                 │ │
│ └──────────────────────────────────────────────────────┘ │
│                                                         │
│ ┌─ ⚡ उपाय 2: ऊर्जा और संसाधन (Energy & Resource) ─────┐ │
│ │ ☀️ "SODIS: धूप से पीने का पानी शुद्ध करें"           │ │
│ │ • साफ पानी भरें और छत पर 6 घंटे तेज धूप में रखें।    │ │
│ │ • सूरज की UV किरणें कीटाणुओं को नष्ट कर देंगी।       │ │
│ │ [▶️ ऑडियो सुनें]                                      │ │
│ └──────────────────────────────────────────────────────┘ │
│                                                         │
│ ┌─ 💰 उपाय 3: कबाड़ से कमाई (Scrap Value & Disposal) ──┐ │
│ │ ♻️ सूखा कचरा (Dry Waste)                             │ │
│ │ • कबाड़ी रेट: ₹10 - ₹12 प्रति किलो                     │ │
│ │ • बोतल को दबाकर ढक्कन लगा दें ताकि जगह कम घेरे।     │ │
│ │ • नजदीकी कबाड़ी: रामू स्क्रैप (200m दूर, कॉल: 98xxx)   │ │
│ └──────────────────────────────────────────────────────┘ │
│                                                         │
│      [ ✅ मैंने यह उपाय अपना लिया (Log Impact) ]        │
└─────────────────────────────────────────────────────────┘
```

---

### 4.5 Screen 5: Society & Gram Panchayat Hub (Samaj Mode)

When toggled to **Samaj Mode**, the interface adapts for community impact:

```
┌─────────────────────────────────────────────────────────┐
│ 👥 समाज मोड: गोकुल धाम सोसाइटी, ब्लॉक-B     🌐 हिन्दी  │
├─────────────────────────────────────────────────────────┤
│ 🏆 इस महीने का सामुदायिक रिकॉर्ड:                      │
│ ┌─────────────────────────────────────────────────────┐ │
│ │ • कुल खाद तैयार: 340 kg (सोसाइटी पार्क में उपयोग)    │ │
│ │ • गीला कचरा डायवर्ट: 85% (शून्य लैंडफिल लक्ष्य)     │ │
│ │ • सामूहिक कबाड़ बिक्री से आय: ₹4,250                │ │
│ └─────────────────────────────────────────────────────┘ │
│                                                         │
│ 🪱 कम्युनिटी कम्पोस्ट गड्ढा स्थिति:                      │
│ [ गड्ढा #1: तैयार खाद (निकालें) ]                        │
│ [ गड्ढा #2: गीला कचरा भर रहा है (70% Full) ]            │
│                                                         │
│ 🚚 सामूहिक कबाड़ पिकअप:                                  │
│ अगला पिकअप: रविवार, सुबह 10:00 बजे                      │
│ [ ➕ मेरा सूखा कबाड़ (8 kg) पिकअप में जोड़ें ]           │
└─────────────────────────────────────────────────────────┘
```

---

### 4.6 Screen 6: Live Scrap Price Index (कबाड़ भाव)

Prevents rural and domestic sellers from being shortchanged:

```
┌─────────────────────────────────────────────────────────┐
│ 💰 आज का मंडी / कबाड़ भाव (Delhi-NCR & Rural Average)   │
├─────────────────────────────────────────────────────────┤
│ सामग्री (Material)                औसत भाव (प्रति किलो)  │
│ ─────────────────────────────────────────────────────── │
│ 📰 रद्दी अखबार (Old Newspaper)    ₹14 - ₹16 / kg        │
│ 📦 गत्ता / कार्टन (Cardboard)      ₹9 - ₹11 / kg         │
│ 🧴 प्लास्टिक बोतल (PET Bottles)   ₹10 - ₹12 / kg        │
│ 🔩 लोहा (Scrap Iron)               ₹28 - ₹32 / kg        │
│ 🪙 तांबा / वायर (Copper Wire)     ₹420 - ₹460 / kg      │
│ 🔋 खराब बैटरी / इनवर्टर            ₹70 - ₹85 / kg        │
│ 🍳 जला हुआ खाद्य तेल (Used Oil)    ₹20 - ₹25 / kg        │
│                                                         │
│ 💡 सलाह: लोहा और तांबा हमेशा तौलने से पहले अलग करें।    │
└─────────────────────────────────────────────────────────┘
```

---

## 5. Voice User Interface (VUI) & Conversational Script

### 5.1 Speech Synthesis (TTS via Amazon Polly)
* **Voice ID:** `Aditi` (Neural Hindi) or `Kajal` (Neural Indian English / Bilingual).
* **Pronunciation Lexicon:** Custom PLS lexicon maps English technical terms into natural Hindi phonemes:
  * `PET Bottle` $\rightarrow$ `पेट प्लास्टिक बोतल`
  * `Compost` $\rightarrow$ `जैविक खाद`
  * `Bio-Enzyme` $\rightarrow$ `बायो-एंजाइम तरल`
  * `Kabadiwala` $\rightarrow$ `कबाड़ी भाई`

### 5.2 Conversational Flow State Machine

```
[ User Snaps Photo ]
        │
        ▼
[ State: ANALYZING ] ──> Audio: "बस एक सेकंड, हम देख रहे हैं..."
        │
        ▼
[ State: IDENTIFIED ] ──> Audio: "यह [Item Name] है।"
        │
        ▼
[ State: PROMPTING ] ──> Audio: "[Question 1]? बटन दबाएं या बोलकर बताएं।"
        │
  User Speaks / Taps
        │
        ▼
[ State: PRESENTING ] ──> Audio: "आपके लिए 3 सबसे बढ़िया उपाय तैयार हैं। 
                                 पहला उपाय सुनने के लिए 'उपाय 1' दबाएं।"
```

---

## 6. Accessibility & Low-End Device Optimizations

1. **Lightweight APK Budget:** Maximum installed app size **< 20 MB** (optimized native Android build with ProGuard/R8, SVG icons, no heavy local ML models bundled).
2. **Offline Mode & Graceful Degradation:**
   * 30 top household items and their 3-way solutions are pre-cached in local Room SQLite DB.
   * If there is zero internet connectivity in a village field, the app falls back to local keyword search and cached guides.
3. **Bandwidth Optimization:**
   * Audio files synthesized from Amazon Polly are streamed in high-compression `MP3 24kbps mono` or synthesized using native Android TTS if offline.
   * Captured images are resized to $800 \times 600$ max resolution before transmission.

---

## 7. Design Deliverables Summary

| Deliverable | Format | Location in Project |
| :--- | :--- | :--- |
| Screen Wireframes | ASCII / XML Layouts | `res/layout/*.xml` |
| Design Tokens (Colors/Fonts) | XML Values | `res/values/colors.xml`, `type.xml` |
| Voice Interaction Prompts | JSON Strings | `res/values/strings_hi.xml`, `strings_en.xml` |
| Material 3 Theme | Style Definition | `res/values/themes.xml` |
