# ⚡ Quick Start Guide - Magnus Chess Android

Get up and running in **10 minutes**!

## 🎯 What You'll Build

A fully functional chess Android app with:
- ✅ User authentication (login/register)
- ✅ Complete chess gameplay
- ✅ Multiple game modes
- ✅ Backend integration
- ✅ Beautiful Material Design UI

## 📦 What's Included

```
✓ Complete Android project structure
✓ Chess engine with full rules
✓ Supabase backend integration
✓ Authentication system
✓ Game state management
✓ Modern Jetpack Compose UI
✓ MVVM architecture
✓ Dependency injection (Hilt)
```

## 🚀 5-Step Setup

### 1️⃣ Clone Repository (1 min)
```bash
git clone https://github.com/Aryankaushik541/magnus-chess-android.git
cd magnus-chess-android
```

### 2️⃣ Setup Supabase (3 min)

**Create Project:**
1. Go to [supabase.com](https://supabase.com)
2. Click "New Project"
3. Name it "Magnus Chess"
4. Wait for creation

**Create Tables:**
1. Go to SQL Editor
2. Run this SQL:

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

CREATE TABLE games (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  white_player_id UUID REFERENCES users(id),
  black_player_id UUID REFERENCES users(id),
  moves TEXT DEFAULT '[]',
  result TEXT DEFAULT '*',
  time_control TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT NOW()
);

ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE games ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view own profile" ON users FOR SELECT USING (auth.uid() = id);
CREATE POLICY "Users can create profile" ON users FOR INSERT WITH CHECK (true);
CREATE POLICY "Users can view own games" ON games FOR SELECT USING (auth.uid() = white_player_id OR auth.uid() = black_player_id);
CREATE POLICY "Users can create games" ON games FOR INSERT WITH CHECK (auth.uid() = white_player_id);
CREATE POLICY "Players can update games" ON games FOR UPDATE USING (auth.uid() = white_player_id OR auth.uid() = black_player_id);
```

**Get Credentials:**
1. Settings → API
2. Copy **Project URL** and **anon public key**

### 3️⃣ Configure Project (2 min)

Create `local.properties` in project root:

```properties
sdk.dir=/path/to/Android/sdk
supabase.url=YOUR_SUPABASE_URL
supabase.key=YOUR_SUPABASE_KEY
```

**Example:**
```properties
sdk.dir=/Users/username/Library/Android/sdk
supabase.url=https://abcdefg.supabase.co
supabase.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 4️⃣ Open in Android Studio (2 min)

1. Open Android Studio
2. File → Open → Select project folder
3. Wait for Gradle sync

### 5️⃣ Run the App (2 min)

1. Click Run button (▶️)
2. Select emulator or device
3. App launches!

## 🎮 Test the App

### First Time Setup
1. **Register**: Click "Sign Up" → Enter details → Create Account
2. **Start Game**: Select "Quick Game" (5 min)
3. **Play Chess**: Tap pieces to move

### Features to Try
- ✅ Make chess moves
- ✅ View move history
- ✅ Resign or draw
- ✅ Sign out and back in
- ✅ Try different game modes

## 📁 Project Structure

```
magnus-chess-android/
├── app/src/main/java/com/magnuschess/
│   ├── chess/              # Chess engine
│   │   ├── engine/         # Game logic
│   │   └── model/          # Chess models
│   ├── data/               # Data layer
│   │   ├── api/            # Supabase
│   │   ├── model/          # Data models
│   │   └── repository/     # Repositories
│   ├── ui/                 # UI layer
│   │   ├── screens/        # Compose screens
│   │   │   ├── auth/       # Login/Register
│   │   │   ├── home/       # Home screen
│   │   │   └── game/       # Game screen
│   │   ├── theme/          # App theme
│   │   └── navigation/     # Navigation
│   ├── viewmodel/          # ViewModels
│   └── di/                 # Dependency injection
└── README.md
```

## 🔑 Key Files

| File | Purpose |
|------|---------|
| `ChessBoard.kt` | Complete chess engine |
| `AuthViewModel.kt` | Authentication logic |
| `GameViewModel.kt` | Game state management |
| `LoginScreen.kt` | Login UI |
| `GameScreen.kt` | Chess board UI |
| `SupabaseClient.kt` | Backend connection |

## 🎨 Customization

### Change Colors
Edit `app/src/main/java/com/magnuschess/ui/theme/Theme.kt`:
```kotlin
primary = Color(0xFF0066CC),  // Your color
```

### Change Time Controls
Edit `app/src/main/java/com/magnuschess/utils/Constants.kt`:
```kotlin
const val TIME_QUICK = 300  // Seconds
```

### Add Game Mode
1. Add button in `HomeScreen.kt`
2. Handle in `GameViewModel.kt`

## 🐛 Common Issues

### "SDK location not found"
**Fix:** Set `sdk.dir` in `local.properties`

### "Supabase error"
**Fix:** Check URL and key in `local.properties`

### "Build failed"
**Fix:** 
```bash
./gradlew clean
./gradlew build
```

### "Network error"
**Fix:** Check internet and Supabase project is active

## 📚 Next Steps

### Learn More
- 📖 [Full Setup Guide](SETUP_GUIDE.md) - Detailed instructions
- 🏗️ [Architecture](ARCHITECTURE.md) - Technical details
- 📝 [README](README.md) - Project overview

### Enhance Your App
- [ ] Add AI opponent (Stockfish)
- [ ] Implement online multiplayer
- [ ] Add puzzle mode
- [ ] Create leaderboards
- [ ] Add game analysis
- [ ] Implement chat

### Deploy
- [ ] Generate signed APK
- [ ] Publish to Google Play
- [ ] Add analytics
- [ ] Monitor crashes

## 🆘 Get Help

- **Issues**: [GitHub Issues](https://github.com/Aryankaushik541/magnus-chess-android/issues)
- **Discussions**: [GitHub Discussions](https://github.com/Aryankaushik541/magnus-chess-android/discussions)

## 🎉 You're Ready!

You now have a fully functional chess app with:
- ✅ Authentication
- ✅ Chess gameplay
- ✅ Backend integration
- ✅ Modern UI

**Start playing and building!** ♔

---

**Made with ❤️ for chess enthusiasts**

Repository: https://github.com/Aryankaushik541/magnus-chess-android
