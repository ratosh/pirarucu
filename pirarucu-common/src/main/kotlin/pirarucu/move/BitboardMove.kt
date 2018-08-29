package pirarucu.move

import pirarucu.board.Bitboard
import pirarucu.board.Color
import pirarucu.board.File
import pirarucu.board.Rank
import pirarucu.board.Square
import pirarucu.game.GameConstants

object BitboardMove {

    const val NORTH = 8
    const val SOUTH = -NORTH

    const val EAST = 1
    const val WEST = -EAST

    val PAWN_FORWARD = arrayOf(NORTH, SOUTH)
    val DOUBLE_PAWN_FORWARD = arrayOf(NORTH * 2, SOUTH * 2)

    private val PAWN_ATTACK_STEP = intArrayOf(NORTH + WEST, NORTH + EAST)
    private val KNIGHT_MOVE_STEPS = intArrayOf(SOUTH * 2 + WEST, SOUTH * 2 + EAST, -10, -6, 6, 10, 15, 17)
    private const val BISHOP_MAGIC_SHIFT = 9
    private val BISHOP_MOVE_STEPS = intArrayOf(-9, -7, 7, 9)
    private const val ROOK_MAGIC_SHIFT = 12
    private val ROOK_MOVE_STEPS = intArrayOf(-8, -1, 1, 8)
    private val KING_MOVE_STEPS = intArrayOf(-9, -8, -7, -1, 1, 7, 8, 9)

    val PAWN_MOVES = Array(Color.SIZE) { LongArray(Square.SIZE) }
    val DOUBLE_PAWN_MOVES = Array(Color.SIZE) { LongArray(Square.SIZE) }
    val PAWN_ATTACKS = Array(Color.SIZE) { LongArray(Square.SIZE) }
    val KNIGHT_MOVES = LongArray(Square.SIZE)
    val BISHOP_PSEUDO_MOVES = LongArray(Square.SIZE)
    val ROOK_PSEUDO_MOVES = LongArray(Square.SIZE)
    val KING_MOVES = LongArray(Square.SIZE)

    val BETWEEN_BITBOARD = Array(Square.SIZE) { LongArray(Square.SIZE) }

    // Large overlapping attack table indexed using magic multiplication.
    private val MAGIC_ATTACKS = LongArray(Magic.SIZE)
    val PINNED_MOVE_MASK = Array(Square.SIZE) { LongArray(Square.SIZE) }
    val NEIGHBOURS = LongArray(Square.SIZE)

    init {
        populateBetween()
        populatePawnMoves()
        populatePawnAttacks()
        populateKnightMoves()
        populateBishopMoves()
        populateRookMoves()
        populateKingMoves()
        populatePinnedMask()
        populateNeighbours()
    }

    private fun slideBetween(square: Int, slideValue: IntArray, limit: Long): Long {
        var result = Bitboard.EMPTY
        for (slide in slideValue) {
            result = result or slideBetween(square, slide, limit)
        }
        return result
    }

    private fun slideBetween(square: Int, slideValue: Int, limit: Long): Long {
        var mask = Bitboard.EMPTY
        var newSquare = square
        var bitboard = Bitboard.getBitboard(newSquare)
        while (Square.isValid(newSquare) && limit and bitboard == Bitboard.EMPTY) {
            newSquare += slideValue
            bitboard = Bitboard.getBitboard(newSquare)
            if (limit and bitboard != Bitboard.EMPTY || !Square.isValid(newSquare)) {
                break
            }
            mask = mask or bitboard
        }
        return mask
    }

    private fun slideMove(square: Int, slideValue: IntArray, limit: Long): Long {
        var result = Bitboard.EMPTY
        for (slide in slideValue) {
            result = result or slideMove(square, slide, limit)
        }
        return result
    }

    private fun slideMove(square: Int, slideValue: Int, limit: Long): Long {
        var mask = Bitboard.EMPTY
        var newSquare = square
        do {
            val oldSquare = newSquare
            newSquare += slideValue
            if (!Square.isValid(newSquare) || Square.SQUARE_DISTANCE[oldSquare][newSquare] > 2) {
                break
            }
            mask = mask or Bitboard.getBitboard(newSquare)
        } while (limit and mask == Bitboard.EMPTY)
        return mask
    }

    private fun populateBetween() {
        val directionArray = arrayOf(7, 9, 1, 8)
        val borderArray = arrayOf(Bitboard.FILE_A or Bitboard.RANK_8,
            Bitboard.FILE_H or Bitboard.RANK_8,
            Bitboard.FILE_H,
            Bitboard.RANK_8)
        for (square1 in Square.A1 until Square.SIZE) {
            for (index in directionArray.indices) {
                val direction = directionArray[index]
                val border = borderArray[index]

                var newSquare = square1

                do {
                    newSquare += direction
                    if (!Square.isValid(newSquare)) {
                        break
                    }
                    val bitboard = Bitboard.getBitboard(newSquare)
                    val between = slideBetween(square1, direction, border or bitboard)
                    BETWEEN_BITBOARD[square1][newSquare] = between
                    BETWEEN_BITBOARD[newSquare][square1] = between
                } while (bitboard and border == Bitboard.EMPTY)
            }
        }
    }

    private fun populatePawnMoves() {
        for (square in Square.A1 until Square.SIZE) {
            PAWN_MOVES[Color.WHITE][square] = getPawnMove(Color.WHITE, square)
            PAWN_MOVES[Color.BLACK][square] = getPawnMove(Color.BLACK, square)
            DOUBLE_PAWN_MOVES[Color.WHITE][square] = getDoublePawnMove(Color.WHITE, square)
            DOUBLE_PAWN_MOVES[Color.BLACK][square] = getDoublePawnMove(Color.BLACK, square)
        }
    }

    private fun getPawnMove(color: Int, square: Int): Long {
        var result = Bitboard.EMPTY
        val pawnMove = PAWN_FORWARD[color]
        val forwardSquare = square + pawnMove
        if (Square.isValid(forwardSquare)) {
            result = result or Bitboard.getBitboard(forwardSquare)
        }
        return result
    }

    private fun getDoublePawnMove(color: Int, square: Int): Long {
        var result = Bitboard.EMPTY
        val bitboard = Bitboard.getBitboard(square)
        if (bitboard and Bitboard.DOUBLE_MOVEMENT_BITBOARD[color] != Bitboard.EMPTY) {
            val doublePawnMove = DOUBLE_PAWN_FORWARD[color]
            val forwardSquare = square + doublePawnMove
            if (Square.isValid(forwardSquare)) {
                result = result or Bitboard.getBitboard(forwardSquare)
            }
        }
        return result
    }

    private fun populatePawnAttacks() {
        for (square in 0 until Square.SIZE) {
            PAWN_ATTACKS[Color.WHITE][square] = getPawnAttack(Color.WHITE, square)
            PAWN_ATTACKS[Color.BLACK][square] = getPawnAttack(Color.BLACK, square)
        }
    }

    private fun getPawnAttack(color: Int, square: Int): Long {
        var result = Bitboard.EMPTY

        var possibleBitboard: Long = Bitboard.ALL
        val file = File.getFile(square)
        when (file) {
            File.FILE_A -> possibleBitboard = Bitboard.NOT_FILE_H
            File.FILE_H -> possibleBitboard = Bitboard.NOT_FILE_A
        }

        for (i in PAWN_ATTACK_STEP) {
            val attackSquare = square + i * GameConstants.COLOR_FACTOR[color]
            if (Square.isValid(attackSquare)) {
                result = result or Bitboard.getBitboard(attackSquare) and possibleBitboard
            }
        }
        return result
    }

    private fun populateKnightMoves() {
        for (square in 0 until Square.SIZE) {
            KNIGHT_MOVES[square] = getKnightMove(square)
        }
    }

    private fun getKnightMove(square: Int): Long {
        val file = File.getFile(square)
        val rank = Rank.getRank(square)

        val blockers: Long = Bitboard.ALL
        var possibleBitboard: Long = Bitboard.ALL

        when (file) {
            File.FILE_A -> possibleBitboard = Bitboard.NOT_FILE_H and Bitboard.NOT_FILE_G
            File.FILE_B -> possibleBitboard = Bitboard.NOT_FILE_H
            File.FILE_G -> possibleBitboard = Bitboard.NOT_FILE_A
            File.FILE_H -> possibleBitboard = Bitboard.NOT_FILE_A and Bitboard.NOT_FILE_B
        }

        when (rank) {
            Rank.RANK_1 -> possibleBitboard = possibleBitboard and Bitboard.NOT_RANK_8 and Bitboard.NOT_RANK_7
            Rank.RANK_2 -> possibleBitboard = possibleBitboard and Bitboard.NOT_RANK_8
            Rank.RANK_7 -> possibleBitboard = possibleBitboard and Bitboard.NOT_RANK_1
            Rank.RANK_8 -> possibleBitboard = possibleBitboard and Bitboard.NOT_RANK_1 and Bitboard.NOT_RANK_2
        }

        return slideMove(square, KNIGHT_MOVE_STEPS, blockers) and possibleBitboard
    }

    private fun populateBishopMoves() {
        for (square in Square.A1 until Square.SIZE) {
            initMagics(square, Magic.BISHOP[square], BISHOP_MAGIC_SHIFT, BISHOP_MOVE_STEPS)
            BISHOP_PSEUDO_MOVES[square] = bishopMoves(square, 0)
        }
    }

    private fun populateRookMoves() {
        for (square in Square.A1 until Square.SIZE) {
            initMagics(square, Magic.ROOK[square], ROOK_MAGIC_SHIFT, ROOK_MOVE_STEPS)
            ROOK_PSEUDO_MOVES[square] = rookMoves(square, 0)
        }
    }

    private fun initMagics(square: Int, magic: Magic, shift: Int, deltas: IntArray) {
        var subset: Long = 0
        do {
            val attack = slideMove(square, deltas, subset)
            val idx = (magic.factor * subset).ushr(Square.SIZE - shift).toInt() + magic.offset
            MAGIC_ATTACKS[idx] = attack

            subset = subset - magic.mask and magic.mask
        } while (subset != Bitboard.EMPTY)
    }

    private fun populateKingMoves() {
        for (square in Square.A1 until Square.SIZE) {
            KING_MOVES[square] = getKingMove(square)
        }
    }

    private fun getKingMove(square: Int): Long {
        val file = File.getFile(square)
        val rank = Rank.getRank(square)

        val blockers: Long = Bitboard.ALL
        var possibleBitboard: Long = Bitboard.ALL

        when (file) {
            File.FILE_A -> possibleBitboard = Bitboard.NOT_FILE_H
            File.FILE_H -> possibleBitboard = Bitboard.NOT_FILE_A
        }

        when (rank) {
            Rank.RANK_1 -> possibleBitboard = possibleBitboard and Bitboard.NOT_RANK_8
            Rank.RANK_8 -> possibleBitboard = possibleBitboard and Bitboard.NOT_RANK_1
        }

        return slideMove(square, KING_MOVE_STEPS, blockers) and possibleBitboard
    }

    private fun populatePinnedMask() {
        val directionArray = arrayOf(7, 9, 1, 8, -7, -9, -1, -8)
        val borderArray = arrayOf(Bitboard.FILE_A or Bitboard.RANK_8,
            Bitboard.FILE_H or Bitboard.RANK_8,
            Bitboard.FILE_H,
            Bitboard.RANK_8,
            Bitboard.FILE_H or Bitboard.RANK_1,
            Bitboard.FILE_A or Bitboard.RANK_1,
            Bitboard.FILE_A,
            Bitboard.RANK_1)
        for (square1 in Square.A1 until Square.SIZE) {
            for (index in directionArray.indices) {
                val direction = directionArray[index]
                val border = borderArray[index]

                val mask = slideMove(square1, direction, border)

                var newSquare = square1
                var bitboard = Bitboard.getBitboard(newSquare)

                while (bitboard and border == Bitboard.EMPTY) {
                    newSquare += direction
                    if (!Square.isValid(newSquare)) {
                        break
                    }
                    bitboard = Bitboard.getBitboard(newSquare)
                    PINNED_MOVE_MASK[square1][newSquare] = mask
                }
            }
        }
    }

    private fun populateNeighbours() {
        for (square in Square.A1 until Square.SIZE) {
            val file = File.getFile(square)
            var boundBitboard = Bitboard.ALL

            when (file) {
                File.FILE_H -> {
                    boundBitboard = boundBitboard and Bitboard.NOT_FILE_A
                }
                File.FILE_A -> {
                    boundBitboard = boundBitboard and Bitboard.NOT_FILE_H
                }
            }
            var possibleNeighbours = Bitboard.EMPTY
            val westSquare = square + WEST
            if (Square.isValid(westSquare)) {
                possibleNeighbours = Bitboard.getBitboard(westSquare)
            }
            val eastSquare = square + EAST
            if (Square.isValid(eastSquare)) {
                possibleNeighbours = possibleNeighbours or Bitboard.getBitboard(eastSquare)
            }
            NEIGHBOURS[square] = boundBitboard and possibleNeighbours
        }
    }

    fun bishopMoves(square: Int, occupied: Long): Long {
        val magic = Magic.BISHOP[square]
        return MAGIC_ATTACKS[(magic.factor * (occupied and magic.mask)).ushr(Square.SIZE -
            BISHOP_MAGIC_SHIFT).toInt() + magic.offset]
    }

    fun rookMoves(square: Int, occupied: Long): Long {
        val magic = Magic.ROOK[square]
        return MAGIC_ATTACKS[(magic.factor * (occupied and magic.mask)).ushr(Square.SIZE -
            ROOK_MAGIC_SHIFT).toInt() + magic.offset]
    }

    fun queenMoves(square: Int, occupied: Long): Long {
        return bishopMoves(square, occupied) xor rookMoves(square, occupied)
    }

    fun pawnAttacks(color: Int, bitboard: Long): Long {
        return if (color == Color.WHITE) {
            (bitboard shl 7 and Bitboard.NOT_FILE_H) or (bitboard shl 9 and Bitboard.NOT_FILE_A)
        } else {
            (bitboard ushr 7 and Bitboard.NOT_FILE_A) or (bitboard ushr 9 and Bitboard.NOT_FILE_H)
        }
    }

    fun pawnForward(color: Int, bitboard: Long): Long {
        return if (color == Color.WHITE) {
            (bitboard shl 8)
        } else {
            (bitboard ushr 8)
        }
    }
}
