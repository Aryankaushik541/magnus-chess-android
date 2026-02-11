# 🔑 Magnus Chess - Credentials & Configuration

## ⚠️ IMPORTANT SECURITY NOTICE

This document contains your Supabase credentials. **Keep this information secure!**

- ✅ **Safe to use**: Anon/Public key in client apps
- ❌ **NEVER use**: Service Role key in client apps (server-side only)
- 🔒 **Keep private**: Never commit credentials to public repositories

---

## 🗄️ Supabase Configuration

### Project Information

| Property | Value |
|----------|-------|
| **Project Name** | Magnus Chess |
| **Project URL** | `https://ihqlwzdnniwkqjvlwtsi.supabase.co` |
| **Project ID** | `ihqlwzdnniwkqjvlwtsi` |
| **Region** | Auto-selected |
| **Dashboard** | https://supabase.com/dashboard/project/ihqlwzdnniwkqjvlwtsi |

### API Keys

#### Anon/Public Key (✅ Use in Android App)
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlocWx3emRubml3a3Fqdmx3dHNpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzA4MjY1MzIsImV4cCI6MjA4NjQwMjUzMn0.1iZ8756rn82nyTUZ5ioeNrTRIgLGLmo2qvz-6c4qrOA
```

**Purpose**: Client-side authentication and API calls  
**Security**: Respects Row Level Security (RLS) policies  
**Usage**: Android app, web apps, mobile apps  
**Expires**: 2086-04-02 (10 years from creation)

#### Service Role Key (❌ Server-Side Only)
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlocWx3emRubml3a3Fqdmx3dHNpIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc3MDgyNjUzMiwiZXhwIjoyMDg2NDAyNTMyfQ.dchq8m3b14TPG-yFcaP_AdfjiIueq54uEm6TnJuxNbs
```

**Purpose**: Server-side operations, admin tasks  
**Security**: Bypasses Row Level Security (full access)  
**Usage**: Backend servers, admin scripts, migrations  
**⚠️ WARNING**: Never expose this key in client-side code!

---

## 📱 Android App Configuration

### local.properties File

Create this file in your project root:

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

---

## 🔗 API Endpoints

### REST API
```
Base URL: https://ihqlwzdnniwkqjvlwtsi.supabase.co/rest/v1/
```

**Examples:**
- Users: `GET /users`
- Games: `GET /games`
- Specific user: `GET /users?id=eq.{user_id}`

### Authentication API
```
Base URL: https://ihqlwzdnniwkqjvlwtsi.supabase.co/auth/v1/
```

**Examples:**
- Sign up: `POST /signup`
- Sign in: `POST /token?grant_type=password`
- Sign out: `POST /logout`

### Realtime API
```
WebSocket: wss://ihqlwzdnniwkqjvlwtsi.supabase.co/realtime/v1/websocket
```

---

## 🗃️ Database Information

### Connection Details

| Property | Value |
|----------|-------|
| **Host** | `db.ihqlwzdnniwkqjvlwtsi.supabase.co` |
| **Database** | `postgres` |
| **Port** | `5432` |
| **User** | `postgres` |

**Connection String:**
```
postgresql://postgres:[YOUR-PASSWORD]@db.ihqlwzdnniwkqjvlwtsi.supabase.co:5432/postgres
```

### Tables

**users**
- `id` (UUID, Primary Key)
- `email` (TEXT, Unique)
- `username` (TEXT, Unique)
- `rating` (INTEGER, Default: 1200)
- `games_played` (INTEGER, Default: 0)
- `games_won` (INTEGER, Default: 0)
- `created_at` (TIMESTAMP)

**games**
- `id` (UUID, Primary Key)
- `white_player_id` (UUID, Foreign Key → users.id)
- `black_player_id` (UUID, Foreign Key → users.id)
- `moves` (TEXT, JSON array)
- `result` (TEXT)
- `time_control` (TEXT)
- `white_time_remaining` (INTEGER)
- `black_time_remaining` (INTEGER)
- `current_turn` (TEXT)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

---

## 🔐 Security Configuration

### Row Level Security (RLS)

**Enabled on:**
- ✅ users table
- ✅ games table

**Policies:**

**Users Table:**
- Users can view their own profile
- Users can update their own profile
- Anyone can create a user profile (for registration)

**Games Table:**
- Users can view their own games (white or black player)
- Users can create games (as white player)
- Players can update their games

### Authentication Settings

| Setting | Value |
|---------|-------|
| **Email Provider** | Enabled |
| **Email Confirmations** | Disabled (for development) |
| **Email Signups** | Enabled |
| **Minimum Password Length** | 6 characters |
| **Site URL** | `http://localhost` |

---

## 🧪 Testing Credentials

For testing purposes, you can use:

**Test Account 1:**
- Email: `test@example.com`
- Username: `testplayer`
- Password: `password123`

**Test Account 2:**
- Email: `demo@example.com`
- Username: `demoplayer`
- Password: `demo123456`

---

## 📊 Usage Limits (Free Tier)

| Resource | Limit |
|----------|-------|
| **Database Size** | 500 MB |
| **Bandwidth** | 5 GB |
| **API Requests** | Unlimited |
| **Auth Users** | Unlimited |
| **Storage** | 1 GB |
| **Realtime Connections** | 200 concurrent |

---

## 🔄 Rotating Keys

If you need to rotate your API keys:

1. Go to **Settings** → **API** in Supabase Dashboard
2. Click **Reset** next to the key you want to rotate
3. Update `local.properties` with new key
4. Rebuild and redeploy your app

---

## 🆘 Emergency Access

If you lose access to your Supabase account:

1. Check your email for Supabase notifications
2. Use password reset at https://supabase.com/reset-password
3. Contact Supabase support: support@supabase.io

---

## 📝 Quick Reference

**Copy-Paste Ready Configuration:**

```properties
# For local.properties
supabase.url=https://ihqlwzdnniwkqjvlwtsi.supabase.co
supabase.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlocWx3emRubml3a3Fqdmx3dHNpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzA4MjY1MzIsImV4cCI6MjA4NjQwMjUzMn0.1iZ8756rn82nyTUZ5ioeNrTRIgLGLmo2qvz-6c4qrOA
```

**For cURL Testing:**
```bash
curl -X GET 'https://ihqlwzdnniwkqjvlwtsi.supabase.co/rest/v1/users' \
  -H "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlocWx3emRubml3a3Fqdmx3dHNpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzA4MjY1MzIsImV4cCI6MjA4NjQwMjUzMn0.1iZ8756rn82nyTUZ5ioeNrTRIgLGLmo2qvz-6c4qrOA" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlocWx3emRubml3a3Fqdmx3dHNpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzA4MjY1MzIsImV4cCI6MjA4NjQwMjUzMn0.1iZ8756rn82nyTUZ5ioeNrTRIgLGLmo2qvz-6c4qrOA"
```

---

## ⚠️ Security Reminders

1. ✅ **DO** use the anon key in your Android app
2. ❌ **DON'T** use the service role key in client apps
3. ✅ **DO** keep `local.properties` in `.gitignore`
4. ❌ **DON'T** commit credentials to version control
5. ✅ **DO** rotate keys if they're exposed
6. ❌ **DON'T** share service role key publicly

---

**Last Updated**: 2026-02-11  
**Project**: Magnus Chess Android  
**Repository**: https://github.com/Aryankaushik541/magnus-chess-android
