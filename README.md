# Magnus Chess Android App

A comprehensive chess Android application similar to Magnus Carlsen's chess game with authentication, backend integration, and full chess gameplay features.

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![Language](https://img.shields.io/badge/language-Kotlin-purple.svg)

## ✨ Features

- 🎮 **Full Chess Gameplay** - Complete chess rules with legal move validation
- 👤 **User Authentication** - Secure login and registration with Supabase
- 🔐 **Backend Integration** - Real-time game state synchronization
- 🎨 **Beautiful UI** - Modern Material Design 3 interface
- 🏆 **Multiple Game Modes** - Quick, Rapid, Classical, and AI practice
- 📊 **Player Statistics** - Track ratings, games played, and wins
- 💾 **Game Persistence** - Save and resume games anytime
- ⏱️ **Timer Support** - Different time controls for each mode
- 📜 **Move History** - Complete game notation and replay

## 🚀 Quick Start (10 Minutes)

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17+
- Android SDK (API 24+)

### Step 1: Clone Repository
```bash
git clone https://github.com/Aryankaushik541/magnus-chess-android.git
cd magnus-chess-android
```

### Step 2: Configure Supabase

Your Supabase backend is **already configured**! Just run this SQL in your Supabase dashboard:

1. Go to: https://supabase.com/dashboard/project/ihqlwzdnniwkqjvlwtsi
2. Click **SQL Editor** → **New Query**
3. Copy and paste the SQL from `SUPABASE_SETUP.md`
4. Click **Run**

### Step 3: Setup Local Properties

Copy the template file:
```bash
cp local.properties.template local.properties
```

Then edit `local.properties` and update **only** the `sdk.dir` path:

**macOS:**
```properties
sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk
supabase.url=https://ihqlwzdnniwkqjvlwtsi.supabase.co
supabase.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlocWx3emRubml3a3Fqdmx3dHNpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzA4MjY1MzIsImV4cCI6MjA4NjQwMjUzMn0.1iZ8756rn82nyTUZ5ioeNrTRIgLGLmo2qvz-6c4qrOA
```

**Windows:**
```properties
sdk.dir=C\:\\Users\\YOUR_USERNAME\\AppData\\Local\\Android\\sdk
supabase.url=https://ihqlwzdnniwkqjvlwtsi.supabase.co
supabase.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlocWx3emRubml3a3Fqdmx3dHNpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzA4MjY1MzIsImV4cCI6MjA4NjQwMjUzMn0.1iZ8756rn82nyTUZ5ioeNrTRIgLGLmo2qvz-6c4qrOA
```

**Linux:**
```properties
sdk.dir=/home/YOUR_USERNAME/Android/Sdk
supabase.url=https://ihqlwzdnniwkqjvlwtsi.supabase.co
supabase.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlocWx3emRubml3a3Fqdmx3dHNpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzA4MjY1MzIsImV4cCI6MjA4NjQwMjUzMn0.1iZ8756rn82nyTUZ5ioeNrTRIgLGLmo2qvz-6c4qrOA
```

### Step 4: Build and Run
1. Open project in Android Studio
2. Wait for Gradle sync
3. Click Run ▶️
4. Select emulator or device

### Step 5: Test the App
1. Register a new account
2. Select a game mode
3. Play chess!

## 📱 Screenshots

```
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│  Login Screen   │  │   Home Screen   │  │   Game Screen   │
│                 │  │                 │  │                 │
│  ♔ Welcome     │  │  Quick Game ⚡  │  │  ♜ ♞ ♝ ♛ ♚ ♝ ♞ ♜ │
│                 │  │  Rapid 🏃       │  │  ♟ ♟ ♟ ♟ ♟ ♟ ♟ ♟ │
│  Email: ____    │  │  Classical ♟️   │  │                 │
│  Password: ____ │  │  Play AI 🤖     │  │                 │
│                 │  │                 │  │  ♙ ♙ ♙ ♙ ♙ ♙ ♙ ♙ │
│  [Sign In]      │  │  Rating: 1200   │  │  ♖ ♘ ♗ ♕ ♔ ♗ ♘ ♖ │
│  [Sign Up]      │  │  Games: 0       │  │                 │
└─────────────────┘  └─────────────────┘  └─────────────────┘
```

## 🏗️ Architecture

### Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose + Material Design 3
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt
- **Backend**: Supabase (Auth + PostgreSQL)
- **Async**: Coroutines + Flow
- **Navigation**: Jetpack Navigation Compose

### Project Structure
```
app/src/main/java/com/magnuschess/
├── chess/              # Chess engine
│   ├── engine/         # Game logic (ChessBoard)
│   └── model/          # Chess models (Piece, Move, Position)
├── data/               # Data layer
│   ├── api/            # Supabase client
│   ├── model/          # Data models (User, Game)
│   └── repository/     # Repositories (Auth, Game)
├── di/                 # Dependency injection
├── ui/                 # Presentation layer
│   ├── screens/        # Compose screens
│   │   ├── auth/       # Login, Register
│   │   ├── home/       # Home screen
│   │   └── game/       # Game screen
│   ├── theme/          # App theme
│   └── navigation/     # Navigation setup
├── viewmodel/          # ViewModels
└── utils/              # Utilities
```

## 🎮 Game Modes

| Mode | Time Control | Description |
|------|-------------|-------------|
| ⚡ Quick | 5 minutes | Fast-paced games |
| 🏃 Rapid | 10 minutes | Balanced gameplay |
| ♟️ Classical | 30 minutes | Traditional chess |
| 🤖 AI Practice | Unlimited | Play against computer |

## 🔐 Backend (Supabase)

### Database Schema

**Users Table:**
```sql
users (
  id UUID PRIMARY KEY,
  email TEXT UNIQUE,
  username TEXT UNIQUE,
  rating INTEGER DEFAULT 1200,
  games_played INTEGER,
  games_won INTEGER,
  created_at TIMESTAMP
)
```

**Games Table:**
```sql
games (
  id UUID PRIMARY KEY,
  white_player_id UUID,
  black_player_id UUID,
  moves TEXT,
  result TEXT,
  time_control TEXT,
  created_at TIMESTAMP
)
```

### Your Supabase Project
- **URL**: https://ihqlwzdnniwkqjvlwtsi.supabase.co
- **Dashboard**: https://supabase.com/dashboard/project/ihqlwzdnniwkqjvlwtsi
- **Anon Key**: Already configured in template files

## 📚 Documentation

- 📖 [Quick Start Guide](QUICK_START.md) - Get running in 10 minutes
- 🔧 [Setup Guide](SETUP_GUIDE.md) - Detailed setup instructions
- 🏗️ [Architecture](ARCHITECTURE.md) - Technical architecture details
- 🗄️ [Supabase Setup](SUPABASE_SETUP.md) - Backend configuration
- 🔨 [Build Instructions](BUILD_INSTRUCTIONS.md) - Step-by-step build guide

## 🛠️ Development

### Build Commands
```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test

# Run lint
./gradlew lint
```

### Code Style
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable names
- Add comments for complex logic
- Keep functions small and focused

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Generate coverage report
./gradlew jacocoTestReport
```

## 🚢 Deployment

### Generate Signed APK

1. Create keystore:
```bash
keytool -genkey -v -keystore magnus-chess.jks -keyalg RSA -keysize 2048 -validity 10000 -alias magnus-chess
```

2. Build release:
```bash
./gradlew assembleRelease
```

3. APK location: `app/build/outputs/apk/release/`

### Publish to Google Play

1. Create Google Play Developer account
2. Create app in Play Console
3. Upload APK/AAB
4. Fill store listing
5. Submit for review

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open Pull Request

### Contribution Guidelines
- Write clear commit messages
- Add tests for new features
- Update documentation
- Follow existing code style
- Test on multiple devices

## 🐛 Known Issues

- [ ] AI opponent not yet implemented (Stockfish integration pending)
- [ ] Online multiplayer in development
- [ ] Timer countdown needs optimization
- [ ] Game analysis feature planned

## 🗺️ Roadmap

### Version 1.1
- [ ] Stockfish AI integration
- [ ] Difficulty levels for AI
- [ ] Game analysis with best moves
- [ ] Opening book

### Version 1.2
- [ ] Online multiplayer with matchmaking
- [ ] Real-time game updates
- [ ] Chat system
- [ ] Friend system

### Version 2.0
- [ ] Puzzle mode
- [ ] Daily challenges
- [ ] Leaderboards
- [ ] Achievements
- [ ] Tournament mode
- [ ] Chess variants (Chess960, etc.)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2026 Magnus Chess

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction...
```

## 🙏 Acknowledgments

- Chess piece Unicode symbols
- [Supabase](https://supabase.com) for backend infrastructure
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for modern UI
- [Material Design](https://m3.material.io/) for design guidelines
- Chess programming community

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/Aryankaushik541/magnus-chess-android/issues)
- **Discussions**: [GitHub Discussions](https://github.com/Aryankaushik541/magnus-chess-android/discussions)
- **Email**: support@magnuschess.com

## 📊 Stats

- **Lines of Code**: ~3,500+
- **Files**: 30+
- **Languages**: Kotlin, XML, SQL
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## 🌟 Star History

If you find this project useful, please consider giving it a star ⭐

## 📱 Download

Coming soon to Google Play Store!

---

**Made with ❤️ for chess enthusiasts**

**Repository**: https://github.com/Aryankaushik541/magnus-chess-android

**Happy Coding! ♔**
