package com.magnuschess.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.magnuschess.chess.model.ChessPiece
import com.magnuschess.chess.model.PieceColor
import com.magnuschess.chess.model.Position
import com.magnuschess.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    gameId: String,
    onNavigateBack: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val chessBoard by viewModel.chessBoard.collectAsState()
    val selectedPosition by viewModel.selectedPosition.collectAsState()
    val validMoves by viewModel.validMoves.collectAsState()
    val gameState by viewModel.gameState.collectAsState()
    
    LaunchedEffect(gameId) {
        if (gameId != "new") {
            viewModel.loadGame(gameId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chess Game") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.resign() }) {
                        Text("Resign")
                    }
                    TextButton(onClick = { viewModel.offerDraw() }) {
                        Text("Draw")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Game info
            Text(
                text = when (chessBoard.getCurrentTurn()) {
                    PieceColor.WHITE -> "White to move"
                    PieceColor.BLACK -> "Black to move"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Chess Board
            ChessBoardView(
                board = chessBoard.getBoardState(),
                selectedPosition = selectedPosition,
                validMoves = validMoves,
                onSquareClick = { position ->
                    viewModel.selectSquare(position)
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Move history
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Move History",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn {
                        items(chessBoard.getMoveHistory()) { move ->
                            Text(
                                text = move.toAlgebraic(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Game over dialog
    if (gameState is com.magnuschess.viewmodel.GameUiState.GameOver) {
        val result = (gameState as com.magnuschess.viewmodel.GameUiState.GameOver).result
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Game Over") },
            text = {
                Text(
                    when (result) {
                        "1-0" -> "White wins!"
                        "0-1" -> "Black wins!"
                        "1/2-1/2" -> "Draw!"
                        else -> "Game ended"
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetGame()
                    onNavigateBack()
                }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun ChessBoardView(
    board: Array<Array<ChessPiece?>>,
    selectedPosition: Position?,
    validMoves: List<Position>,
    onSquareClick: (Position) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .border(2.dp, MaterialTheme.colorScheme.outline)
    ) {
        for (row in 7 downTo 0) {
            Row(modifier = Modifier.weight(1f)) {
                for (col in 0..7) {
                    val position = Position(row, col)
                    val piece = board[row][col]
                    val isSelected = position == selectedPosition
                    val isValidMove = validMoves.contains(position)
                    val isLightSquare = (row + col) % 2 == 0
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                when {
                                    isSelected -> Color(0xFF7FC8F8)
                                    isValidMove -> Color(0xFFAAD576)
                                    isLightSquare -> Color(0xFFF0D9B5)
                                    else -> Color(0xFFB58863)
                                }
                            )
                            .clickable { onSquareClick(position) },
                        contentAlignment = Alignment.Center
                    ) {
                        piece?.let {
                            Text(
                                text = it.getSymbol(),
                                fontSize = 40.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
