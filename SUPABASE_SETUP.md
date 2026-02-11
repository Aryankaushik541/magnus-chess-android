# Supabase Backend Setup - Magnus Chess

## 🔑 Your Supabase Credentials

**Project URL**: `https://ihqlwzdnniwkqjvlwtsi.supabase.co`

**Anon/Public Key** (for Android app):
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlocWx3emRubml3a3Fqdmx3dHNpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzA4MjY1MzIsImV4cCI6MjA4NjQwMjUzMn0.1iZ8756rn82nyTUZ5ioeNrTRIgLGLmo2qvz-6c4qrOA
```

⚠️ **IMPORTANT SECURITY NOTE**: 
- ✅ **USE** the anon/public key in your Android app (above)
- ❌ **NEVER USE** the service_role secret key in client-side code
- The service_role key bypasses Row Level Security and should only be used server-side

## 📝 Step 1: Create local.properties

In your project root directory, create a file named `local.properties` with:

```properties
# Android SDK location (update this path for your system)
sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk

# Supabase Configuration
supabase.url=https://ihqlwzdnniwkqjvlwtsi.supabase.co
supabase.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlocWx3emRubml3a3Fqdmx3dHNpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzA4MjY1MzIsImV4cCI6MjA4NjQwMjUzMn0.1iZ8756rn82nyTUZ5ioeNrTRIgLGLmo2qvz-6c4qrOA
```

**For Windows**:
```properties
sdk.dir=C\:\\Users\\YOUR_USERNAME\\AppData\\Local\\Android\\sdk
supabase.url=https://ihqlwzdnniwkqjvlwtsi.supabase.co
supabase.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlocWx3emRubml3a3Fqdmx3dHNpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzA4MjY1MzIsImV4cCI6MjA4NjQwMjUzMn0.1iZ8756rn82nyTUZ5ioeNrTRIgLGLmo2qvz-6c4qrOA
```

**For Linux**:
```properties
sdk.dir=/home/YOUR_USERNAME/Android/Sdk
supabase.url=https://ihqlwzdnniwkqjvlwtsi.supabase.co
supabase.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlocWx3emRubml3a3Fqdmx3dHNpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzA4MjY1MzIsImV4cCI6MjA4NjQwMjUzMn0.1iZ8756rn82nyTUZ5ioeNrTRIgLGLmo2qvz-6c4qrOA
```

## 🗄️ Step 2: Setup Database Tables

1. Go to your Supabase Dashboard: https://supabase.com/dashboard/project/ihqlwzdnniwkqjvlwtsi

2. Click on **SQL Editor** in the left sidebar

3. Click **New Query**

4. Copy and paste this SQL script:

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

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_games_white_player ON games(white_player_id);
CREATE INDEX IF NOT EXISTS idx_games_black_player ON games(black_player_id);
CREATE INDEX IF NOT EXISTS idx_games_created_at ON games(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_games_result ON games(result);

-- Enable Row Level Security
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE games ENABLE ROW LEVEL SECURITY;

-- Drop existing policies if they exist
DROP POLICY IF EXISTS "Users can view their own profile" ON users;
DROP POLICY IF EXISTS "Users can update their own profile" ON users;
DROP POLICY IF EXISTS "Anyone can create a user profile" ON users;
DROP POLICY IF EXISTS "Users can view their own games" ON games;
DROP POLICY IF EXISTS "Users can create games" ON games;
DROP POLICY IF EXISTS "Players can update their games" ON games;

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

-- Create a function to update the updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger to automatically update updated_at
DROP TRIGGER IF EXISTS update_games_updated_at ON games;
CREATE TRIGGER update_games_updated_at
    BEFORE UPDATE ON games
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Grant necessary permissions
GRANT USAGE ON SCHEMA public TO anon, authenticated;
GRANT ALL ON users TO anon, authenticated;
GRANT ALL ON games TO anon, authenticated;
```

5. Click **Run** (or press Ctrl/Cmd + Enter)

6. You should see "Success. No rows returned" message

## ✅ Step 3: Verify Setup

### Check Tables Created

1. In Supabase Dashboard, go to **Table Editor**
2. You should see two tables:
   - ✅ **users** (7 columns)
   - ✅ **games** (10 columns)

### Check Authentication Enabled

1. Go to **Authentication** → **Settings**
2. Ensure **Enable Email provider** is ON
3. Under **Email Auth**, ensure:
   - ✅ Enable email confirmations: OFF (for development)
   - ✅ Enable email signups: ON

### Test Connection (Optional)

You can test your Supabase connection using this simple script:

```bash
curl -X GET 'https://ihqlwzdnniwkqjvlwtsi.supabase.co/rest/v1/users' \
  -H "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlocWx3emRubml3a3Fqdmx3dHNpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzA4MjY1MzIsImV4cCI6MjA4NjQwMjUzMn0.1iZ8756rn82nyTUZ5ioeNrTRIgLGLmo2qvz-6c4qrOA" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlocWx3emRubml3a3Fqdmx3dHNpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzA4MjY1MzIsImV4cCI6MjA4NjQwMjUzMn0.1iZ8756rn82nyTUZ5ioeNrTRIgLGLmo2qvz-6c4qrOA"
```

Expected response: `[]` (empty array, since no users yet)

## 🔐 Step 4: Configure Authentication Settings

1. Go to **Authentication** → **Providers**
2. Ensure **Email** is enabled
3. Go to **Authentication** → **URL Configuration**
4. Set **Site URL**: `http://localhost` (for development)
5. Add **Redirect URLs**: `magnus-chess://callback` (for future deep linking)

## 📊 Step 5: View Your Database

### Using Table Editor

1. Go to **Table Editor**
2. Click on **users** or **games** table
3. You can manually add/edit/delete rows here

### Using SQL Editor

Run queries like:
```sql
-- View all users
SELECT * FROM users;

-- View all games
SELECT * FROM games;

-- Count users
SELECT COUNT(*) FROM users;
```

## 🚀 Step 6: Build and Run Your App

Now that Supabase is configured:

1. **Open Android Studio**
2. **Open the project**
3. **Sync Gradle** (File → Sync Project with Gradle Files)
4. **Run the app** (Click ▶️)

### First Time Usage

1. **Register a new account**:
   - Email: `test@example.com`
   - Username: `testplayer`
   - Password: `password123`

2. **Check Supabase**:
   - Go to **Authentication** → **Users**
   - You should see your new user!
   - Go to **Table Editor** → **users**
   - Your user profile should be there!

3. **Start a game**:
   - Select "Quick Game"
   - Make some moves
   - Go to **Table Editor** → **games**
   - Your game should be saved!

## 🔍 Monitoring and Debugging

### View Logs

1. Go to **Logs** in Supabase Dashboard
2. Select **API Logs** to see all requests
3. Select **Auth Logs** to see authentication events

### Check API Usage

1. Go to **Settings** → **API**
2. View your API usage statistics

### Database Performance

1. Go to **Database** → **Roles**
2. Monitor connection pooling
3. Check query performance

## 🛡️ Security Best Practices

### ✅ What's Secure

- ✅ Row Level Security (RLS) enabled
- ✅ Users can only see their own data
- ✅ Anon key used (not secret key)
- ✅ HTTPS for all connections

### ⚠️ For Production

Before deploying to production:

1. **Enable Email Confirmations**:
   - Authentication → Settings
   - Enable "Enable email confirmations"

2. **Set Password Requirements**:
   - Authentication → Settings
   - Set minimum password length (8+)

3. **Configure Rate Limiting**:
   - Settings → API
   - Set rate limits for auth endpoints

4. **Enable 2FA** (optional):
   - Authentication → Settings
   - Enable two-factor authentication

5. **Backup Database**:
   - Database → Backups
   - Enable automatic backups

## 📱 Testing Checklist

- [ ] User registration works
- [ ] User login works
- [ ] User profile is created in database
- [ ] Game creation works
- [ ] Game moves are saved
- [ ] Game history is retrieved
- [ ] User can only see their own games
- [ ] Logout works

## 🆘 Troubleshooting

### "Network Error" in App

**Check**:
- Internet connection
- Supabase URL is correct in `local.properties`
- Supabase project is active (not paused)

### "Authentication Failed"

**Check**:
- Email provider is enabled in Supabase
- Email confirmations are disabled (for development)
- Password meets requirements

### "Database Error"

**Check**:
- Tables were created successfully
- RLS policies are set up correctly
- User has proper permissions

### "Invalid API Key"

**Check**:
- Using anon key (not service_role key)
- No extra spaces in `local.properties`
- Key is complete and not truncated

## 📞 Support

- **Supabase Docs**: https://supabase.com/docs
- **Supabase Discord**: https://discord.supabase.com
- **Project Issues**: https://github.com/Aryankaushik541/magnus-chess-android/issues

## ✅ Setup Complete!

Your Supabase backend is now fully configured and ready to use with Magnus Chess!

**Next Steps**:
1. Build and run the app
2. Register a test account
3. Play some chess!
4. Check your data in Supabase Dashboard

---

**Your Supabase Project**: https://supabase.com/dashboard/project/ihqlwzdnniwkqjvlwtsi

**API Keys Summary**:
- **Anon Key** (for app): `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlocWx3emRubml3a3Fqdmx3dHNpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzA4MjY1MzIsImV4cCI6MjA4NjQwMjUzMn0.1iZ8756rn82nyTUZ5ioeNrTRIgLGLmo2qvz-6c4qrOA`
- **Service Role** (server only): Keep this secret and never use in client apps!
