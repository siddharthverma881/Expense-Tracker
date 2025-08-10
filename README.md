# 📱 Smart Daily Expense Tracker

A full-featured **Smart Daily Expense Tracker** Android application built with **Jetpack Compose** and **MVVM architecture**, designed for small business owners to easily record, view, and analyze daily expenses.

This project was developed as part of the *Smart Daily Expense Tracker — Full Module Assignment (AI-First)*, with added enhancements beyond the base requirements.

---

## ✨ Features

### Core Requirements
1. **Expense Entry Screen**
    - Fields: Title, Amount (₹), Category, Optional Notes (max 100 chars), Optional Receipt Image (mocked)
    - Submit button with toast confirmation and animated entry
    - Real-time "Total Spent Today" at the top

2. **Expense List Screen**
    - View expenses for **Today** (default) or previous dates via calendar/filter
    - Group by **Category** or **Time**
    - Displays total count, total amount, and empty state

3. **Expense Report Screen**
    - Mock report for the last 7 days
    - Daily totals and category-wise totals
    - Mocked bar/line chart
    - Optional export simulation (PDF/CSV) and share intent

---

## 🆕 Additional Enhancements
- **Bottom Navigation Bar** for quick switching between screens
- **Theme Switcher** (Light/Dark mode toggle)
- **Persistent Data Storage** using **Room** and **DataStore**
- **Smooth Animation on Expense Add**
- **Validation** for non-empty title and amount > 0
- **Reusable UI Components** for consistent design and code reuse

---

## 🛠 Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM (Hilt Dependency Injection)
- **State Management:** ViewModel + StateFlow
- **Navigation:** Jetpack Compose Navigation
- **Local Storage:** Room Database
- **Image Loading:** Coil (mocked receipt images)

---

## 📂 Project Structure
```
com.example.expensetracker
│
├── data
│   ├── local (Room entities, DAO)
│   ├── model (Expense, Category)
│   ├── repository (ExpenseRepository)
│
├── di
│   ├── AppModule
│   ├── DatabaseModule
│
├── ui
│   ├── components (Reusable UI)
│   ├── navigation (NavHost, BottomBar)
│   ├── screens
│   │   ├── entry
│   │   ├── list
│   │   ├── report
│
├── viewmodel
│   ├── ExpenseEntryViewModel
│   ├── ExpenseListViewModel
│   ├── ExpenseReportViewModel
│   ├── SettingsViewModel
│
└── util (Validation, Constants, Extensions)
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug | 2024.2.1 or later
- Kotlin JVM target 11
- Android SDK 33 or higher

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/siddharthverma881/Expense-Tracker.git
   ```
2. Open the project in Android Studio.
3. Build and run on an emulator or physical device.

---

## 🤖 AI Usage Summary
This project was developed with **AI assistance** to:
- Generate Jetpack Compose UI layouts
- Structure MVVM architecture
- Create ViewModels, data classes, and repositories
- Optimize validation and state handling
- Suggest UX/UI improvements (animations, theming)
- Write and refine README.md

---

## 📸 Screenshots
*(Add your app screenshots here)*

