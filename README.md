# Ticket Bus Booking App

## Overview

The **Ticket Bus Booking App** is a modern, feature-rich mobile application for managing bus ticket reservations. Built with **Kotlin**, **Jetpack Compose**, and **Room Database**, the app provides users with a seamless booking experience. It includes features such as user authentication, dynamic route and bus management, and an intuitive UI powered by **Material Design 3**.

This repository serves as the source code for the project, showcasing an MVVM architecture and leveraging modern Android development best practices.

---

## Features

- **User Authentication**:
  - Secure user registration and login with validation checks.
  - Password hashing for security.

- **Search Functionality**:
  - Search buses by origin, destination, and travel date.
  - Real-time display of search results.

- **Seat Selection**:
  - Interactive seat map for booking specific seats.
  - Real-time seat availability updates.

- **Booking Summary**:
  - View detailed booking summaries.
  - Re-book previous trips with stored data.

- **Responsive UI**:
  - Built with **Jetpack Compose** for dynamic layouts.
  - Material3 components for a polished, user-friendly interface.

---

## Tech Stack

### Languages & Frameworks
- **Kotlin**
- **Jetpack Compose**
- **Room Database**

### Architecture
- **MVVM (Model-View-ViewModel)**:
  - Separation of concerns.
  - Improved testability and maintainability.

### Tools & Libraries
- **Dagger Hilt**: Dependency injection.
- **Kotlin Coroutines**: Asynchronous programming.
- **Material3 Design**: Modern UI components.
- **Room Database**: Local data persistence.

---

## Project Structure

```
.
├── database              # Room database and DAOs
├── model                 # Data models (User, Bus, Route, etc.)
├── repository            # Repositories for managing data sources
├── viewModel             # ViewModels for business logic and state management
├── ui.theme.screens      # Jetpack Compose UI screens
├── util                  # Utility classes for initialization and helpers
└── MainActivity.kt       # Entry point of the app
```

---

## Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/ticket-bus-booking-app.git
   ```

2. **Open in Android Studio**:
   - Open the project in **Android Studio** (latest stable version recommended).

3. **Build the Project**:
   - Sync Gradle dependencies and build the project.

4. **Run the App**:
   - Deploy the app on an emulator or a physical device.

---

## Usage

### Initial Setup
- Upon launching the app, you'll start at the **Registration Screen**.
- Login if you already have an account or proceed to create a new account.

### Booking Process
1. **Select Origin and Destination**:
   - Use dropdown menus to choose a route.
2. **Pick a Travel Date**:
   - Select a date using the integrated date picker.
3. **Search Buses**:
   - View available buses for the chosen route and date.
4. **Select Seats**:
   - Choose specific seats from the interactive seat map.
5. **Confirm Booking**:
   - View a detailed booking summary and confirm.

---
