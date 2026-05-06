# 🎮 Gaming Hub (In Progress)

> A multi-game desktop application built in Java — your one-stop platform for classic and logic-based games.

---

## 📋 Project Info

| Field | Details |
|---|---|
| **Course** | CS-212: Object Oriented Programming |
| **Institution** | NUST — School of Electrical Engineering and Computer Science |
| **Instructor** | Dr. Aimal Rextin |
| **Batch** | BESE 16 A |
| **Submission Date** | 11th May 2026 |

### 👥 Team Members

| Name | CMS ID |
|---|---|
| Ahmed Mohiuddin | 543769 |
| Faraz Rasheed | 572957 |
| Alisha Maryam Habib | 555847 |

---

## 🧩 About the Project

**Gaming Hub** is a centralized multi-game desktop application developed in Java. Rather than focusing on a single game, it brings multiple classic and logic-based games together under one interactive GUI — a Game Hub where users can pick, play, and switch between games seamlessly.

The project is built around strong **Object-Oriented Programming (OOP)** principles, featuring modular design and an interactive graphical interface using Java Swing.

---

## ✨ Features

### 🏠 Main Menu
- GUI-based game selection screen built with Java Swing
- Displays all available games in one place
- Navigation options: **Start**, **Back**, **Exit**

### ♟️ Chess
- Two-player mode
- Fully graphical chessboard
- Valid move enforcement and game state management (win/lose detection)
- Piece-based class structure

### 🔢 Sudoku
- Puzzle generation with multiple difficulty levels: **Easy**, **Medium**, **Hard**
- User input with real-time validation
- Option to check progress or auto-complete the puzzle

### 🐍 Snake Game
- GUI-based gameplay with keyboard controls
- Score tracking and increasing difficulty
- Collision detection

### 🃏 Card Games (Uno / Blackjack / Poker)
- Full deck and card management
- Turn-based game flow with rule enforcement
- Optional single-player mode

### ❌⭕ Tic-Tac-Toe
- Two-player mode
- Interactive grid interface with game state tracking
- Difficulty Levels: Easy, Medium, Hard

### 🌀 Maze
- Randomly generated mazes
- Visual display with player navigation
- Option to reveal the solution

---

## 🏗️ Architecture & OOP Design

### Core Classes
```
Game              ← Abstract base class for all games
GameManager       ← Manages game lifecycle and transitions
MainMenuGUI       ← Entry point and game hub interface
Player            ← Represents a human player

```

### Game-Specific Classes
```
Chess     → Piece, Board, Move
Sudoku    → Grid, Cell, GameController
Snake     → Snake, Food, GameBoard
CardGame  → Card, Deck, Hand
TicTacToe → Board, DifficultyLevels
Maze      → Maze, Cell, Navigator
```

### OOP Concepts Applied

| Concept | Application |
|---|---|
| **Encapsulation** | Private variables with controlled access |
| **Inheritance** | All game modules extend a common `Game` base class |
| **Polymorphism** | Shared interfaces, different game behaviors |
| **Abstraction** | Abstract classes define common game structure |
| **Modularity** | Each game is a self-contained, independent module |

---

## 🛠️ Tech Stack

- **Language:** Java
- **GUI Framework:** Java Swing
- **Paradigm:** Object-Oriented Programming (OOP)

---

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Any Java IDE (IntelliJ IDEA, Eclipse, NetBeans) or command line

### Running the Application

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/gaming-hub.git
   cd gaming-hub
   ```

2. **Compile the project**
   ```bash
   javac -d out src/**/*.java
   ```

3. **Run the application**
   ```bash
   java -cp out MainMenuGUI
   ```

---

## 🎮 How to Play

1. Launch the application
2. The **Main Menu** appears with all available games
3. Select a game to open it in a new window
4. Play the game using the on-screen controls
5. Return to the main menu at any time using the **Back** button

---

## 📄 License

This project is submitted as part of the CS-212 coursework at NUST. All rights reserved by the team members.

---
