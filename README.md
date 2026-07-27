# 🎮 Tic Tac Toe --- 3D Multi-Layer Edition

[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)](https://developer.android.com/android)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.x-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack-Compose-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A high-performance, multi-dimensional **Tic Tac Toe** game built from the ground up with **Jetpack Compose**. This edition introduces a revolutionary **Pseudo-3D engine**, advanced **Monte Carlo simulations**, and a robust **Clean Architecture** to provide a professional-grade gaming experience.

---

## 🚀 Key Features

### 🧱 Pseudo-3D & Multi-Layer Boards
- **Beyond 2D:** Play on grids spanning multiple layers (e.g., 3x3x3, 5x5x2).
- **Dynamic Dimensions:** Fully customizable rows, columns, and layers through the "Quick Setup" menu.
- **Winning Streak Control:** Adjust the required streak (3, 4, or 5) to win, scaling with board complexity.

### 🧠 Advanced AI & Match Analysis
- **Impossible Mode:** An unbeatable AI utilizing **Minimax with Alpha-Beta pruning**.
- **Humanized Difficulty:** AI strength slider (1-100) that injects probabilistic errors, simulating different skill levels.
- **Monte Carlo fairness Check:** Before starting, the app can run a 1,000-iteration random simulation to calculate win probabilities and verify if a custom board configuration is fair for both players.

### 🌍 Global & Tactical UX
- **Hindi Localization:** Full support for Hindi (`values-hi`) with a seamless in-app language switcher.
- **Haptic Feedback:** Distinct vibration patterns for moves, victories, and draws powered by a dedicated hardware service.
- **Cyber-Grid Aesthetics:** A custom particle-based confetti system and interactive "snake" chain backgrounds.

---

## 🏗️ Clean Architecture

The project follows the **SOLID** principles and **Clean Architecture** to ensure maintainability and testability.

```text
com.tuto.alokkumar.tictactoe
├── core/             # Hardware services, Preferences, Utility classes
├── data/             # Models, Repositories (Impl), DataStore persistence
├── domain/           # UseCases, Business Models, Logic (The "Truth")
│   ├── model/        # Domain-specific data classes
│   └── usecase/      # CalculateMatchProbabilities, ProcessMove, GetAiMove
├── di/               # Hilt Modules (Dependency Injection)
├── viewModel/        # Screen-specific State management
└── ui/               # UI Layer (Composables, Themes, Screens)
```

- **Unidirectional Data Flow:** State flows down, events flow up.
- **Type-safe Navigation:** Uses Kotlin Serialization for robust screen transitions.

---

## 🔬 Technical Deep Dive

### AI Logic: Minimax + Alpha-Beta
The AI evaluates millions of possible game states. To optimize performance on mobile, we implement:
- **Transposition Tables:** Caching previously evaluated board states.
- **Board Symmetry Reduction:** Identifying rotations and flips to prune the search tree.

### Monte Carlo Simulation
The "Match Analysis" feature uses a random-play algorithm to simulate thousands of games in milliseconds. This provides users with a statistical "Fairness Score" for their custom board setups.

---

## 🗺️ Roadmap

- [ ] **v3.1.0:** 🛜 Local Multiplayer via Bluetooth.
- [ ] **v3.2.0:** 🔥 Global Multiplayer via Firebase/Realtime Database.
- [ ] **v3.5.0:** 📱 Tablet & Foldable UI optimization (Two-pane layout).
- [ ] **v4.0.0:** 🎨 Custom 3D Asset support and skin marketplace.

---

## 🛠️ Installation & Setup

1. **Prerequisites:** Android Studio Ladybug (2024.2.1) or newer.
2. **Clone:**
   ```bash
   git clone https://github.com/dev-Alok-Kumar-android/Tic-Tac-Toe-Android.git
   ```
3. **Branch:**
   ```bash
   git checkout feature-pseudo-3d
   ```
4. **Build:** Sync Gradle and hit **Run ▶️**.

---

## 🏷️ Version History

| Version | Milestone | Key Changes |
| :--- | :--- | :--- |
| **v3.0.0-alpha2** | **3D & Architecture** | UseCases, Hilt, Monte Carlo, Pseudo-3D UI. |
| **v2.3.0** | **Unified Logic** | Merged 2D/3D systems, Orientation support. |
| **v2.1.0** | **Compose Shift** | Migrated from XML to Jetpack Compose. |
| **v1.0.0** | **Legacy** | Initial XML implementation. |

---

## 📄 License & Credits

Distributed under the **MIT License**. Created by [Alok Kumar](https://github.com/dev-Alok-Kumar-android).

---
💡 *Pushing the boundaries of classic games with modern technology.*
