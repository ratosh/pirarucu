package pirarucu.board

data class BoardState(var zobristKey: Long, var pawnZobristKey: Long, var epSquare: Int,
    var castlingRights: Int, var rule50: Int, var lastMove: Int, val previousState: BoardState?)