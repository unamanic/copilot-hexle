# Hexle – Detailed Specification

## Overview
A Gradle multi-module project for Hexle, a 6-letter word guessing game, featuring:
- **API Layer**: Spring Boot (Java/Kotlin)
- **UI Layer**: React + Redux + Vite (TypeScript)

---

## Project Structure

```
wordle-clone/
├── build.gradle[.kts]         # Root Gradle build
├── settings.gradle[.kts]
├── api/                       # Spring Boot backend
│   ├── build.gradle[.kts]
│   └── src/main/java|kotlin/
├── ui/                        # React frontend
│   ├── package.json
│   ├── vite.config.ts
│   └── src/
└── README.md
```

---

## API Module (Spring Boot)

### Responsibilities
- Game logic (6-letter Wordle)
- Word selection (random, from dictionary)
- Guess validation and feedback
- Game state management (stateless or session-based)
- User statistics (optional)

### Endpoints
- `POST /api/game/start` – Start new game, returns gameId
- `POST /api/game/guess` – Submit guess, returns feedback
- `GET /api/game/status?gameId=...` – Get current game state
- `GET /api/game/stats?userId=...` – (Optional) User stats

### Data Models
- **GameState**: gameId, guesses, solution, status (in-progress, win, lose)
- **GuessRequest**: gameId, guess
- **GuessResponse**: feedback (per-letter: correct, present, absent), updated state

### Word List
- Use a curated list of 6-letter words (resource file)

### Tech Stack
- Spring Boot 3+
- Java 17+ or Kotlin
- JPA (optional, for persistence)
- REST controllers

---

## UI Module (React + Redux + Vite)

### Responsibilities
- Game board UI (6x6 grid)
- Virtual keyboard
- State management (Redux)
- API integration
- Animations for feedback
- Responsive design

### Pages/Components
- **GameBoard**: Displays guesses and feedback
- **Keyboard**: On-screen keyboard
- **Stats**: Shows user stats (optional)
- **Header/Footer**: Branding, controls

### State Shape (Redux)
- currentGame: { gameId, guesses, feedback, status }
- keyboard: { letterStates }
- stats: { gamesPlayed, winRate, streak }

### API Integration
- Use fetch/axios for REST calls
- Handle loading/error states

### Tech Stack
- React 18+
- Redux Toolkit
- TypeScript
- Vite
- CSS Modules or styled-components

---

## Build & Run
- `./gradlew build` – Build all modules
- `./gradlew :api:bootRun` – Run backend (default: port 8080)
- `cd ui && npm install && npm run dev` – Run frontend (default: port 3000, proxy API to 8080)

---

## Stretch Goals
- User authentication
- Persistent stats (DB)
- Dark mode
- Mobile PWA support
- Daily challenge mode

---

## Notes
- Keep modules decoupled (API/UI communicate via REST only)
- Use environment variables for config (ports, API URLs)
- Write unit and integration tests for both modules
