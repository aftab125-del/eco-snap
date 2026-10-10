# 🌿 EcoSnap Bharat (इकोस्नैप भारत)
### Vernacular Home Waste-to-Wealth, DIY Jugaad, Micro-Energy & Fair Scrap Price Companion

> **WeMakeDevs Bharat Builds Tour — Environmental Hacks (Track 03: Waste & Energy)**  
> *Transforming household & community waste into economic value through vernacular AI intelligence.*

---

## 📌 Problem Statement
Over 62 million tonnes of municipal solid waste is generated annually in India, with more than 70% ending up in landfills without segregation. Most households discard recyclable plastics, organic waste, and scrap without knowing:
1. Practical DIY upcycling (*ghar ka jugaad*) that can save money immediately.
2. Safe home bio-energy or composting methods (e.g., bio-enzymes, SODIS solar disinfection).
3. Fair local scrap market prices (*kabadiwala rates*), often leading to distress selling or dumping.
4. Language barriers in existing apps that don't support voice or vernacular languages (Hindi, Hinglish).

---

## 💡 The Solution: EcoSnap Bharat
**EcoSnap Bharat** is a lightweight, vernacular Android application designed for Indian households, tier-2/tier-3 towns, and residential housing societies (RWA/Panchayats).

- **📸 Instant Waste Diagnostics:** Camera scanning identifies waste materials (PET plastics, glass, metals, organic citrus peels, cardboard).
- **🛠️ 3-Way Actionable Solution Bundles:**
  - **उपाय 1: घर का जुगाड़ (DIY Upcycling):** 5-minute step-by-step household projects (self-watering planters, drawer organizers).
  - **उपाय 2: ऊर्जा व संसाधन (Energy & Compost):** Bio-enzyme cleaners, anaerobic fermentation, solar UV purification.
  - **उपाय 3: कबाड़ से कमाई (Fair Scrap Value):** Transparent benchmark rates per kg for newspapers, metals, plastics, and batteries.
- **🤖 इको-साथी AI (EcoSaathi AI):** Two-way voice and text AI companion answering vernacular queries with natural neural voice playback in Hindi, Hinglish, and English.
- **🔐 User Accounts & Cloud History:** Secure registration and login with real-time tracking of past scans, adopted solutions, and conversation history.
- **🏡 Dual Modes (Family vs Society):**
  - **Ghar Mode:** Tracks household money saved (₹), kilograms diverted, and compost produced.
  - **Samaj Mode:** Coordinates community compost pit rotation and bulk scrap buyer pickups.

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                   EcoSnap Bharat Android App                │
│             (Kotlin • Jetpack Compose • Material 3)         │
└──────────────┬───────────────────────────────┬──────────────┘
               │                               │
       HTTP REST API                   Local Storage / Cache
               ▼                               ▼
┌─────────────────────────────────────────────────────────────┐
│                 EcoSnap Cloud Backend                       │
│        (Python FastAPI / AWS Serverless Architecture)       │
└──────┬───────────────────┬───────────────────┬──────────────┘
       │                   │                   │
       ▼                   ▼                   ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│ Amazon       │    │ Amazon Polly │    │ Amazon       │
│ Bedrock      │    │ (Aditi       │    │ DynamoDB     │
│ (Generative  │    │ Neural TTS)  │    │ (Auth, Logs, │
│ Intelligence)│    │              │    │ History)     │
└──────────────┘    └──────────────┘    └──────────────┘
```

---

## 🚀 Tech Stack

### Mobile Client (Android)
- **Language:** Kotlin 2.0+
- **UI Framework:** Jetpack Compose (Material Design 3)
- **Architecture:** StateFlow, Coroutines, Repository Pattern
- **Design Palette:** Bharat-First high-contrast palette (*Neem Green, Pale Mint, Surya Amber, Rupee Gold, Khadi*)
- **Target SDK:** Android 14 (API 34)

### Cloud & Backend
- **Core Runtime:** Python 3.13 / FastAPI v2.0
- **AI Intelligence:** Amazon Bedrock (`amazon.nova-lite-v1:0` / Claude)
- **Neural Voice Synthesis:** Amazon Polly (`Aditi` bilingual neural voice)
- **Database & Storage:** Amazon DynamoDB (Users, Scans, Chat history) & Amazon S3
- **Region:** `ap-southeast-2` (Sydney)

---

## 🛠️ Getting Started

### 1. Prerequisites
- Android Studio Ladybug / Meerkat or Android SDK command-line tools
- JDK 17+ (e.g. Eclipse Temurin or JBR 17)
- Python 3.10+
- AWS Account with Bedrock & Polly access (or use smart local fallback)

### 2. Backend Setup
```bash
# Clone the repository
git clone https://github.com/aftab125-del/eco-snap.git
cd eco-snap/backend

# Create virtual environment and install dependencies
python -m venv .venv
source .venv/bin/activate  # On Windows: .venv\Scripts\activate
pip install -r requirements.txt

# Configure environment variables
cp .env.example .env
# Edit .env with your AWS credentials if using cloud features

# Start the backend server
python server.py
# Server will run at http://127.0.0.1:8000 (accessible to Android emulator at http://10.0.2.2:8000)
```

### 3. Android Client Setup
```bash
cd ../android

# Build Debug APK
./gradlew assembleDebug

# Install on connected device or running emulator
./gradlew installDebug
```

---

## 🔒 Security & Privacy
- Zero cloud indicators or telemetry watermarks in the consumer UI.
- Passwords salted and hashed with SHA-256 before storage.
- All secrets and environment variables are excluded from version control via `.gitignore`.

---

## 👥 Authors & Acknowledgments
Built with ❤️ for **WeMakeDevs Bharat Builds Tour**.  
Dedicated to empowering Indian households, waste pickers, and societies towards a sustainable, zero-waste Bharat.
