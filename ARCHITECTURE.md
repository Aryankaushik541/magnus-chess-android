# Magnus Chess - Architecture Documentation

## 🏗️ Architecture Overview

Magnus Chess follows **Clean Architecture** principles with **MVVM (Model-View-ViewModel)** pattern, ensuring separation of concerns, testability, and maintainability.

## 📐 Architecture Layers

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Compose    │  │  ViewModels  │  │  Navigation  │  │
│  │   Screens    │  │              │  │              │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                     Domain Layer                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ Chess Engine │  │  Use Cases   │  │   Models     │  │
│  │              │  │              │  │              │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                      Data Layer                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ Repositories │  │  API Client  │  │ Local Cache  │  │
│  │              │  │  (Supabase)  │  │   (Room)     │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
```

## 🎯 Core Components

### 1. Presentation Layer

#### Jetpack Compose UI
- **Modern declarative UI** framework
- **State-driven** rendering
- **Material Design 3** components

**Key Screens:**
- `LoginScreen` - User authentication
- `RegisterScreen` - New user registration
- `HomeScreen` - Game mode selection
- `GameScreen` - Chess gameplay interface

#### ViewModels
- **State management** using StateFlow
- **Business logic** coordination
- **Lifecycle-aware** components

**Key ViewModels:**
```kotlin
AuthViewModel
├── authState: StateFlow<AuthState>
├── currentUser: StateFlow<User?>
├── signIn(email, password)
├── signUp(email, password, username)
└── signOut()

GameViewModel
├── gameState: StateFlow<GameUiState>
├── chessBoard: StateFlow<ChessBoard>
├── selectedPosition: StateFlow<Position?>
├── validMoves: StateFlow<List<Position>>
├── createNewGame(userId, timeControl)
├── makeMove(from, to)
├── resign()
└── offerDraw()
```

### 2. Domain Layer

#### Chess Engine
**ChessBoard.kt** - Core game logic
```kotlin
class ChessBoard {
    // Board state management
    private val board: Array<Array<ChessPiece?>>
    private var currentTurn: PieceColor
    private val moveHistory: List<ChessMove>
    
    // Move validation
    fun isValidMove(from: Position, to: Position): Boolean
    fun makeMove(from: Position, to: Position): ChessMove?
    
    // Game state
    fun isInCheck(color: PieceColor): Boolean
    fun hasLegalMoves(color: PieceColor): Boolean
    fun getGameState(): GameState
}
```

**Features:**
- ✅ Complete chess rules implementation
- ✅ Legal move validation
- ✅ Check/checkmate detection
- ✅ Special moves (castling, en passant)
- ✅ Pawn promotion
- ✅ Move history tracking

#### Models
```kotlin
// Chess piece representation
data class ChessPiece(
    val type: PieceType,
    val color: PieceColor,
    val hasMoved: Boolean
)

// Board position
data class Position(
    val row: Int,
    val col: Int
)

// Chess move
data class ChessMove(
    val from: Position,
    val to: Position,
    val piece: ChessPiece,
    val capturedPiece: ChessPiece?,
    val isEnPassant: Boolean,
    val isCastling: Boolean,
    val promotionPiece: PieceType?
)
```

### 3. Data Layer

#### Repositories
**Single source of truth** for data operations

```kotlin
AuthRepository
├── signUp(email, password, username): Result<User>
├── signIn(email, password): Result<User>
├── signOut(): Result<Unit>
├── getCurrentUser(): Result<User?>
└── isUserLoggedIn(): Boolean

GameRepository
├── createGame(request): Result<Game>
├── updateGame(gameId, request): Result<Game>
├── getGame(gameId): Result<Game>
├── getUserGames(userId): Result<List<Game>>
└── deleteGame(gameId): Result<Unit>
```

#### Supabase Integration
```kotlin
object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        install(Auth)      // Authentication
        install(Postgrest) // Database
        install(Realtime)  // Real-time updates
    }
}
```

## 🔄 Data Flow

### Authentication Flow
```
User Input (LoginScreen)
    ↓
AuthViewModel.signIn()
    ↓
AuthRepository.signIn()
    ↓
Supabase Auth API
    ↓
AuthRepository (fetch user profile)
    ↓
Supabase Postgrest (users table)
    ↓
AuthViewModel (update state)
    ↓
UI Update (navigate to HomeScreen)
```

### Game Creation Flow
```
User Action (HomeScreen - select game mode)
    ↓
GameViewModel.createNewGame()
    ↓
GameRepository.createGame()
    ↓
Supabase Postgrest (insert into games table)
    ↓
GameViewModel (initialize ChessBoard)
    ↓
UI Update (navigate to GameScreen)
```

### Move Execution Flow
```
User Tap (GameScreen - select square)
    ↓
GameViewModel.selectSquare()
    ↓
ChessBoard.isValidMove()
    ↓
ChessBoard.makeMove()
    ↓
GameViewModel.saveGameState()
    ↓
GameRepository.updateGame()
    ↓
Supabase Postgrest (update games table)
    ↓
UI Update (board re-renders)
```

## 🔐 Security Architecture

### Authentication
- **Supabase Auth** for secure user management
- **JWT tokens** for session management
- **Row Level Security (RLS)** on database

### Data Protection
```sql
-- Users can only view their own profile
CREATE POLICY "Users can view their own profile"
  ON users FOR SELECT
  USING (auth.uid() = id);

-- Users can only view their own games
CREATE POLICY "Users can view their own games"
  ON games FOR SELECT
  USING (auth.uid() = white_player_id OR auth.uid() = black_player_id);
```

### Network Security
- **HTTPS** for all API calls
- **API key** stored in BuildConfig (not in code)
- **ProGuard** rules for release builds

## 🎨 UI Architecture

### Compose Navigation
```kotlin
NavHost(navController, startDestination) {
    composable(Screen.Login.route) { LoginScreen() }
    composable(Screen.Register.route) { RegisterScreen() }
    composable(Screen.Home.route) { HomeScreen() }
    composable(Screen.Game.route) { GameScreen() }
}
```

### State Management
- **StateFlow** for reactive state
- **Compose State** for UI state
- **ViewModel** as single source of truth

```kotlin
// ViewModel
private val _gameState = MutableStateFlow<GameUiState>(GameUiState.Idle)
val gameState: StateFlow<GameUiState> = _gameState.asStateFlow()

// Composable
val gameState by viewModel.gameState.collectAsState()
when (gameState) {
    is GameUiState.Loading -> LoadingIndicator()
    is GameUiState.Playing -> ChessBoard()
    is GameUiState.GameOver -> GameOverDialog()
}
```

## 🧪 Testing Strategy

### Unit Tests
```kotlin
// ViewModel tests
@Test
fun `signIn with valid credentials updates state to Success`()

// Repository tests
@Test
fun `createGame returns success with valid request`()

// Chess engine tests
@Test
fun `isValidMove returns true for legal pawn move`()
```

### Integration Tests
```kotlin
// API integration
@Test
fun `Supabase auth flow completes successfully`()

// Database operations
@Test
fun `Game creation and retrieval works end-to-end`()
```

### UI Tests
```kotlin
// Compose UI tests
@Test
fun `LoginScreen displays error on invalid credentials`()

@Test
fun `ChessBoard allows valid moves and rejects invalid ones`()
```

## 📊 Database Schema

### Users Table
```sql
CREATE TABLE users (
  id UUID PRIMARY KEY,
  email TEXT UNIQUE NOT NULL,
  username TEXT UNIQUE NOT NULL,
  rating INTEGER DEFAULT 1200,
  games_played INTEGER DEFAULT 0,
  games_won INTEGER DEFAULT 0,
  created_at TIMESTAMP DEFAULT NOW()
);
```

### Games Table
```sql
CREATE TABLE games (
  id UUID PRIMARY KEY,
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
```

## 🔧 Dependency Injection

### Hilt Setup
```kotlin
@HiltAndroidApp
class MagnusChessApplication : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity()

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel()
```

### Module Configuration
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository
    
    @Provides
    @Singleton
    fun provideGameRepository(): GameRepository
}
```

## 🚀 Performance Optimizations

### Compose Optimizations
- **Remember** for expensive calculations
- **LaunchedEffect** for side effects
- **derivedStateOf** for computed state
- **Immutable data classes** for stability

### Network Optimizations
- **Coroutines** for async operations
- **Flow** for reactive streams
- **Caching** with Room database
- **Pagination** for large datasets

### Chess Engine Optimizations
- **Bitboards** (future enhancement)
- **Move generation** caching
- **Transposition tables** (for AI)
- **Alpha-beta pruning** (for AI)

## 📱 Offline Support (Future)

```kotlin
// Room database for offline caching
@Database(entities = [User::class, Game::class])
abstract class ChessDatabase : RoomDatabase()

// Sync strategy
class SyncManager {
    suspend fun syncGames()
    suspend fun syncUserProfile()
    fun observeConnectivity()
}
```

## 🔮 Future Enhancements

### AI Integration
```kotlin
class StockfishEngine {
    suspend fun getBestMove(fen: String, depth: Int): ChessMove
    suspend fun evaluatePosition(fen: String): Float
}
```

### Real-time Multiplayer
```kotlin
class RealtimeGameManager {
    fun subscribeToGame(gameId: String)
    fun publishMove(gameId: String, move: ChessMove)
    fun observeOpponentMoves(): Flow<ChessMove>
}
```

### Analytics
```kotlin
class AnalyticsManager {
    fun trackGameStart(mode: String)
    fun trackGameEnd(result: String, duration: Long)
    fun trackUserAction(action: String)
}
```

## 📚 Resources

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Supabase Documentation](https://supabase.com/docs)
- [Chess Programming Wiki](https://www.chessprogramming.org/)
- [Material Design 3](https://m3.material.io/)

---

**Architecture designed for scalability, maintainability, and performance.**
