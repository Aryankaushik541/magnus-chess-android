package com.magnuschess.chess.model

// Piece Types
enum class PieceType {
    PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
}

// Piece Colors
enum class PieceColor {
    WHITE, BLACK;
    
    fun opposite(): PieceColor = if (this == WHITE) BLACK else WHITE
}

// Chess Piece
data class ChessPiece(
    val type: PieceType,
    val color: PieceColor,
    val hasMoved: Boolean = false
) {
    fun getSymbol(): String {
        val symbols = mapOf(
            PieceType.PAWN to if (color == PieceColor.WHITE) "♙" else "♟",
            PieceType.KNIGHT to if (color == PieceColor.WHITE) "♘" else "♞",
            PieceType.BISHOP to if (color == PieceColor.WHITE) "♗" else "♝",
            PieceType.ROOK to if (color == PieceColor.WHITE) "♖" else "♜",
            PieceType.QUEEN to if (color == PieceColor.WHITE) "♕" else "♛",
            PieceType.KING to if (color == PieceColor.WHITE) "♔" else "♚"
        )
        return symbols[type] ?: ""
    }
    
    fun getValue(): Int {
        return when (type) {
            PieceType.PAWN -> 1
            PieceType.KNIGHT -> 3
            PieceType.BISHOP -> 3
            PieceType.ROOK -> 5
            PieceType.QUEEN -> 9
            PieceType.KING -> 0
        }
    }
}

// Board Position
data class Position(
    val row: Int,
    val col: Int
) {
    fun isValid(): Boolean = row in 0..7 && col in 0..7
    
    fun toAlgebraic(): String {
        val file = ('a' + col).toString()
        val rank = (row + 1).toString()
        return "$file$rank"
    }
    
    companion object {
        fun fromAlgebraic(notation: String): Position? {
            if (notation.length != 2) return null
            val col = notation[0] - 'a'
            val row = notation[1] - '1'
            val pos = Position(row, col)
            return if (pos.isValid()) pos else null
        }
    }
}

// Chess Move
data class ChessMove(
    val from: Position,
    val to: Position,
    val piece: ChessPiece,
    val capturedPiece: ChessPiece? = null,
    val isEnPassant: Boolean = false,
    val isCastling: Boolean = false,
    val promotionPiece: PieceType? = null,
    val isCheck: Boolean = false,
    val isCheckmate: Boolean = false
) {
    fun toAlgebraic(): String {
        var notation = ""
        
        // Castling
        if (isCastling) {
            return if (to.col > from.col) "O-O" else "O-O-O"
        }
        
        // Piece symbol (except for pawns)
        if (piece.type != PieceType.PAWN) {
            notation += when (piece.type) {
                PieceType.KNIGHT -> "N"
                PieceType.BISHOP -> "B"
                PieceType.ROOK -> "R"
                PieceType.QUEEN -> "Q"
                PieceType.KING -> "K"
                else -> ""
            }
        }
        
        // Capture
        if (capturedPiece != null || isEnPassant) {
            if (piece.type == PieceType.PAWN) {
                notation += ('a' + from.col)
            }
            notation += "x"
        }
        
        // Destination
        notation += to.toAlgebraic()
        
        // Promotion
        if (promotionPiece != null) {
            notation += "=" + when (promotionPiece) {
                PieceType.QUEEN -> "Q"
                PieceType.ROOK -> "R"
                PieceType.BISHOP -> "B"
                PieceType.KNIGHT -> "N"
                else -> ""
            }
        }
        
        // Check/Checkmate
        if (isCheckmate) {
            notation += "#"
        } else if (isCheck) {
            notation += "+"
        }
        
        return notation
    }
}

// Game State
enum class GameState {
    ACTIVE,
    CHECK,
    CHECKMATE,
    STALEMATE,
    DRAW,
    RESIGNED
}
