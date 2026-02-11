# Magnus Chess Android - Complete Setup Guide

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK 17** or higher
- **Android SDK** with minimum API level 24 (Android 7.0)
- **Git** for version control
- **Supabase Account** (free tier available at https://supabase.com)

## 🚀 Step-by-Step Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/Aryankaushik541/magnus-chess-android.git
cd magnus-chess-android
```

### Step 2: Set Up Supabase Backend

#### 2.1 Create a Supabase Project

1. Go to https://supabase.com and sign up/login
2. Click "New Project"
3. Fill in project details:
   - **Name**: Magnus Chess
   - **Database Password**: Choose a strong password
   - **Region**: Select closest to your location
4. Wait for project to be created (takes ~2 minutes)

#### 2.2 Create Database Tables

1. In your Supabase dashboard, go to **SQL Editor**
2. Click **New Query**
3. Copy and paste the following SQL:

```sql
-- Create users table
CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  email TEXT UNIQUE NOT NULL,
  username TEXT UNIQUE NOT NULL,
  rating INTEGER DEFAULT 1200,
  games_played INTEGER DEFAULT 0,
  games_won INTEGER DEFAULT 0,
  created_at TIMESTAMP DEFAULT NOW()
);

-- Create games table
CREATE TABLE games (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  white_player_id UUID REFERENCES users(id),
  black_player_id UUID REFERENCES users(id),
  moves TEXT DEFAULT '[]',
  result TEXT DEFAULT '*',
  time_control TEXT NOT NULL,
  white_time_remaining INTEGER,
  black_time_remaining INTEGER,
  current_turn TEXT DEFAULT 'white',
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);

-- Create indexes for better performance
CREATE INDEX idx_games_white_player ON games(white_player_id);
CREATE INDEX idx_games_black_player ON games(black_player_id);
CREATE INDEX idx_games_created_at ON games(created_at DESC);

-- Enable Row Level Security
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE games ENABLE ROW LEVEL SECURITY;

-- Create policies for users table
CREATE POLICY "Users can view their own profile"
  ON users FOR SELECT
  USING (auth.uid() = id);

CREATE POLICY "Users can update their own profile"
  ON users FOR UPDATE
  USING (auth.uid() = id);

CREATE POLICY "Anyone can create a user profile"
  ON users FOR INSERT
  WITH CHECK (true);

-- Create policies for games table
CREATE POLICY "Users can view their own games"
  ON games FOR SELECT
  USING (auth.uid() = white_player_id OR auth.uid() = black_player_id);

CREATE POLICY "Users can create games"
  ON games FOR INSERT
  WITH CHECK (auth.uid() = white_player_id);

CREATE POLICY "Players can update their games"
  ON games FOR UPDATE
  USING (auth.uid() = white_player_id OR auth.uid() = black_player_id);
```

4. Click **Run** to execute the SQL

#### 2.3 Get Your Supabase Credentials

1. In Supabase dashboard, go to **Settings** → **API**
2. Copy the following:
   - **Project URL** (looks like: `https://xxxxx.supabase.co`)
   - **anon public** key (under "Project API keys")

### Step 3: Configure the Android Project

#### 3.1 Create local.properties File

1. In the project root directory, create a file named `local.properties`
2. Add your Supabase credentials:

```properties
sdk.dir=/path/to/your/Android/sdk
supabase.url=YOUR_SUPABASE_PROJECT_URL
supabase.key=YOUR_SUPABASE_ANON_KEY
```

**Example:**
```properties
sdk.dir=/Users/username/Library/Android/sdk
supabase.url=https://abcdefghijk.supabase.co
supabase.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

⚠️ **Important**: Never commit `local.properties` to Git (it's already in .gitignore)

### Step 4: Open Project in Android Studio

1. Open **Android Studio**
2. Click **File** → **Open**
3. Navigate to the cloned repository folder
4. Click **OK**
5. Wait for Gradle sync to complete (may take a few minutes)

### Step 5: Resolve Dependencies

If you encounter any dependency issues:

1. Click **File** → **Sync Project with Gradle Files**
2. If prompted, update Gradle plugin
3. Accept any SDK licenses:
   ```bash
   cd ~/Library/Android/sdk/tools/bin
   ./sdkmanager --licenses
   ```

### Step 6: Build and Run

#### 6.1 Using an Emulator

1. Click **Tools** → **Device Manager**
2. Click **Create Device**
3. Select a device (e.g., Pixel 6)
4. Select system image (API 34 recommended)
5. Click **Finish**
6. Click the **Run** button (green play icon)
7. Select your emulator

#### 6.2 Using a Physical Device

1. Enable **Developer Options** on your Android device:
   - Go to **Settings** → **About Phone**
   - Tap **Build Number** 7 times
2. Enable **USB Debugging**:
   - Go to **Settings** → **Developer Options**
   - Enable **USB Debugging**
3. Connect device via USB
4. Click **Run** and select your device

### Step 7: Test the App

1. **Register a new account**:
   - Open the app
   - Click "Sign Up"
   - Enter email, username, and password
   - Click "Create Account"

2. **Start a game**:
   - Select a game mode (Quick, Rapid, Classical, or AI)
   - Play chess by tapping pieces and valid move squares

3. **Test features**:
   - Make moves
   - Check move history
   - Resign or offer draw
   - Sign out and sign back in

## 🔧 Troubleshooting

### Build Errors

**Error: "SDK location not found"**
- Solution: Ensure `sdk.dir` is set correctly in `local.properties`

**Error: "Supabase credentials not found"**
- Solution: Check that `supabase.url` and `supabase.key` are in `local.properties`

**Error: "Duplicate class found"**
- Solution: Clean and rebuild:
  ```bash
  ./gradlew clean
  ./gradlew build
  ```

### Runtime Errors

**Error: "Network error" or "Connection failed"**
- Check internet connection
- Verify Supabase URL is correct
- Ensure Supabase project is active

**Error: "Authentication failed"**
- Check Supabase Auth is enabled
- Verify email/password requirements
- Check Supabase logs in dashboard

**Error: "Database error"**
- Verify tables were created correctly
- Check Row Level Security policies
- Review Supabase logs

### Gradle Sync Issues

```bash
# Clear Gradle cache
./gradlew clean
rm -rf .gradle
rm -rf build

# Invalidate caches in Android Studio
File → Invalidate Caches → Invalidate and Restart
```

## 📱 App Features

### Authentication
- ✅ Email/password registration
- ✅ Secure login
- ✅ Session management
- ✅ User profiles

### Chess Gameplay
- ✅ Full chess rules implementation
- ✅ Legal move validation
- ✅ Check/checkmate detection
- ✅ Castling, en passant
- ✅ Pawn promotion
- ✅ Move history

### Game Modes
- ⚡ Quick (5 min)
- 🏃 Rapid (10 min)
- ♟️ Classical (30 min)
- 🤖 AI opponent (ready for integration)

### Backend Features
- 💾 Game state persistence
- 📊 Player statistics
- 🏆 Rating system
- 📜 Game history

## 🎨 Customization

### Change App Theme

Edit `app/src/main/java/com/magnuschess/ui/theme/Theme.kt`:

```kotlin
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0066CC),  // Change primary color
    secondary = Color(0xFF34C759), // Change secondary color
    // ... other colors
)
```

### Modify Time Controls

Edit `app/src/main/java/com/magnuschess/utils/Constants.kt`:

```kotlin
const val TIME_QUICK = 300      // 5 minutes
const val TIME_RAPID = 600      // 10 minutes
const val TIME_CLASSICAL = 1800 // 30 minutes
```

### Add New Game Modes

1. Add constant in `Constants.kt`
2. Update `HomeScreen.kt` to add new button
3. Handle in `GameViewModel.kt`

## 📚 Project Structure

```
magnus-chess-android/
├── app/
│   ├── src/main/
│   │   ├── java/com/magnuschess/
│   │   │   ├── chess/          # Chess engine
│   │   │   │   ├── engine/     # Game logic
│   │   │   │   └── model/      # Chess models
│   │   │   ├── data/           # Data layer
│   │   │   │   ├── api/        # Supabase client
│   │   │   │   ├── model/      # Data models
│   │   │   │   └── repository/ # Repositories
│   │   │   ├── di/             # Dependency injection
│   │   │   ├── ui/             # UI layer
│   │   │   │   ├── screens/    # Compose screens
│   │   │   │   ├── theme/      # App theme
│   │   │   │   └── navigation/ # Navigation
│   │   │   ├── viewmodel/      # ViewModels
│   │   │   └── utils/          # Utilities
│   │   ├── res/                # Resources
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── README.md
```

## 🚢 Deployment

### Generate Signed APK

1. **Create Keystore**:
   ```bash
   keytool -genkey -v -keystore magnus-chess.jks -keyalg RSA -keysize 2048 -validity 10000 -alias magnus-chess
   ```

2. **Configure signing** in `app/build.gradle`:
   ```gradle
   android {
       signingConfigs {
           release {
               storeFile file("magnus-chess.jks")
               storePassword "your-password"
               keyAlias "magnus-chess"
               keyPassword "your-password"
           }
       }
       buildTypes {
           release {
               signingConfig signingConfigs.release
           }
       }
   }
   ```

3. **Build APK**:
   ```bash
   ./gradlew assembleRelease
   ```

4. APK location: `app/build/outputs/apk/release/app-release.apk`

### Publish to Google Play

1. Create Google Play Developer account
2. Create new app in Play Console
3. Upload APK or AAB
4. Fill in store listing details
5. Submit for review

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

- **Issues**: https://github.com/Aryankaushik541/magnus-chess-android/issues
- **Discussions**: https://github.com/Aryankaushik541/magnus-chess-android/discussions
- **Email**: support@magnuschess.com

## 🎯 Next Steps

- [ ] Implement AI opponent using Stockfish
- [ ] Add online multiplayer with real-time sync
- [ ] Implement puzzle mode
- [ ] Add game analysis
- [ ] Create leaderboards
- [ ] Add achievements system
- [ ] Implement chat feature
- [ ] Add game variants (Chess960, etc.)

---

**Happy Coding! ♔**
