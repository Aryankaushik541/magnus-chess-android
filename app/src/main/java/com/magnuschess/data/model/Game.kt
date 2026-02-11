package com.magnuschess.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "games")
data class Game(
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    
    @SerializedName("white_player_id")
    val whitePlayerId: String,
    
    @SerializedName("black_player_id")
    val blackPlayerId: String?,
    
    @SerializedName("moves")
    val moves: String, // PGN format or JSON array of moves
    
    @SerializedName("result")
    val result: String, // "1-0", "0-1", "1/2-1/2", "*"
    
    @SerializedName("time_control")
    val timeControl: String,
    
    @SerializedName("white_time_remaining")
    val whiteTimeRemaining: Int? = null,
    
    @SerializedName("black_time_remaining")
    val blackTimeRemaining: Int? = null,
    
    @SerializedName("current_turn")
    val currentTurn: String = "white", // "white" or "black"
    
    @SerializedName("created_at")
    val createdAt: String? = null,
    
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

data class CreateGameRequest(
    @SerializedName("white_player_id")
    val whitePlayerId: String,
    
    @SerializedName("black_player_id")
    val blackPlayerId: String?,
    
    @SerializedName("time_control")
    val timeControl: String,
    
    @SerializedName("moves")
    val moves: String = "[]",
    
    @SerializedName("result")
    val result: String = "*"
)

data class UpdateGameRequest(
    @SerializedName("moves")
    val moves: String,
    
    @SerializedName("result")
    val result: String,
    
    @SerializedName("white_time_remaining")
    val whiteTimeRemaining: Int?,
    
    @SerializedName("black_time_remaining")
    val blackTimeRemaining: Int?,
    
    @SerializedName("current_turn")
    val currentTurn: String
)
