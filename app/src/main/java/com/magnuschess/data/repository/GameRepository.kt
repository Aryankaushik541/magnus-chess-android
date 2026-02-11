package com.magnuschess.data.repository

import com.magnuschess.data.api.SupabaseClient
import com.magnuschess.data.model.CreateGameRequest
import com.magnuschess.data.model.Game
import com.magnuschess.data.model.UpdateGameRequest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor() {
    
    private val supabase = SupabaseClient.client
    
    suspend fun createGame(request: CreateGameRequest): Result<Game> {
        return withContext(Dispatchers.IO) {
            try {
                val game = supabase.from("games")
                    .insert(request)
                    .decodeSingle<Game>()
                
                Timber.d("Game created successfully: ${game.id}")
                Result.success(game)
            } catch (e: Exception) {
                Timber.e(e, "Create game failed")
                Result.failure(e)
            }
        }
    }
    
    suspend fun updateGame(gameId: String, request: UpdateGameRequest): Result<Game> {
        return withContext(Dispatchers.IO) {
            try {
                val game = supabase.from("games")
                    .update(request) {
                        filter {
                            eq("id", gameId)
                        }
                    }
                    .decodeSingle<Game>()
                
                Timber.d("Game updated successfully: $gameId")
                Result.success(game)
            } catch (e: Exception) {
                Timber.e(e, "Update game failed")
                Result.failure(e)
            }
        }
    }
    
    suspend fun getGame(gameId: String): Result<Game> {
        return withContext(Dispatchers.IO) {
            try {
                val game = supabase.from("games")
                    .select {
                        filter {
                            eq("id", gameId)
                        }
                    }
                    .decodeSingle<Game>()
                
                Timber.d("Game fetched successfully: $gameId")
                Result.success(game)
            } catch (e: Exception) {
                Timber.e(e, "Get game failed")
                Result.failure(e)
            }
        }
    }
    
    suspend fun getUserGames(userId: String): Result<List<Game>> {
        return withContext(Dispatchers.IO) {
            try {
                val games = supabase.from("games")
                    .select {
                        filter {
                            or {
                                eq("white_player_id", userId)
                                eq("black_player_id", userId)
                            }
                        }
                        order("created_at", ascending = false)
                    }
                    .decodeList<Game>()
                
                Timber.d("Fetched ${games.size} games for user: $userId")
                Result.success(games)
            } catch (e: Exception) {
                Timber.e(e, "Get user games failed")
                Result.failure(e)
            }
        }
    }
    
    suspend fun deleteGame(gameId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                supabase.from("games")
                    .delete {
                        filter {
                            eq("id", gameId)
                        }
                    }
                
                Timber.d("Game deleted successfully: $gameId")
                Result.success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "Delete game failed")
                Result.failure(e)
            }
        }
    }
}
