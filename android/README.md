# EcoSnap Bharat - Android Mobile Application

Native Android application built with **Kotlin** and **Jetpack Compose (Material 3)** for the **WeMakeDevs Bharat Builds Tour - Environmental Hacks (Track 03: Waste & Energy)**.

---

## 📱 Features Implemented

1. **Bharat-First Vernacular Design System:**
   - High-contrast outdoor color palette: Neem Leaf Green (`#1E7E34`), Surya Amber (`#FF8F00`), Rupee Gold (`#D4AF37`), Khadi White (`#FBFBF9`).
   - Accessible Devanagari typography, large 48dp+ touch targets, and culturally natural metaphors (*Jugaad*, *Khad*, *Kabaad*).

2. **Dual-Mode System:**
   - **🏡 घर मोड (Family / Household):** Personal savings (₹), kilograms diverted from landfill, and micro-compost/enzyme tracking.
   - **👥 समाज मोड (Residential Society / Panchayat):** Community compost pit status, bulk scrap pickup scheduler, and collective society scoreboard.

3. **Smart Camera & Viewfinder:**
   - Camera view with preset selectors (Plastic Bottle, Citrus Peels, Amazon Box, Used Oil, E-Waste Cable).

4. **Conversational Diagnostic Flow:**
   - Instant verification badge and material classification.
   - AI companion dialogue with natural voice playback trigger.
   - Interactive multiple-choice chips (e.g. garden availability, tools on hand).

5. **3-Pathway Actionable Solutions:**
   - 🏡 **Ghar Ka Jugaad (Home DIY Upcycle):** 5-minute zero-cost step-by-step guides.
   - ⚡ **Urja Aur Khad (Home Energy & Resource):** Solar UV water disinfection (SODIS), 90-day bio-enzyme floor cleaner fuel, thermal compost acceleration.
   - 💰 **Kabaad Se Kamai (Scrap Value & Disposal):** Real-time fair market scrap rates in ₹/kg, safe bin classification, and prep advice.
   - 🔊 **Amazon Polly Hindi Voice Player:** Full verbal narration in natural Hindi.
   - ✅ **One-Tap "Log Impact":** Updates personal savings and adds to community landfill diversion.

6. **Live Scrap Price Index (कबाड़ भाव):**
   - Verified market scrap rates across paper, cardboard, PET bottles, hard plastics, scrap iron, copper wire, batteries, and used cooking oil.

7. **Multilingual Vernacular Support (Language Switcher):**
   - Seamless language switching (🌐 हिन्दी / 🇬🇧 English / Vernacular options) directly from top bar header across all app screens.
   - Dynamically localizes all waste analysis result titles, AI diagnostic questions, DIY upcycle steps, energy solutions, scrap prices, and society metrics.

---

## 🚀 How to Run in Android Studio

1. Open **Android Studio**.
2. Click **Open** and select the folder:
   ```
   e:\hackathon\android
   ```
3. Android Studio will automatically sync the Gradle project using Java 17 and Android SDK 36.
4. Connect an Android phone via USB (or start an Android Virtual Device / Emulator).
5. Click the green **Run (▶)** button to launch **EcoSnap Bharat**!

---

## 🛠️ Tech Stack
- **Language:** Kotlin 2.0+
- **UI:** Jetpack Compose + Material 3
- **Navigation:** Single-Activity Compose Navigation with backstack handling
- **Architecture:** MVVM / Clean Architecture with StateFlow
- **Platform Compatibility:** Android 8.0+ (API Level 26 to 36)
