package com.magnuschess.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("rating")
    val rating: Int = 1200,
    
    @SerializedName("games_played")
    val gamesPlayed: Int = 0,
    
    @SerializedName("games_won")
    val gamesWon: Int = 0,
    
    @SerializedName("created_at")
    val createdAt: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String
)

data class AuthResponse(
    @SerializedName("access_token")
    val accessToken: String,
    
    @SerializedName("user")
    val user: User
)
