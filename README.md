# 📱 Koseemani

**Koseemani** is a mobile application built with **Kotlin** and **Jetpack Compose** that enables users to quickly send **SOS alerts** during emergencies.  
It leverages Android system services like **Broadcast Receivers**, **Foreground Services**, and **Location Services** to provide fast, reliable, and secure emergency communication through **video clips, SMS messages, and cloud backup (Google Drive API)**.  

---

## 🚨 Problem Statement
In critical emergency situations (e.g., danger, accidents, health crises), fast communication is vital — but people often cannot type or call for help.  
Koseemani solves this by providing a **one-tap SOS system** that shares real-time context (video + location) with pre-selected emergency contacts.  

---

## ✨ Features
- 📍 **Real-time Location Tracking** – captures and attaches the user’s live location to SOS alerts.  
- 🎥 **Short Video Recording** – records and sends video clips of the surroundings for situational context.  
- 📡 **Foreground Services & Broadcast Receivers** – ensure the SOS system remains active even when the app is in the background.  
- ☁️ **Google Drive Integration** – automatically uploads SOS videos for redundancy and accessibility.  
- 📨 **Instant SMS Alerts** – sends critical information via text messages to chosen emergency contacts.  
- 🗄 **Local Persistence** – leverages **Room Database** and **Datastore** for storing user preferences, contacts, and history.  
- ⚡ **Jetpack Compose UI** – modern, responsive, and lightweight user interface.  

---

## 🛠 Tech Stack
- **Language**: Kotlin  
- **Architecture**: MVVM (Model-View-ViewModel)  
- **UI**: Jetpack Compose  
- **Persistence**: Room Database, Datastore (Preferences)  
- **APIs & Services**:  
  - Location Services  
  - Broadcast Receivers  
  - Foreground Service  
  - Google Drive API  
- **Other Libraries**: Coroutines, Flow, AndroidX  

---

## 📲 Installation
1. Clone the repository:  
   ```bash
   git clone https://github.com/Tritonfc/koseemani.git
   cd koseemani
