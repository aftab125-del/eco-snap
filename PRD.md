# Product Requirement Document (PRD)

**Project Codename:** *EcoSnap Bharat* (Temporary Name — Subject to Final Branding)  
**Event:** WeMakeDevs — Bharat Builds Tour (Environmental Hacks)  
**Track:** Track 03 — Waste and Energy  
**Target Platform:** Native Android Application (APK) with AWS Cloud Backend  
**Primary Languages:** Hindi, Hinglish, and English (Voice & Text)  

---

## 1. Executive Summary & Product Vision

### 1.1 Vision
To empower every Indian household, village resident, and residential community to transform daily waste into valuable resources, home energy, and direct cash earnings through an intuitive, conversational AI companion that speaks their language.

### 1.2 Elevator Pitch
Over 60% of household waste generated in India is dumped into landfills or burned openly because people lack simple, local guidance on what else to do with it. *EcoSnap Bharat* is an Android application designed for everyday Indian families and village communities. Users simply point their phone camera at any waste item; the AI identifies it, talks back in clear Hindi or Hinglish, asks 1–2 quick questions about their home setup, and provides immediate, step-by-step DIY upcycling guides (*jugaad*), home energy/compost recipes, and live scrap market rates (₹/kg) for local *kabadiwalas*.

---

## 2. Problem Statement & User Context

### 2.1 The Problem in the Indian Context
1. **Source Segregation Failure:** Most citizens find generic "dry vs. wet waste" rules confusing and unmotivating without direct utility or reward.
2. **Language & Tech Barriers:** Modern sustainability apps are built in technical English, ignoring rural, semi-urban, and elderly demographics who prefer verbal communication in Hindi or Hinglish.
3. **Loss of Value & Unfair Scrap Pricing:** Households throw away recyclable metals, plastics, and cardboard without realizing their economic value, or get underpaid by unorganized scrap buyers.
4. **Untapped Micro-Energy Potential:** Kitchen wet waste, organic matter, and used cooking oil are discarded rather than utilized for bio-enzymes, compost acceleration, or community biogas feedstocks.

### 2.2 Key Opportunity
By combining **Multimodal Computer Vision** with **Two-Way Voice AI in Hindi/Hinglish** and **Indian vernacular upcycling methods**, we can make waste management culturally natural, financially rewarding, and actionable at the grassroots level.

---

## 3. Target Personas & User Modes

The application operates in **two distinct modes**:

### 3.1 Mode 1: Individual Household / Family Mode (*Ghar Mode*)
* **Persona A: Sunita Sharma (48, Homemaker in a Tier-2 Town / Village)**
  * *Context:* Manages kitchen and household waste; prefers speaking in Hindi; unfamiliar with complex apps.
  * *Need:* Quick answer on what to do with plastic oil cans, vegetable peel piles, and old clothes without reading long text.
* **Persona B: Rahul Verma (22, Rural Youth / College Student)**
  * *Context:* Wants practical DIY hacks to repurpose scrap materials into useful study tools, garden planters, or sellable scrap.
  * *Need:* Direct rupee value estimation and voice assistance in Hinglish.

### 3.2 Mode 2: Residential Society & Gram Panchayat Mode (*Samaj Mode*)
* **Persona C: RWA Secretary / Village Panchayat Pradhan**
  * *Context:* Responsible for managing cluster waste, community compost pits, and dealing with local waste aggregators.
  * *Need:* Aggregated logs of diverted waste, tracking organic waste fed into community compost/biogas pits, and pooling bulk recyclables for higher scrap prices.

---

## 4. Goals and Non-Goals

### 4.1 MVP Goals (In Scope)
* **One-Tap Camera Snap:** Capture real-time photos of waste items using native Android camera.
* **Accurate Material Classification:** Classify items across Indian waste categories (PET bottles, MLP multilayer plastics, organic wet waste, cardboard, e-waste, metals, glass, used cooking oil).
* **Interactive Conversational Flow:** AI identifies the item and asks 1–2 quick, contextual multiple-choice questions (e.g., *"Do you have a balcony/garden?"*, *"Do you have basic scissors/tools?"*).
* **Bilingual Voice & Text (Hindi, Hinglish, English):** Full two-way voice capability—the app speaks instructions aloud with human-like Hindi pronunciation and listens to user voice responses.
* **Three Actionable Solution Cards:**
  1. *Ghar Ka Jugaad (Home DIY Upcycle):* Simple, numbered steps using common household tools.
  2. *Urja Aur Khad (Home Energy & Soil Fuel):* Bio-enzyme recipes, compost activation, thermal mulch, or biogas feedstock preparation.
  3. *Kabaad Se Kamai (Waste-to-Cash & Disposal):* Current fair market scrap rates (₹/kg) and safe bin/drop-off recommendations.
* **Dual-Mode Architecture:** Seamless toggle between Family Mode and Society/Panchayat Mode.
* **Offline Caching:** Cached standard recipes, scrap price indices, and previous scans accessible without active internet.

### 4.2 Non-Goals (Out of Scope for Hackathon MVP)
* Physical IoT integration (e.g., smart bin sensor hardware).
* Integrated online payment gateway for scrap buying.
* Real-time GPS tracking of live scrap trucks (mocked directory/phonebook is sufficient for MVP).

---

## 5. Detailed Feature Requirements

### 5.1 Module 1: Smart Vision & Identification
| Requirement ID | Description | Priority |
| :--- | :--- | :--- |
| **FR-VIS-01** | Capture photo via device camera or select from local gallery. | P0 (Must Have) |
| **FR-VIS-02** | Compress image client-side to < 500 KB for rapid transmission on 2G/3G/4G networks. | P0 (Must Have) |
| **FR-VIS-03** | Cloud AI identifies material composition, cleanliness condition, and recyclable category. | P0 (Must Have) |
| **FR-VIS-04** | Return confidence score and immediate plain-language identification tag in Hindi/English (e.g., "प्लास्टिक की बोतल (PET-1)"). | P0 (Must Have) |

### 5.2 Module 2: Interactive Voice & Chat Conversational Flow
| Requirement ID | Description | Priority |
| :--- | :--- | :--- |
| **FR-CONV-01** | AI prompts user with 1–2 brief situational questions via quick-tap chips and voice (e.g., "क्या आपके पास गमला या बागीचा है?"). | P0 (Must Have) |
| **FR-CONV-02** | Speech-to-Text (STT) and Text-to-Speech (TTS) engine enabling user to speak their answer or tap a button. | P0 (Must Have) |
| **FR-CONV-03** | AI voice playback using natural Hindi / Indian English accent (via Amazon Polly). | P0 (Must Have) |
| **FR-CONV-04** | Simple, everyday language generation—avoids scientific jargon (uses words like *खाद*, *जुगाड़*, *रद्दी*, *तेल*). | P0 (Must Have) |

### 5.3 Module 3: Three-Way Action Pathways
| Pathway | Output Description | Example (Citrus / Fruit Peels) | Example (1L Plastic Bottle) |
| :--- | :--- | :--- | :--- |
| **Pathway 1: DIY Upcycling (*Ghar Ka Jugaad*)** | Step-by-step practical repurposing at zero cost. | Natural room freshener or infused vinegar cleaner. | Self-watering planter or funnel for grain pouring. |
| **Pathway 2: Energy & Fuel (*Urja Aur Khad*)** | Biological/chemical energy or nutrient fuel recovery. | **Bio-Enzyme:** Ferment peels + jaggery + water for floor cleaner; high-nitrogen compost fuel. | **SODIS Solar Disinfection:** Purify drinking water using 6-hr UV-A sunlight exposure. |
| **Pathway 3: Scrap Value (*Kabaad Se Kamai*)** | Fair market price estimation in ₹/kg and safe disposal route. | Wet Waste / Green Bin. Zero scrap value, but high compost value. | Recyclable Dry Waste. Scrap value: **₹10–12 / kg**. Blue Bin. |

### 5.4 Module 4: Dual Mode (Family vs. Residential Society)
| Mode | Features & Capabilities |
| :--- | :--- |
| **Family Mode** | Personal scan history, home carbon/waste diversion counter, saved DIY bookmarks, individual scrap earnings calculator. |
| **Residential Society / Panchayat Mode** | Society community leaderboard, aggregated kilograms diverted from landfill, bulk scrap pickup coordination, community compost pit status. |

### 5.5 Module 5: Offline First & Low Bandwidth Design
| Requirement ID | Description | Priority |
| :--- | :--- | :--- |
| **FR-OFF-01** | Local SQLite/Room DB cache storing 25+ standard upcycling recipes and scrap price tables. | P1 (Should Have) |
| **FR-OFF-02** | Offline queue: photos captured without network are automatically uploaded when connection resumes. | P1 (Should Have) |

---

## 6. End-to-End User Flow

```
[ User Launches Native Android App ]
                 │
       Select Mode: Family or Society
                 │
       [ Tap "Snap Waste" Button ]
                 │
         [ Capture Camera Photo ]
                 │
    [ Upload to AWS Lambda + S3 ]
                 │
  [ Amazon Bedrock Vision Analysis ]
                 │
  [ AI Speaks & Shows Initial Tag ]
   "Yeh 1-litre plastic bottle hai!"
                 │
     [ Quick Clarifying Prompt ]
   "Kya aapke paas paudhe ya gamla hai?"
          [ Haan ] / [ Nahi ]
                 │
 [ AI Generates 3 Actionable Cards ]
 ┌──────────────────────┬──────────────────────┬──────────────────────┐
 │  🏡 Ghar Ka Jugaad   │  ⚡ Urja Aur Khad    │  💰 Kabaad Se Kamai  │
 │  Self-watering pot   │  SODIS Solar Purify  │  ₹12/kg Scrap Value  │
 │  (Step-by-step DIY)  │  (Solar Water Prep)  │  (Nearest Kabadi)    │
 └──────────────────────┴──────────────────────┴──────────────────────┘
                 │
        [ Tap "Listen (Sunिये)" ]
  -> Amazon Polly speaks steps in Hindi
                 │
 [ Tap "Log Saved Waste" -> Updates Society / Family Metrics ]
```

---

## 7. AWS Cloud & Hackathon Technical Compliance

To meet the WeMakeDevs & AWS Environmental Hacks rules:
1. **AWS Open Source & Local Tooling:** Developed and tested locally using **AWS SAM CLI** and **LocalStack** (satisfies the open source/local build requirement).
2. **Deployed Cloud Architecture (Free Tier Compatible):**
   * **API Layer:** Amazon API Gateway (REST API endpoint).
   * **Compute:** AWS Lambda (Python 3.12 / Node.js 20 runtime for zero idle cost).
   * **AI Reasoning:** Amazon Bedrock (Anthropic Claude 3.5 Sonnet / Haiku for multimodal image understanding and Hindi prompt completions).
   * **Speech Engine:** Amazon Polly (Aditi / Kajal voices for fluent Hindi & Indian English neural speech).
   * **Storage:** Amazon S3 (compressed image upload buckets) + Amazon DynamoDB (user stats, scrap rate catalog, society logs).
   * **Auth (Optional):** Amazon Cognito (lightweight phone/anonymous session login).

---

## 8. Success Metrics & Judging Alignment

| Evaluation Criteria | How This Product Wins |
| :--- | :--- |
| **Idea & Impact** | Directly empowers the 1.4B Indian population at the source; transforms passive waste disposal into active economic and energy recovery. |
| **Built on AWS** | Deep integration of Amazon Bedrock, Polly, Lambda, DynamoDB, API Gateway, and local development via AWS SAM CLI. |
| **Design & Usability** | Voice-first in Hindi/Hinglish; big intuitive buttons designed for rural and domestic users with zero tech literacy required. |
| **Execution** | Reliable 3-step conversational flow producing working, verified DIY guides in under 3 seconds. |
| **Demo Appeal** | A live camera test with real waste items (plastic bottle, vegetable peels, old wires) showing instant Hindi voice playback will captivate the judges. |

---

## 9. Next Steps in Documentation
1. **`DESIGN.md`**: Screen layouts, UI wireframes, voice interaction flows, and vernacular tone guidelines.
2. **`ARCHITECTURE.md`**: Detailed system architecture, API schemas, prompt engineering templates, DynamoDB table designs, and deployment configurations.
