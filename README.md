# Magnus Chess Android App

A comprehensive chess Android application similar to Magnus Carlsen's chess game with authentication, backend integration, and full chess gameplay features.

## Features

- 🎮 Full chess gameplay with legal move validation
- 👤 User authentication (Login/Register)
- 🔐 Secure backend integration with Supabase
- 🎨 Beautiful Material Design UI
- 🏆 Player ratings and statistics
- 📊 Game history tracking
- 🤖 AI opponent (Stockfish integration ready)
- ⏱️ Timer support for different game modes
- 💾 Save and resume games
- 🌐 Online multiplayer support

## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Backend**: Supabase (Authentication + Database)
- **Chess Engine**: Custom implementation with Stockfish integration
- **UI**: Material Design 3, Jetpack Compose
- **Dependencies**:
  - Retrofit for API calls
  - Room for local database
  - Coroutines for async operations
  - Hilt for dependency injection

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/magnuschess/
│   │   │   ├── data/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   └── api/
│   │   │   ├── ui/
│   │   │   │   ├── auth/
│   │   │   │   ├── game/
│   │   │   │   ├── home/
│   │   │   │   └── profile/
│   │   │   ├── viewmodel/
│   │   │   ├── chess/
│   │   │   │   ├── engine/
│   │   │   │   ├── board/
│   │   │   │   └── pieces/
│   │   │   └── utils/
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── build.gradle
└── build.gradle
```

## Setup Instructions

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17 or higher
- Android SDK 24 or higher
- Supabase account

### Step 1: Clone the Repository
```bash
git clone https://github.com/Aryankaushik541/magnus-chess-android.git
cd magnus-chess-android
```

### Step 2: Configure Supabase Backend

1. Create a Supabase project at https://supabase.com
2. Create the following tables:

**users table:**
```sql
CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  email TEXT UNIQUE NOT NULL,
  username TEXT UNIQUE NOT NULL,
  rating INTEGER DEFAULT 1200,
  games_played INTEGER DEFAULT 0,
  games_won INTEGER DEFAULT 0,
  created_at TIMESTAMP DEFAULT NOW()
);
```

**games table:**
```sql
CREATE TABLE games (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  white_player_id UUID REFERENCES users(id),
  black_player_id UUID REFERENCES users(id),
  moves TEXT,
  result TEXT,
  time_control TEXT,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);
```

3. Copy your Supabase URL and API Key
4. Create `local.properties` in the root directory:
```properties
supabase.url=YOUR_SUPABASE_URL
supabase.key=YOUR_SUPABASE_ANON_KEY
```

### Step 3: Build and Run

1. Open the project in Android Studio
2. Sync Gradle files
3. Run the app on an emulator or physical device

## Configuration

Edit `app/src/main/java/com/magnuschess/utils/Constants.kt` to customize:
- Game modes
- Time controls
- Rating system parameters
- UI themes

## Game Modes

- **Quick Game**: 5 minutes per player
- **Rapid**: 10 minutes per player
- **Classical**: 30 minutes per player
- **Custom**: Set your own time
- **AI Practice**: Play against computer

## Backend API Endpoints

The app uses Supabase for:
- User authentication (signup/login)
- User profile management
- Game storage and retrieval
- Real-time multiplayer (Supabase Realtime)

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Chess piece images from Wikimedia Commons
- Stockfish chess engine
- Material Design guidelines
- Supabase for backend infrastructure

## Support

For issues and questions, please open an issue on GitHub.

---
Made with ❤️ for chess enthusiasts
