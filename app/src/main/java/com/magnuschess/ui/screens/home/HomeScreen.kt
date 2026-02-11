package com.magnuschess.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.magnuschess.viewmodel.AuthViewModel
import com.magnuschess.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToGame: (String) -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    gameViewModel: GameViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Magnus Chess") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.signOut()
                        onLogout()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // User Profile Card
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = currentUser?.username ?: "Player",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Rating: ${currentUser?.rating ?: 1200}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Games: ${currentUser?.gamesPlayed ?: 0} | Wins: ${currentUser?.gamesWon ?: 0}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            item {
                Text(
                    text = "Quick Play",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                GameModeCard(
                    title = "⚡ Quick Game",
                    description = "5 minutes per player",
                    onClick = {
                        currentUser?.let { user ->
                            gameViewModel.createNewGame(user.id, "quick")
                        }
                    }
                )
            }
            
            item {
                GameModeCard(
                    title = "🏃 Rapid",
                    description = "10 minutes per player",
                    onClick = {
                        currentUser?.let { user ->
                            gameViewModel.createNewGame(user.id, "rapid")
                        }
                    }
                )
            }
            
            item {
                GameModeCard(
                    title = "♟️ Classical",
                    description = "30 minutes per player",
                    onClick = {
                        currentUser?.let { user ->
                            gameViewModel.createNewGame(user.id, "classical")
                        }
                    }
                )
            }
            
            item {
                GameModeCard(
                    title = "🤖 Play vs AI",
                    description = "Practice against computer",
                    onClick = {
                        currentUser?.let { user ->
                            gameViewModel.createNewGame(user.id, "ai")
                        }
                    }
                )
            }
        }
    }
    
    // Observe game creation
    val gameState by gameViewModel.gameState.collectAsState()
    LaunchedEffect(gameState) {
        if (gameState is com.magnuschess.viewmodel.GameUiState.Playing) {
            val game = (gameState as com.magnuschess.viewmodel.GameUiState.Playing).game
            onNavigateToGame(game.id)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameModeCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
