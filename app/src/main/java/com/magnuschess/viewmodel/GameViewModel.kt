package com.magnuschess.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.magnuschess.chess.engine.ChessBoard
import com.magnuschess.chess.model.*
import com.magnuschess.data.model.CreateGameRequest
import com.magnuschess.data.model.Game
import com.magnuschess.data.model.UpdateGameRequest
import com.magnuschess.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class GameUiState {
    object Idle : GameUiState()
    object Loading : GameUiState()
    data class Playing(val game: Game) : GameUiState()
    data class GameOver(val game: Game, val result: String) : GameUiState()
    data class Error(val message: String) : GameUiState()
}

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {
    
    private val _gameState = MutableStateFlow<GameUiState>(GameUiState.Idle)
    val gameState: StateFlow<GameUiState> = _gameState.asStateFlow()
    
    private val _chessBoard = MutableStateFlow(ChessBoard())
    val chessBoard: StateFlow<ChessBoard> = _chessBoard.asStateFlow()
    
    private val _selectedPosition = MutableStateFlow<Position?>(null)
    val selectedPosition: StateFlow<Position?> = _selectedPosition.asStateFlow()
    
    private val _validMoves = MutableStateFlow<List<Position>>(emptyList())
    val validMoves: StateFlow<List<Position>> = _validMoves.asStateFlow()
    
    private val _currentGame = MutableStateFlow<Game?>(null)
    val currentGame: StateFlow<Game?> = _currentGame.asStateFlow()
    
    private val _whiteTimeRemaining = MutableStateFlow(0)
    val whiteTimeRemaining: StateFlow<Int> = _whiteTimeRemaining.asStateFlow()
    
    private val _blackTimeRemaining = MutableStateFlow(0)
    val blackTimeRemaining: StateFlow<Int> = _blackTimeRemaining.asStateFlow()
    
    private val gson = Gson()
    
    fun createNewGame(userId: String, timeControl: String, opponentId: String? = null) {
        viewModelScope.launch {
            _gameState.value = GameUiState.Loading
            
            val request = CreateGameRequest(
                whitePlayerId = userId,
                blackPlayerId = opponentId,
                timeControl = timeControl,
                moves = "[]",
                result = "*"
            )
            
            gameRepository.createGame(request).fold(
                onSuccess = { game ->
                    _currentGame.value = game
                    _chessBoard.value = ChessBoard()
                    _gameState.value = GameUiState.Playing(game)
                    
                    // Initialize timers based on time control
                    val timeInSeconds = when (timeControl) {
                        "quick" -> 300
                        "rapid" -> 600
                        "classical" -> 1800
                        else -> 600
                    }
                    _whiteTimeRemaining.value = timeInSeconds
                    _blackTimeRemaining.value = timeInSeconds
                    
                    Timber.d("New game created: ${game.id}")
                },
                onFailure = { error ->
                    _gameState.value = GameUiState.Error(error.message ?: "Failed to create game")
                    Timber.e(error, "Failed to create game")
                }
            )
        }
    }
    
    fun loadGame(gameId: String) {
        viewModelScope.launch {
            _gameState.value = GameUiState.Loading
            
            gameRepository.getGame(gameId).fold(
                onSuccess = { game ->
                    _currentGame.value = game
                    
                    // Reconstruct board state from moves
                    val board = ChessBoard()
                    val moves = gson.fromJson(game.moves, Array<String>::class.java)
                    // TODO: Apply moves to board
                    
                    _chessBoard.value = board
                    
                    if (game.result == "*") {
                        _gameState.value = GameUiState.Playing(game)
                    } else {
                        _gameState.value = GameUiState.GameOver(game, game.result)
                    }
                    
                    Timber.d("Game loaded: ${game.id}")
                },
                onFailure = { error ->
                    _gameState.value = GameUiState.Error(error.message ?: "Failed to load game")
                    Timber.e(error, "Failed to load game")
                }
            )
        }
    }
    
    fun selectSquare(position: Position) {
        val board = _chessBoard.value
        val piece = board.getPiece(position)
        
        // If a piece is already selected
        val currentSelected = _selectedPosition.value
        if (currentSelected != null) {
            // Try to make a move
            if (_validMoves.value.contains(position)) {
                makeMove(currentSelected, position)
            } else if (piece?.color == board.getCurrentTurn()) {
                // Select new piece
                _selectedPosition.value = position
                calculateValidMoves(position)
            } else {
                // Deselect
                _selectedPosition.value = null
                _validMoves.value = emptyList()
            }
        } else {
            // Select piece if it's the current player's turn
            if (piece?.color == board.getCurrentTurn()) {
                _selectedPosition.value = position
                calculateValidMoves(position)
            }
        }
    }
    
    private fun calculateValidMoves(from: Position) {
        val board = _chessBoard.value
        val validMoves = mutableListOf<Position>()
        
        for (row in 0..7) {
            for (col in 0..7) {
                val to = Position(row, col)
                if (board.isValidMove(from, to)) {
                    validMoves.add(to)
                }
            }
        }
        
        _validMoves.value = validMoves
    }
    
    private fun makeMove(from: Position, to: Position) {
        val board = _chessBoard.value
        val move = board.makeMove(from, to)
        
        if (move != null) {
            // Clear selection
            _selectedPosition.value = null
            _validMoves.value = emptyList()
            
            // Update board state
            _chessBoard.value = board
            
            // Save move to backend
            saveGameState()
            
            // Check game state
            when (board.getGameState()) {
                GameState.CHECKMATE -> {
                    val result = if (board.getCurrentTurn() == PieceColor.WHITE) "0-1" else "1-0"
                    endGame(result)
                }
                GameState.STALEMATE, GameState.DRAW -> {
                    endGame("1/2-1/2")
                }
                else -> {
                    // Game continues
                }
            }
            
            Timber.d("Move made: ${move.toAlgebraic()}")
        }
    }
    
    private fun saveGameState() {
        viewModelScope.launch {
            val game = _currentGame.value ?: return@launch
            val board = _chessBoard.value
            
            val moves = board.getMoveHistory().map { it.toAlgebraic() }
            val movesJson = gson.toJson(moves)
            
            val updateRequest = UpdateGameRequest(
                moves = movesJson,
                result = game.result,
                whiteTimeRemaining = _whiteTimeRemaining.value,
                blackTimeRemaining = _blackTimeRemaining.value,
                currentTurn = if (board.getCurrentTurn() == PieceColor.WHITE) "white" else "black"
            )
            
            gameRepository.updateGame(game.id, updateRequest).fold(
                onSuccess = { updatedGame ->
                    _currentGame.value = updatedGame
                    Timber.d("Game state saved")
                },
                onFailure = { error ->
                    Timber.e(error, "Failed to save game state")
                }
            )
        }
    }
    
    private fun endGame(result: String) {
        viewModelScope.launch {
            val game = _currentGame.value ?: return@launch
            
            val moves = _chessBoard.value.getMoveHistory().map { it.toAlgebraic() }
            val movesJson = gson.toJson(moves)
            
            val updateRequest = UpdateGameRequest(
                moves = movesJson,
                result = result,
                whiteTimeRemaining = _whiteTimeRemaining.value,
                blackTimeRemaining = _blackTimeRemaining.value,
                currentTurn = if (_chessBoard.value.getCurrentTurn() == PieceColor.WHITE) "white" else "black"
            )
            
            gameRepository.updateGame(game.id, updateRequest).fold(
                onSuccess = { updatedGame ->
                    _currentGame.value = updatedGame
                    _gameState.value = GameUiState.GameOver(updatedGame, result)
                    Timber.d("Game ended: $result")
                },
                onFailure = { error ->
                    Timber.e(error, "Failed to end game")
                }
            )
        }
    }
    
    fun resign() {
        val board = _chessBoard.value
        val result = if (board.getCurrentTurn() == PieceColor.WHITE) "0-1" else "1-0"
        endGame(result)
    }
    
    fun offerDraw() {
        // TODO: Implement draw offer logic
        endGame("1/2-1/2")
    }
    
    fun resetGame() {
        _chessBoard.value = ChessBoard()
        _selectedPosition.value = null
        _validMoves.value = emptyList()
        _gameState.value = GameUiState.Idle
        _currentGame.value = null
    }
}
