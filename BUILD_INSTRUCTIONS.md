# 🔨 Build Instructions - Magnus Chess Android

Complete step-by-step instructions to build and run the Magnus Chess Android app.

## ✅ Prerequisites Checklist

Before starting, ensure you have:

- [ ] **Android Studio** Hedgehog (2023.1.1) or later installed
- [ ] **JDK 17** or higher installed
- [ ] **Git** installed
- [ ] **Internet connection** for downloading dependencies
- [ ] **Android device** or emulator ready

## 📥 Step 1: Clone the Repository

Open your terminal and run:

```bash
# Clone the repository
git clone https://github.com/Aryankaushik541/magnus-chess-android.git

# Navigate to project directory
cd magnus-chess-android
```

**Verify**: You should see project files including `build.gradle`, `settings.gradle`, and `app/` folder.

## 🗄️ Step 2: Setup Supabase Database

### 2.1 Access Supabase Dashboard

1. Open your browser
2. Go to: https://supabase.com/dashboard/project/ihqlwzdnniwkqjvlwtsi
3. Login to your Supabase account

### 2.2 Create Database Tables

1. Click **SQL Editor** in the left sidebar
2. Click **New Query** button
3. Copy the entire SQL script below:

```sql
-- ============================================
-- Magnus Chess Database Setup
-- ============================================

-- Create users table
CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  email TEXT UNIQUE NOT NULL,
  username TEXT UNIQUE NOT NULL,
  rating INTEGER DEFAULT 1200,
  games_played INTEGER DEFAULT 0,
  games_won INTEGER DEFAULT 0,
  created_at TIMESTAMP DEFAULT NOW()
);

-- Create games table
CREATE TABLE IF NOT EXISTS games (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  white_player_id UUID REFERENCES users(id) ON DELETE CASCADE,
  black_player_id UUID REFERENCES users(id) ON DELETE CASCADE,
  moves TEXT DEFAULT '[]',
  result TEXT DEFAULT '*',
  time_control TEXT NOT NULL,
  white_time_remaining INTEGER,
  black_time_remaining INTEGER,
  current_turn TEXT DEFAULT 'white',
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_games_white_player ON games(white_player_id);
CREATE INDEX IF NOT EXISTS idx_games_black_player ON games(black_player_id);
CREATE INDEX IF NOT EXISTS idx_games_created_at ON games(created_at DESC);

-- Enable Row Level Security
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE games ENABLE ROW LEVEL SECURITY;

-- Drop existing policies
DROP POLICY IF EXISTS "Users can view their own profile" ON users;
DROP POLICY IF EXISTS "Users can update their own profile" ON users;
DROP POLICY IF EXISTS "Anyone can create a user profile" ON users;
DROP POLICY IF EXISTS "Users can view their own games" ON games;
DROP POLICY IF EXISTS "Users can create games" ON games;
DROP POLICY IF EXISTS "Players can update their games" ON games;

-- Create security policies
CREATE POLICY "Users can view their own profile"
  ON users FOR SELECT USING (auth.uid() = id);

CREATE POLICY "Users can update their own profile"
  ON users FOR UPDATE USING (auth.uid() = id);

CREATE POLICY "Anyone can create a user profile"
  ON users FOR INSERT WITH CHECK (true);

CREATE POLICY "Users can view their own games"
  ON games FOR SELECT USING (auth.uid() = white_player_id OR auth.uid() = black_player_id);

CREATE POLICY "Users can create games"
  ON games FOR INSERT WITH CHECK (auth.uid() = white_player_id);

CREATE POLICY "Players can update their games"
  ON games FOR UPDATE USING (auth.uid() = white_player_id OR auth.uid() = black_player_id);

-- Create update trigger
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

DROP TRIGGER IF EXISTS update_games_updated_at ON games;
CREATE TRIGGER update_games_updated_at
    BEFORE UPDATE ON games
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Grant permissions
GRANT USAGE ON SCHEMA public TO anon, authenticated;
GRANT ALL ON users TO anon, authenticated;
GRANT ALL ON games TO anon, authenticated;
```

4. Click **Run** button (or press Ctrl/Cmd + Enter)
5. Wait for "Success. No rows returned" message

### 2.3 Verify Tables Created

1. Click **Table Editor** in left sidebar
2. You should see:
   - ✅ **users** table (7 columns)
   - ✅ **games** table (10 columns)

### 2.4 Configure Authentication

1. Go to **Authentication** → **Providers**
2. Ensure **Email** is enabled (should be ON by default)
3. Go to **Authentication** → **Settings**
4. Under **Email Auth**:
   - Set "Enable email confirmations" to **OFF** (for development)
   - Set "Enable email signups" to **ON**

**Verify**: Email provider should show as enabled.

## ⚙️ Step 3: Configure Local Properties

### 3.1 Find Your Android SDK Path

**Option A - Using Android Studio:**
1. Open Android Studio (any project)
2. Go to: **Preferences/Settings** → **Appearance & Behavior** → **System Settings** → **Android SDK**
3. Copy the path shown at the top (e.g., `/Users/username/Library/Android/sdk`)

**Option B - Common Locations:**

**macOS:**
```
/Users/YOUR_USERNAME/Library/Android/sdk
```

**Windows:**
```
C:\Users\YOUR_USERNAME\AppData\Local\Android\sdk
```

**Linux:**
```
/home/YOUR_USERNAME/Android/Sdk
```

### 3.2 Create local.properties File

In the project root directory (same level as `build.gradle`), create a file named `local.properties`:

**macOS/Linux:**
```bash
touch local.properties
nano local.properties
```

**Windows:**
```bash
type nul > local.properties
notepad local.properties
```

### 3.3 Add Configuration

Paste this content into `local.properties` (update sdk.dir path):

**For macOS:**
```properties
sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk
supabase.url=https://ihqlwzdnniwkqjvlwtsi.supabase.co
supabase.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImloeWx3emRubml3a3Fqdmx3dHNpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk0NjI0NzAsImV4cCI6MjA1NTAzODQ3MH0.9LSW4VfRvSJKfCw2xsRQ_w_2agGai
```

**For Windows:**
```properties
sdk.dir=C\:\\Users\\YOUR_USERNAME\\AppData\\Local\\Android\\sdk
supabase.url=https://ihqlwzdnniwkqjvlwtsi.supabase.co
supabase.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImloeWx3emRubml3a3Fqdmx3dHNpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk0NjI0NzAsImV4cCI6MjA1NTAzODQ3MH0.9LSW4VfRvSJKfCw2xsRQ_w_2agGai
```

**For Linux:**
```properties
sdk.dir=/home/YOUR_USERNAME/Android/Sdk
supabase.url=https://ihqlwzdnniwkqjvlwtsi.supabase.co
supabase.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImloeWx3emRubml3a3Fqdmx3dHNpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk0NjI0NzAsImV4cCI6MjA1NTAzODQ3MH0.9LSW4VfRvSJKfCw2xsRQ_w_2agGai
```

**Important**: Replace `YOUR_USERNAME` with your actual username!

**Verify**: File should exist at `magnus-chess-android/local.properties`

## 🏗️ Step 4: Open Project in Android Studio

### 4.1 Launch Android Studio

1. Open Android Studio
2. If you see a welcome screen, click **Open**
3. If you have another project open, go to **File** → **Open**

### 4.2 Select Project

1. Navigate to the `magnus-chess-android` folder
2. Click **OK** or **Open**

### 4.3 Wait for Gradle Sync

Android Studio will automatically:
- ✅ Sync Gradle files
- ✅ Download dependencies
- ✅ Index project files

**This may take 3-5 minutes on first run.**

**Progress indicators:**
- Bottom right: "Gradle sync in progress..."
- Bottom status bar: Shows download progress

### 4.4 Resolve Any Issues

**If you see "SDK location not found":**
- Check `local.properties` exists
- Verify `sdk.dir` path is correct

**If you see "Gradle sync failed":**
- Click **File** → **Invalidate Caches** → **Invalidate and Restart**
- Wait for restart and re-sync

**If you see "Missing SDK components":**
- Click the notification
- Click **Install missing components**
- Wait for installation

**Verify**: Bottom status bar shows "Gradle sync finished" or "Ready"

## 📱 Step 5: Setup Emulator or Device

### Option A: Use Android Emulator

1. Click **Tools** → **Device Manager**
2. Click **Create Device** button
3. Select a device (recommended: **Pixel 6**)
4. Click **Next**
5. Select system image:
   - Recommended: **API 34** (Android 14)
   - Click **Download** if not installed
6. Click **Next**
7. Name it "Magnus Chess Emulator"
8. Click **Finish**

**Verify**: Emulator appears in Device Manager list

### Option B: Use Physical Device

1. **Enable Developer Options** on your Android device:
   - Go to **Settings** → **About Phone**
   - Tap **Build Number** 7 times
   - You'll see "You are now a developer!"

2. **Enable USB Debugging**:
   - Go to **Settings** → **Developer Options**
   - Enable **USB Debugging**
   - Enable **Install via USB** (if available)

3. **Connect Device**:
   - Connect phone to computer via USB
   - On phone, tap **Allow** when prompted for USB debugging
   - Select **File Transfer** mode

4. **Verify Connection**:
   - In Android Studio, check top toolbar
   - Your device should appear in device dropdown

## ▶️ Step 6: Build and Run

### 6.1 Select Run Configuration

1. In top toolbar, ensure **app** is selected in configuration dropdown
2. Select your emulator or device in device dropdown

### 6.2 Build the App

**Option A - Run Directly:**
1. Click the green **Run** button (▶️) in toolbar
2. Or press **Shift + F10** (Windows/Linux) or **Control + R** (macOS)

**Option B - Build First:**
1. Click **Build** → **Make Project**
2. Wait for build to complete
3. Then click **Run** button

### 6.3 Wait for Installation

Android Studio will:
1. ✅ Compile Kotlin code
2. ✅ Process resources
3. ✅ Build APK
4. ✅ Install on device/emulator
5. ✅ Launch app

**First build may take 2-5 minutes.**

**Progress indicators:**
- Bottom: "Building 'app'..."
- Bottom: "Installing APK..."
- Bottom: "Launching 'app'..."

**Verify**: App launches on device/emulator

## 🎮 Step 7: Test the Application

### 7.1 Register a New Account

1. App opens to **Login Screen**
2. Click **"Don't have an account? Sign Up"**
3. Enter details:
   - **Username**: `testplayer`
   - **Email**: `test@example.com`
   - **Password**: `password123`
   - **Confirm Password**: `password123`
4. Click **Create Account**

**Expected**: Navigates to Home Screen

### 7.2 Verify User in Supabase

1. Go to Supabase Dashboard
2. Click **Authentication** → **Users**
3. You should see your new user!
4. Click **Table Editor** → **users**
5. Your user profile should be there with rating 1200

### 7.3 Start a Game

1. On Home Screen, click **"⚡ Quick Game"**
2. App navigates to Game Screen
3. You should see a chess board

**Expected**: 
- Chess board with pieces in starting position
- "White to move" text at top
- Move history panel at bottom

### 7.4 Make Chess Moves

1. **Tap a white pawn** (bottom row, second from bottom)
2. Valid moves highlight in green
3. **Tap a highlighted square** to move
4. Piece moves to new position
5. Turn changes to "Black to move"

**Try these moves:**
- Move e2 pawn to e4
- Move e7 pawn to e5
- Move Nf3 (knight)

**Expected**:
- Pieces move correctly
- Move history updates
- Turn indicator changes

### 7.5 Verify Game Saved

1. Go to Supabase Dashboard
2. Click **Table Editor** → **games**
3. You should see your game!
4. Click on the row to see details
5. Check `moves` column has your moves

### 7.6 Test Other Features

**Sign Out:**
1. Go back to Home Screen
2. Click logout icon (top right)
3. Returns to Login Screen

**Sign In Again:**
1. Enter same email and password
2. Click Sign In
3. Should return to Home Screen

**Try Different Game Modes:**
- Quick Game (5 min)
- Rapid (10 min)
- Classical (30 min)
- Play vs AI

## ✅ Verification Checklist

After completing all steps, verify:

- [ ] Project opens in Android Studio without errors
- [ ] Gradle sync completes successfully
- [ ] App builds without errors
- [ ] App installs on device/emulator
- [ ] App launches successfully
- [ ] Can register new account
- [ ] User appears in Supabase Auth
- [ ] User profile created in database
- [ ] Can login with credentials
- [ ] Can start a new game
- [ ] Chess board displays correctly
- [ ] Can make valid moves
- [ ] Moves are saved to database
- [ ] Can sign out and sign back in

## 🐛 Troubleshooting

### Build Errors

**Error: "SDK location not found"**
```
Solution:
1. Check local.properties exists
2. Verify sdk.dir path is correct
3. Restart Android Studio
```

**Error: "Gradle sync failed"**
```
Solution:
1. File → Invalidate Caches → Invalidate and Restart
2. Delete .gradle folder in project root
3. Sync again
```

**Error: "Supabase credentials not found"**
```
Solution:
1. Check local.properties has supabase.url and supabase.key
2. Ensure no extra spaces or line breaks
3. Sync Gradle again
```

**Error: "Duplicate class found"**
```
Solution:
./gradlew clean
./gradlew build
```

### Runtime Errors

**Error: "Network error" or "Unable to connect"**
```
Solution:
1. Check internet connection
2. Verify Supabase URL is correct
3. Check Supabase project is active (not paused)
4. Try on different network
```

**Error: "Authentication failed"**
```
Solution:
1. Check email provider is enabled in Supabase
2. Verify email confirmations are OFF
3. Check password meets requirements (6+ chars)
4. View Supabase Auth logs for details
```

**Error: "Database error" or "Permission denied"**
```
Solution:
1. Verify SQL script ran successfully
2. Check RLS policies are created
3. View Supabase API logs
4. Ensure tables exist in Table Editor
```

**App crashes on launch:**
```
Solution:
1. Check Logcat in Android Studio
2. Look for stack trace
3. Verify all dependencies downloaded
4. Try clean rebuild
```

### Emulator Issues

**Emulator won't start:**
```
Solution:
1. Tools → Device Manager → Delete emulator
2. Create new emulator
3. Ensure virtualization is enabled in BIOS
4. Try different system image
```

**Emulator is slow:**
```
Solution:
1. Increase RAM allocation (Tools → Device Manager → Edit)
2. Enable hardware acceleration
3. Use x86_64 system image (not ARM)
4. Close other applications
```

## 📊 Build Output

After successful build, you'll find:

**Debug APK:**
```
app/build/outputs/apk/debug/app-debug.apk
```

**Release APK (after signing):**
```
app/build/outputs/apk/release/app-release.apk
```

## 🚀 Next Steps

Now that your app is running:

1. **Explore the code** - Check out different files
2. **Customize** - Change colors, add features
3. **Learn** - Read ARCHITECTURE.md
4. **Enhance** - Add AI opponent, multiplayer
5. **Deploy** - Build release APK for distribution

## 📚 Additional Resources

- [Android Studio User Guide](https://developer.android.com/studio/intro)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Jetpack Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)
- [Supabase Documentation](https://supabase.com/docs)

## 🆘 Get Help

If you encounter issues:

1. **Check Logcat** in Android Studio (View → Tool Windows → Logcat)
2. **Search Issues** on GitHub
3. **Create Issue** with error details
4. **Ask in Discussions** for general questions

**GitHub Repository**: https://github.com/Aryankaushik541/magnus-chess-android

---

**Congratulations! You've successfully built Magnus Chess! ♔**
