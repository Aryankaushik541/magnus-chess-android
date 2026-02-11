package com.magnuschess.data.repository

import com.magnuschess.data.api.SupabaseClient
import com.magnuschess.data.model.User
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {
    
    private val supabase = SupabaseClient.client
    
    suspend fun signUp(email: String, password: String, username: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                // Sign up with Supabase Auth
                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                
                val userId = supabase.auth.currentUserOrNull()?.id ?: throw Exception("User ID not found")
                
                // Create user profile in database
                val user = User(
                    id = userId,
                    email = email,
                    username = username,
                    rating = 1200,
                    gamesPlayed = 0,
                    gamesWon = 0
                )
                
                supabase.from("users").insert(user)
                
                Timber.d("User signed up successfully: $email")
                Result.success(user)
            } catch (e: Exception) {
                Timber.e(e, "Sign up failed")
                Result.failure(e)
            }
        }
    }
    
    suspend fun signIn(email: String, password: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                // Sign in with Supabase Auth
                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                
                val userId = supabase.auth.currentUserOrNull()?.id ?: throw Exception("User ID not found")
                
                // Fetch user profile from database
                val users = supabase.from("users")
                    .select {
                        filter {
                            eq("id", userId)
                        }
                    }
                    .decodeList<User>()
                
                val user = users.firstOrNull() ?: throw Exception("User profile not found")
                
                Timber.d("User signed in successfully: $email")
                Result.success(user)
            } catch (e: Exception) {
                Timber.e(e, "Sign in failed")
                Result.failure(e)
            }
        }
    }
    
    suspend fun signOut(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                supabase.auth.signOut()
                Timber.d("User signed out successfully")
                Result.success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "Sign out failed")
                Result.failure(e)
            }
        }
    }
    
    suspend fun getCurrentUser(): Result<User?> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = supabase.auth.currentUserOrNull()?.id
                
                if (userId == null) {
                    return@withContext Result.success(null)
                }
                
                val users = supabase.from("users")
                    .select {
                        filter {
                            eq("id", userId)
                        }
                    }
                    .decodeList<User>()
                
                val user = users.firstOrNull()
                Result.success(user)
            } catch (e: Exception) {
                Timber.e(e, "Get current user failed")
                Result.failure(e)
            }
        }
    }
    
    fun isUserLoggedIn(): Boolean {
        return supabase.auth.currentUserOrNull() != null
    }
}
