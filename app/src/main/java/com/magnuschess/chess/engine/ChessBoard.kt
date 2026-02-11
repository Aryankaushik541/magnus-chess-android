package com.magnuschess.chess.engine

import com.magnuschess.chess.model.*

class ChessBoard {
    
    private val board = Array(8) { Array<ChessPiece?>(8) { null } }
    private var currentTurn = PieceColor.WHITE
    private val moveHistory = mutableListOf<ChessMove>()
    private var enPassantTarget: Position? = null
    private var gameState = GameState.ACTIVE
    
    init {
        setupInitialPosition()
    }
    
    private fun setupInitialPosition() {
        // White pieces
        board[0][0] = ChessPiece(PieceType.ROOK, PieceColor.WHITE)
        board[0][1] = ChessPiece(PieceType.KNIGHT, PieceColor.WHITE)
        board[0][2] = ChessPiece(PieceType.BISHOP, PieceColor.WHITE)
        board[0][3] = ChessPiece(PieceType.QUEEN, PieceColor.WHITE)
        board[0][4] = ChessPiece(PieceType.KING, PieceColor.WHITE)
        board[0][5] = ChessPiece(PieceType.BISHOP, PieceColor.WHITE)
        board[0][6] = ChessPiece(PieceType.KNIGHT, PieceColor.WHITE)
        board[0][7] = ChessPiece(PieceType.ROOK, PieceColor.WHITE)
        
        for (col in 0..7) {
            board[1][col] = ChessPiece(PieceType.PAWN, PieceColor.WHITE)
        }
        
        // Black pieces
        board[7][0] = ChessPiece(PieceType.ROOK, PieceColor.BLACK)
        board[7][1] = ChessPiece(PieceType.KNIGHT, PieceColor.BLACK)
        board[7][2] = ChessPiece(PieceType.BISHOP, PieceColor.BLACK)
        board[7][3] = ChessPiece(PieceType.QUEEN, PieceColor.BLACK)
        board[7][4] = ChessPiece(PieceType.KING, PieceColor.BLACK)
        board[7][5] = ChessPiece(PieceType.BISHOP, PieceColor.BLACK)
        board[7][6] = ChessPiece(PieceType.KNIGHT, PieceColor.BLACK)
        board[7][7] = ChessPiece(PieceType.ROOK, PieceColor.BLACK)
        
        for (col in 0..7) {
            board[6][col] = ChessPiece(PieceType.PAWN, PieceColor.BLACK)
        }
    }
    
    fun getPiece(position: Position): ChessPiece? {
        if (!position.isValid()) return null
        return board[position.row][position.col]
    }
    
    fun setPiece(position: Position, piece: ChessPiece?) {
        if (position.isValid()) {
            board[position.row][position.col] = piece
        }
    }
    
    fun getCurrentTurn(): PieceColor = currentTurn
    
    fun getGameState(): GameState = gameState
    
    fun getMoveHistory(): List<ChessMove> = moveHistory.toList()
    
    fun isValidMove(from: Position, to: Position): Boolean {
        val piece = getPiece(from) ?: return false
        
        // Check if it's the correct player's turn
        if (piece.color != currentTurn) return false
        
        // Check if destination is valid
        if (!to.isValid()) return false
        
        // Check if destination has same color piece
        val targetPiece = getPiece(to)
        if (targetPiece?.color == piece.color) return false
        
        // Check piece-specific move rules
        if (!isPieceMoveLegal(from, to, piece)) return false
        
        // Check if move would put own king in check
        if (wouldBeInCheck(from, to, piece.color)) return false
        
        return true
    }
    
    private fun isPieceMoveLegal(from: Position, to: Position, piece: ChessPiece): Boolean {
        return when (piece.type) {
            PieceType.PAWN -> isValidPawnMove(from, to, piece.color)
            PieceType.KNIGHT -> isValidKnightMove(from, to)
            PieceType.BISHOP -> isValidBishopMove(from, to)
            PieceType.ROOK -> isValidRookMove(from, to)
            PieceType.QUEEN -> isValidQueenMove(from, to)
            PieceType.KING -> isValidKingMove(from, to, piece)
        }
    }
    
    private fun isValidPawnMove(from: Position, to: Position, color: PieceColor): Boolean {
        val direction = if (color == PieceColor.WHITE) 1 else -1
        val startRow = if (color == PieceColor.WHITE) 1 else 6
        val rowDiff = to.row - from.row
        val colDiff = Math.abs(to.col - from.col)
        
        // Forward move
        if (colDiff == 0) {
            if (rowDiff == direction && getPiece(to) == null) return true
            if (from.row == startRow && rowDiff == 2 * direction && 
                getPiece(to) == null && getPiece(Position(from.row + direction, from.col)) == null) {
                return true
            }
        }
        
        // Capture
        if (colDiff == 1 && rowDiff == direction) {
            val targetPiece = getPiece(to)
            if (targetPiece != null && targetPiece.color != color) return true
            
            // En passant
            if (to == enPassantTarget) return true
        }
        
        return false
    }
    
    private fun isValidKnightMove(from: Position, to: Position): Boolean {
        val rowDiff = Math.abs(to.row - from.row)
        val colDiff = Math.abs(to.col - from.col)
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)
    }
    
    private fun isValidBishopMove(from: Position, to: Position): Boolean {
        val rowDiff = Math.abs(to.row - from.row)
        val colDiff = Math.abs(to.col - from.col)
        if (rowDiff != colDiff) return false
        return isPathClear(from, to)
    }
    
    private fun isValidRookMove(from: Position, to: Position): Boolean {
        if (from.row != to.row && from.col != to.col) return false
        return isPathClear(from, to)
    }
    
    private fun isValidQueenMove(from: Position, to: Position): Boolean {
        return isValidBishopMove(from, to) || isValidRookMove(from, to)
    }
    
    private fun isValidKingMove(from: Position, to: Position, piece: ChessPiece): Boolean {
        val rowDiff = Math.abs(to.row - from.row)
        val colDiff = Math.abs(to.col - from.col)
        
        // Normal king move
        if (rowDiff <= 1 && colDiff <= 1) return true
        
        // Castling
        if (!piece.hasMoved && rowDiff == 0 && colDiff == 2) {
            return canCastle(from, to, piece.color)
        }
        
        return false
    }
    
    private fun isPathClear(from: Position, to: Position): Boolean {
        val rowStep = when {
            to.row > from.row -> 1
            to.row < from.row -> -1
            else -> 0
        }
        val colStep = when {
            to.col > from.col -> 1
            to.col < from.col -> -1
            else -> 0
        }
        
        var currentRow = from.row + rowStep
        var currentCol = from.col + colStep
        
        while (currentRow != to.row || currentCol != to.col) {
            if (getPiece(Position(currentRow, currentCol)) != null) return false
            currentRow += rowStep
            currentCol += colStep
        }
        
        return true
    }
    
    private fun canCastle(from: Position, to: Position, color: PieceColor): Boolean {
        // King must not have moved
        val king = getPiece(from) ?: return false
        if (king.hasMoved) return false
        
        // Determine rook position
        val rookCol = if (to.col > from.col) 7 else 0
        val rook = getPiece(Position(from.row, rookCol)) ?: return false
        
        // Rook must not have moved
        if (rook.hasMoved) return false
        
        // Path must be clear
        if (!isPathClear(from, Position(from.row, rookCol))) return false
        
        // King must not be in check
        if (isInCheck(color)) return false
        
        // King must not pass through check
        val step = if (to.col > from.col) 1 else -1
        for (col in from.col..(from.col + step) step step) {
            if (wouldBeInCheck(from, Position(from.row, col), color)) return false
        }
        
        return true
    }
    
    private fun wouldBeInCheck(from: Position, to: Position, color: PieceColor): Boolean {
        // Simulate the move
        val piece = getPiece(from)
        val capturedPiece = getPiece(to)
        
        setPiece(to, piece)
        setPiece(from, null)
        
        val inCheck = isInCheck(color)
        
        // Undo the move
        setPiece(from, piece)
        setPiece(to, capturedPiece)
        
        return inCheck
    }
    
    private fun isInCheck(color: PieceColor): Boolean {
        // Find king position
        var kingPos: Position? = null
        for (row in 0..7) {
            for (col in 0..7) {
                val piece = getPiece(Position(row, col))
                if (piece?.type == PieceType.KING && piece.color == color) {
                    kingPos = Position(row, col)
                    break
                }
            }
            if (kingPos != null) break
        }
        
        kingPos ?: return false
        
        // Check if any opponent piece can attack the king
        for (row in 0..7) {
            for (col in 0..7) {
                val piece = getPiece(Position(row, col))
                if (piece != null && piece.color != color) {
                    if (isPieceMoveLegal(Position(row, col), kingPos, piece)) {
                        return true
                    }
                }
            }
        }
        
        return false
    }
    
    fun makeMove(from: Position, to: Position, promotionPiece: PieceType? = null): ChessMove? {
        if (!isValidMove(from, to)) return null
        
        val piece = getPiece(from) ?: return null
        val capturedPiece = getPiece(to)
        
        // Handle en passant
        val isEnPassant = piece.type == PieceType.PAWN && to == enPassantTarget
        var enPassantCaptured: ChessPiece? = null
        if (isEnPassant) {
            val captureRow = if (piece.color == PieceColor.WHITE) to.row - 1 else to.row + 1
            enPassantCaptured = getPiece(Position(captureRow, to.col))
            setPiece(Position(captureRow, to.col), null)
        }
        
        // Handle castling
        val isCastling = piece.type == PieceType.KING && Math.abs(to.col - from.col) == 2
        if (isCastling) {
            val rookFromCol = if (to.col > from.col) 7 else 0
            val rookToCol = if (to.col > from.col) to.col - 1 else to.col + 1
            val rook = getPiece(Position(from.row, rookFromCol))
            setPiece(Position(from.row, rookToCol), rook?.copy(hasMoved = true))
            setPiece(Position(from.row, rookFromCol), null)
        }
        
        // Handle pawn promotion
        var finalPiece = piece.copy(hasMoved = true)
        if (piece.type == PieceType.PAWN && (to.row == 0 || to.row == 7)) {
            finalPiece = ChessPiece(promotionPiece ?: PieceType.QUEEN, piece.color, true)
        }
        
        // Make the move
        setPiece(to, finalPiece)
        setPiece(from, null)
        
        // Update en passant target
        enPassantTarget = if (piece.type == PieceType.PAWN && Math.abs(to.row - from.row) == 2) {
            Position((from.row + to.row) / 2, from.col)
        } else {
            null
        }
        
        // Check for check/checkmate
        val opponentColor = currentTurn.opposite()
        val isCheck = isInCheck(opponentColor)
        val isCheckmate = isCheck && !hasLegalMoves(opponentColor)
        
        // Update game state
        gameState = when {
            isCheckmate -> GameState.CHECKMATE
            isCheck -> GameState.CHECK
            !hasLegalMoves(opponentColor) -> GameState.STALEMATE
            else -> GameState.ACTIVE
        }
        
        // Create move object
        val move = ChessMove(
            from = from,
            to = to,
            piece = piece,
            capturedPiece = capturedPiece ?: enPassantCaptured,
            isEnPassant = isEnPassant,
            isCastling = isCastling,
            promotionPiece = if (finalPiece.type != piece.type) finalPiece.type else null,
            isCheck = isCheck,
            isCheckmate = isCheckmate
        )
        
        moveHistory.add(move)
        currentTurn = currentTurn.opposite()
        
        return move
    }
    
    private fun hasLegalMoves(color: PieceColor): Boolean {
        for (fromRow in 0..7) {
            for (fromCol in 0..7) {
                val piece = getPiece(Position(fromRow, fromCol))
                if (piece?.color == color) {
                    for (toRow in 0..7) {
                        for (toCol in 0..7) {
                            if (isValidMove(Position(fromRow, fromCol), Position(toRow, toCol))) {
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }
    
    fun getBoardState(): Array<Array<ChessPiece?>> {
        return board.map { it.clone() }.toTypedArray()
    }
    
    fun reset() {
        for (row in 0..7) {
            for (col in 0..7) {
                board[row][col] = null
            }
        }
        setupInitialPosition()
        currentTurn = PieceColor.WHITE
        moveHistory.clear()
        enPassantTarget = null
        gameState = GameState.ACTIVE
    }
}
