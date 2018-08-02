package pirarucu.tuning

import pirarucu.board.Color
import pirarucu.board.File
import pirarucu.board.Piece
import pirarucu.board.Rank
import pirarucu.board.Square
import pirarucu.game.GameConstants
import pirarucu.util.SplitValue
import kotlin.math.min

object TunableConstants {
    val RAZOR_MARGIN = intArrayOf(0, 400)
    val FUTILITY_CHILD_MARGIN = intArrayOf(0, 120, 180, 250, 330, 420, 520)
    val FUTILITY_PARENT_MARGIN = intArrayOf(0, 100, 200, 310, 430, 550, 660)

    val TEMPO_TUNING = intArrayOf(23, 13)
    val TEMPO = IntArray(Color.SIZE)

    val PHASE_PIECE_VALUE = intArrayOf(0, 0, 9, 10, 20, 40, 0)

    var PHASE_MAX = PHASE_PIECE_VALUE[Piece.PAWN] * 16 +
        PHASE_PIECE_VALUE[Piece.KNIGHT] * 4 +
        PHASE_PIECE_VALUE[Piece.BISHOP] * 4 +
        PHASE_PIECE_VALUE[Piece.ROOK] * 4 +
        PHASE_PIECE_VALUE[Piece.QUEEN] * 2

    val MATERIAL_SCORE_MG = intArrayOf(0, 85, 475, 490, 670, 1425)
    val MATERIAL_SCORE_EG = intArrayOf(0, 115, 325, 355, 615, 1065)
    val MATERIAL_SCORE = IntArray(Piece.SIZE)

    val QS_FUTILITY_VALUE = intArrayOf(0,
        min(MATERIAL_SCORE_MG[1], MATERIAL_SCORE_EG[1]),
        min(MATERIAL_SCORE_MG[2], MATERIAL_SCORE_EG[2]),
        min(MATERIAL_SCORE_MG[3], MATERIAL_SCORE_EG[3]),
        min(MATERIAL_SCORE_MG[4], MATERIAL_SCORE_EG[4]),
        min(MATERIAL_SCORE_MG[5], MATERIAL_SCORE_EG[5]))

    val SEE_VALUE = intArrayOf(0, 100, 325, 330, 550, 900, 10000)

    val PSQT_MG = arrayOf(
        intArrayOf(
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0
        ),
        intArrayOf(
            0, 0, 0, 0, -14, 63, 28, 68, 16, 42, 69, 88, 2, 15, 20, 27, -8, -11, 22, 25, -8, -8, 1, 4, -3, 6, 4, 5, 0, 0, 0, 0
        ),
        intArrayOf(
            -210, -140, -140, -25, -95, -55, 40, -18, -20, 40, 30, 40, 5, 15, 15, 25, -5, 15, 20, 20, -20, 8, 15, 25, -15, -20, 5, 18, -65, -5, -23, 0
        ),
        intArrayOf(
            -45, -55, -145, -125, -75, 0, -15, -35, 5, 30, 40, 25, -15, 5, 13, 35, -5, 5, 0, 30, -5, 10, 20, 10, 5, 30, 15, 10, -25, -5, -5, 0
        ),
        intArrayOf(
            0, 25, -10, 45, 5, 18, 75, 65, -15, 20, 13, 10, -35, -25, 15, 20, -40, -10, -5, 5, -40, -10, 5, 0, -50, 0, 5, 15, -10, -10, 15, 30
        ),
        intArrayOf(
            -5, -5, -5, 33, -5, -35, 10, -40, 25, 15, 10, 0, -3, -15, -15, -20, 10, -5, 10, 0, 10, 35, 20, 25, 10, 28, 45, 50, 30, 35, 40, 50
        ),
        intArrayOf(
            -83, 134, 34, 39, 43, 62, 42, 86, 32, 138, 148, 81, -36, 3, 36, 27, -88, -31, -57, -51, -47, -13, -50, -35, 18, 8, -53, -33, 27, 37, -24, 30
        )
    )

    val PSQT_EG = arrayOf(
        intArrayOf(
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0
        ),
        intArrayOf(
            0, 0, 0, 0, 49, 19, -10, -38, 27, 13, -15, -52, 5, -4, -12, -27, -11, -7, -23, -29, -17, -18, -15, -16, -13, -17, -8, -11, 0, 0, 0, 0
        ),
        intArrayOf(
            -45, -35, -5, -25, -20, -5, -30, 0, -30, -20, 5, 5, -10, 0, 20, 25, -10, -5, 15, 20, -15, -15, -5, 10, -30, -10, -10, -5, -15, -35, -10, -5
        ),
        intArrayOf(
            -15, -10, 5, 10, 0, -10, 0, -5, -10, -13, -10, -15, 0, -10, 0, 0, -10, -10, 0, 0, -10, -10, -5, 0, -20, -20, -10, -10, -10, -5, -5, -5
        ),
        intArrayOf(
            15, 10, 20, 10, 15, 15, 0, 0, 5, 5, 5, 5, 15, 10, 10, 0, 5, 5, 3, 0, 0, 0, -10, -5, 0, -15, -10, -10, -15, -5, -10, -15
        ),
        intArrayOf(
            -5, 18, 25, 5, -15, 15, 20, 60, -20, -5, 5, 40, 10, 35, 20, 45, -10, 25, 5, 25, -5, -35, 0, -15, -35, -50, -45, -45, -63, -65, -60, -50
        ),
        intArrayOf(
            -42, -44, -8, -29, -13, 10, 26, 6, 4, 18, 22, 11, 5, 33, 31, 31, 2, 17, 41, 42, 0, 13, 31, 36, -26, -6, 23, 22, -68, -41, -11, -26
        )
    )

    val PSQT = Array(Piece.SIZE) { IntArray(Square.SIZE) }

    val MOBILITY_MG = arrayOf(
        intArrayOf(),
        intArrayOf(),
        intArrayOf(-36, -26, -16, -10, 2, 5, 16, 18, 42),
        intArrayOf(-32, -23, -5, 4, 7, 13, 17, 18, 19, 23, 41, 64, -22, 64),
        intArrayOf(-72, -57, -49, -41, -39, -27, -20, -8, 3, 15, 24, 35, 42, 40, 50),
        intArrayOf(8, -4, 3, 0, 10, 12, 9, 10, 9, 10, 13, 13, 12, 22, 23, 16, 16, 42, 25, 61, 46, 95, 62, 136, 75, 213, 49, 194),
        intArrayOf())

    val MOBILITY_EG = arrayOf(
        intArrayOf(),
        intArrayOf(),
        intArrayOf(-24, -24, 4, 15, 16, 23, 19, 17, 0),
        intArrayOf(-38, -38, -17, -7, 4, 8, 12, 16, 18, 17, 8, 6, 36, 6),
        intArrayOf(-20, -25, 9, 14, 21, 24, 35, 31, 31, 29, 30, 33, 34, 39, 34),
        intArrayOf(-6, -92, -53, -51, -69, -60, -31, -20, -7, 9, 18, 26, 28, 27, 23, 44, 49, 22, 41, 27, 37, 13, 17, 0, 6, -59, 47, -58),
        intArrayOf())
    val MOBILITY = Array(Piece.SIZE) { IntArray(32) }

    val PAWN_SUPPORT_MG = intArrayOf(0, 0, 4, 1, 0, 0, 0)
    val PAWN_SUPPORT_EG = intArrayOf(0, 0, 13, 17, 0, 0, 0)
    val PAWN_SUPPORT = IntArray(Piece.SIZE)

    val PAWN_THREAT_MG = intArrayOf(0, 0, 74, 79, 0, 0, 0)
    val PAWN_THREAT_EG = intArrayOf(0, 0, 23, 49, 0, 0, 0)
    val PAWN_THREAT = IntArray(Piece.SIZE)

    const val PAWN_BONUS_SUPPORTED = 0
    const val PAWN_BONUS_PHALANX = 1
    const val PAWN_BONUS_ISOLATED = 2
    const val PAWN_BONUS_STACKED = 3
    const val PAWN_BONUS_BACKWARD = 4

    val PAWN_BONUS_MG = intArrayOf(24, 11, -11, -2, 0)
    val PAWN_BONUS_EG = intArrayOf(5, -2, -10, -11, -2)
    val PAWN_BONUS = IntArray(PAWN_BONUS_EG.size)

    val PASSED_PAWN_MG = intArrayOf(0, -34, -36, -49, -21, -23, 49, 0)
    val PASSED_PAWN_EG = intArrayOf(0, -41, -34, -4, 22, 100, 180, 0)
    val PASSED_PAWN = IntArray(Rank.SIZE)

    const val PASSED_PAWN_SAFE = 0
    const val PASSED_PAWN_CAN_ADVANCE = 1
    const val PASSED_PAWN_SAFE_ADVANCE = 2
    const val PASSED_PAWN_SAFE_PATH = 3
    const val PASSED_PAWN_DEFENDED = 4
    const val PASSED_PAWN_DEFENDED_ADVANCE = 5

    val PASSED_PAWN_BONUS_MG = intArrayOf(0, 30, 0, 0, 25, 0)
    val PASSED_PAWN_BONUS_EG = intArrayOf(10, 10, 21, 21, 0, 17)
    val PASSED_PAWN_BONUS = IntArray(PASSED_PAWN_BONUS_EG.size)

    val PAWN_SHIELD_MG = arrayOf(
        arrayOf(
            intArrayOf(0, 43, 56, 19, 2, 126, 35, 0),
            intArrayOf(0, 69, 63, 40, 22, 29, -52, 0),
            intArrayOf(0, 65, 44, 27, 43, -4, -13, 0),
            intArrayOf(0, 21, 21, 23, 25, -35, 72, 0)),
        arrayOf(
            intArrayOf(5, 15, 22, 20, 11, 35, -88, 0),
            intArrayOf(79, 52, 32, 33, 14, 6, -146, 0),
            intArrayOf(55, 45, 7, 6, 8, 40, 221, 0),
            intArrayOf(15, 17, 5, 20, 23, -45, -56, 0))
    )
    val PAWN_SHIELD_EG = arrayOf(
        arrayOf(
            intArrayOf(0, -30, -25, -10, -6, -89, -13, 0),
            intArrayOf(0, -20, -15, -16, -12, -18, -70, 0),
            intArrayOf(0, -10, -4, -5, -19, -15, -42, 0),
            intArrayOf(0, 10, 5, -2, -11, 45, -74, 0)),
        arrayOf(
            intArrayOf(-6, -7, -2, -6, 0, -15, -11, 0),
            intArrayOf(-18, -12, -2, -10, -2, 8, 23, 0),
            intArrayOf(-9, -12, 0, -9, -5, -15, -98, 0),
            intArrayOf(8, 0, 4, -6, -20, 5, 50, 0))
    )
    val PAWN_SHIELD = Array(2) { Array(File.SIZE / 2) { IntArray(Rank.SIZE) } }

    val KING_THREAT_MG = intArrayOf(0, 0, 5, 10, 2, 11, 0)
    val KING_THREAT_EG = intArrayOf(0, 0, 1, 1, 2, 6, 0)
    val KING_THREAT = IntArray(Piece.SIZE)

    val SAFE_CHECK_THREAT_MG = intArrayOf(0, 0, 113, 16, 88, 32, 0)
    val SAFE_CHECK_THREAT_EG = intArrayOf(0, 0, 3, 36, 5, 93, 0)
    val SAFE_CHECK_THREAT = IntArray(Piece.SIZE)

    val OTHER_BONUS_BISHOP_PAIR = 0

    val OTHER_BONUS_MG = intArrayOf(24)
    val OTHER_BONUS_EG = intArrayOf(59)
    val OTHER_BONUS = IntArray(OTHER_BONUS_EG.size)

    val THREATEN_BY_KNIGHT_MG = intArrayOf(0, 6, 0, 36, 61, 31, 0)
    val THREATEN_BY_KNIGHT_EG = intArrayOf(0, 11, 0, 26, 1, 1, 0)
    val THREATEN_BY_KNIGHT = IntArray(Piece.SIZE)

    init {
        update()
    }

    fun update() {
        PHASE_MAX = PHASE_PIECE_VALUE[Piece.PAWN] * 16 +
            PHASE_PIECE_VALUE[Piece.KNIGHT] * 4 +
            PHASE_PIECE_VALUE[Piece.BISHOP] * 4 +
            PHASE_PIECE_VALUE[Piece.ROOK] * 4 +
            PHASE_PIECE_VALUE[Piece.QUEEN] * 2

        for (piece in Piece.PAWN until Piece.KING) {
            MATERIAL_SCORE[piece] = SplitValue.mergeParts(MATERIAL_SCORE_MG[piece], MATERIAL_SCORE_EG[piece])
        }

        for (piece in Piece.PAWN until Piece.SIZE) {
            var psqPosition = 0
            for (rank in Rank.RANK_1 until Rank.SIZE) {
                for (file in File.FILE_A until File.SIZE / 2) {
                    val square = Square.getSquare(file, Rank.invertRank(rank))
                    val psqtValue = SplitValue.mergeParts(PSQT_MG[piece][psqPosition], PSQT_EG[piece][psqPosition])
                    PSQT[piece][square] = psqtValue
                    PSQT[piece][Square.flipHorizontal(square)] = psqtValue
                    psqPosition++
                }
            }
        }

        for (piece in 0 until MOBILITY.size) {
            for (index in 0 until MOBILITY_MG[piece].size) {
                MOBILITY[piece][index] = SplitValue.mergeParts(MOBILITY_MG[piece][index], MOBILITY_EG[piece][index])
            }
        }

        for (index in 0 until TEMPO.size) {
            TEMPO[index] = SplitValue.mergeParts(TEMPO_TUNING[0], TEMPO_TUNING[1]) * GameConstants.COLOR_FACTOR[index]
        }

        for (piece in 0 until PAWN_SUPPORT.size) {
            PAWN_SUPPORT[piece] = SplitValue.mergeParts(PAWN_SUPPORT_MG[piece], PAWN_SUPPORT_EG[piece])
        }

        for (piece in 0 until PAWN_THREAT.size) {
            PAWN_THREAT[piece] = SplitValue.mergeParts(PAWN_THREAT_MG[piece], PAWN_THREAT_EG[piece])
        }

        for (index in 0 until PAWN_BONUS.size) {
            PAWN_BONUS[index] = SplitValue.mergeParts(PAWN_BONUS_MG[index], PAWN_BONUS_EG[index])
        }

        for (index in 0 until PASSED_PAWN.size) {
            PASSED_PAWN[index] = SplitValue.mergeParts(PASSED_PAWN_MG[index], PASSED_PAWN_EG[index])
        }

        for (index in 0 until PAWN_SHIELD.size) {
            for (index2 in 0 until PAWN_SHIELD[index].size) {
                for (index3 in 0 until PAWN_SHIELD[index][index2].size) {
                    PAWN_SHIELD[index][index2][index3] = SplitValue.mergeParts(
                        PAWN_SHIELD_MG[index][index2][index3],
                        PAWN_SHIELD_EG[index][index2][index3])
                }
            }
        }

        for (index in 0 until KING_THREAT.size) {
            KING_THREAT[index] = SplitValue.mergeParts(KING_THREAT_MG[index], KING_THREAT_EG[index])
        }

        for (index in 0 until SAFE_CHECK_THREAT.size) {
            SAFE_CHECK_THREAT[index] = SplitValue.mergeParts(SAFE_CHECK_THREAT_MG[index], SAFE_CHECK_THREAT_EG[index])
        }

        for (index in 0 until PASSED_PAWN_BONUS.size) {
            PASSED_PAWN_BONUS[index] = SplitValue.mergeParts(PASSED_PAWN_BONUS_MG[index], PASSED_PAWN_BONUS_EG[index])
        }

        for (index in 0 until OTHER_BONUS.size) {
            OTHER_BONUS[index] = SplitValue.mergeParts(OTHER_BONUS_MG[index], OTHER_BONUS_EG[index])
        }

        for (piece in 0 until THREATEN_BY_KNIGHT.size) {
            THREATEN_BY_KNIGHT[piece] = SplitValue.mergeParts(THREATEN_BY_KNIGHT_MG[piece],
                THREATEN_BY_KNIGHT_EG[piece])
        }
    }
}

