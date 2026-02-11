package com.magnuschess.utils

object Constants {
    
    // Game Modes
    const val GAME_MODE_QUICK = "quick"
    const val GAME_MODE_RAPID = "rapid"
    const val GAME_MODE_CLASSICAL = "classical"
    const val GAME_MODE_CUSTOM = "custom"
    const val GAME_MODE_AI = "ai"
    
    // Time Controls (in seconds)
    const val TIME_QUICK = 300 // 5 minutes
    const val TIME_RAPID = 600 // 10 minutes
    const val TIME_CLASSICAL = 1800 // 30 minutes
    
    // Rating System
    const val DEFAULT_RATING = 1200
    const val K_FACTOR = 32
    
    // Board Configuration
    const val BOARD_SIZE = 8
    
    // Piece Values
    const val PAWN_VALUE = 1
    const val KNIGHT_VALUE = 3
    const val BISHOP_VALUE = 3
    const val ROOK_VALUE = 5
    const val QUEEN_VALUE = 9
    const val KING_VALUE = 0
    
    // Game Results
    const val RESULT_WHITE_WIN = "1-0"
    const val RESULT_BLACK_WIN = "0-1"
    const val RESULT_DRAW = "1/2-1/2"
    const val RESULT_ONGOING = "*"
    
    // Preferences Keys
    const val PREF_USER_ID = "user_id"
    const val PREF_USER_EMAIL = "user_email"
    const val PREF_USER_NAME = "user_name"
    const val PREF_USER_RATING = "user_rating"
    const val PREF_IS_LOGGED_IN = "is_logged_in"
    const val PREF_AUTH_TOKEN = "auth_token"
    
    // Database
    const val DATABASE_NAME = "magnus_chess_db"
    const val DATABASE_VERSION = 1
    
    // API
    const val API_TIMEOUT = 30L // seconds
    
    // Chess Notation
    const val FILES = "abcdefgh"
    const val RANKS = "12345678"
}
