# 🎮 Tic Tac Toe --- Jetpack Compose Edition

A modern **Tic Tac Toe** game built with **Jetpack Compose**, featuring
multiple AI difficulty levels, immersive sound and animation, and a
sleek **Material 3** design.

------------------------------------------------------------------------

## ✨ Features

-   🧠 **Four Game Modes**
  -   👥 Player vs Player (PVP)
  -   🎯 Easy AI --- Random move selection
  -   🧩 Medium AI --- Defensive + Winning logic
  -   🧠 Hard AI --- Minimax algorithm with Alpha-Beta pruning
      (Unbeatable)
-   💾 **Game History**
  -   Stores each finished match with date, mode, and board state.
  -   Allows reopening any past match and continuing from that point.
-   🎨 **Jetpack Compose UI**
  -   100% built with **Jetpack Compose** and **Material 3**.
  -   Reactive state handling using `ViewModel` and `StateFlow`.
  -   Responsive layout optimized for both **portrait** and
      **landscape** via `BoxWithConstraints`.
-   ⚙️ **Persistence**
  -   Game state and theme preferences saved using **DataStore**.
  -   Automatically restores game progress and theme settings after
      app restart.

------------------------------------------------------------------------

## 🔊 Animations & Sound Effects

-   🎵 **Sound Effects:**
  -   Added unique **move**, **win**, **lose**, and **draw** sounds.
  -   Added background music (**BGM**).
  -   All sound features can be toggled in the **Settings** screen.
-   🎬 **Animations:**
  -   Smooth animations for board updates and transitions.
  -   Visual feedback for game results and state changes.
  -   Optimized for both light and dark themes.

------------------------------------------------------------------------

## 🧩 Tech Stack

Layer             Technology
  ----------------- ------------------------------------
🎨 UI             Jetpack Compose, Material 3
🧠 Architecture   MVVM (ViewModel + StateFlow)
🧩 Logic          Pure Kotlin (Minimax + Alpha-Beta)
💾 Storage        DataStore + Kotlin Serialization
⚙️ Language       Kotlin

------------------------------------------------------------------------

## 🧠 AI Logic Overview

The **Hard** mode uses the **Minimax algorithm** enhanced with
**Alpha-Beta pruning**, making it nearly **impossible to defeat**.\
It evaluates all possible moves recursively to pick the best outcome
based on perfect play.

Mode     Description
  -------- --------------------------------------------
Easy     Plays random moves
Medium   Tries to win and blocks player threats
Hard     Plays optimally using Minimax + Alpha-Beta

------------------------------------------------------------------------

## 🚀 How to Edit

1.  Clone the repository:

    ``` bash
    git clone https://github.com/dev-Alok-Kumar-android/Tic-Tac-Toe-Android.git
    ```

2.  Switch to the Compose branch:

    ``` bash
    git checkout compose-version
    ```

3.  Open in **Android Studio Hedgehog+** (or newer).

4. Add Some Important Default Files.
(or make a new project (Empty Activity) in Android Studio(can add same package and same name) then replace the src/main then add dependencies )

5. Click **Run ▶️** if done.

------------------------------------------------------------------------

## 🏷️ Version History

  -----------------------------------------------------------------------
Version                             Changes
  ----------------------------------- -----------------------------------
v1.0                                Classic XML-based version

v2.0                                Jetpack Compose rewrite with AI
logic, persistence, and sounds
  -----------------------------------------------------------------------

------------------------------------------------------------------------

## 📄 License

This project is licensed under the **MIT License**.

------------------------------------------------------------------------

💡 *Built with Kotlin and Jetpack Compose.*
