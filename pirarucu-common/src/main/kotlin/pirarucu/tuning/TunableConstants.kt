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

    val MATERIAL_SCORE_MG = intArrayOf(0, 85, 485, 515, 680, 1450)
    val MATERIAL_SCORE_EG = intArrayOf(0, 115, 320, 360, 620, 1085)
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
            0, 0, 0, 0, -24, 53, 73, 73, 21, 27, 71, 88, 2, 10, 20, 27, -8, -11, 17, 25, -8, -13, 1, 4, -3, 1, 9, 5, 0, 0, 0, 0
        ),
        intArrayOf(
            -200, -125, -130, -30, -85, -50, 55, -10, -20, 60, 45, 70, 15, 20, 15, 30, 0, 15, 20, 20, -20, 5, 15, 25, -15, -15, 5, 20, -75, 0, -25, -5
        ),
        intArrayOf(
            -40, -70, -155, -135, -80, -10, -20, -45, -10, 30, 35, 15, -20, 5, 10, 25, -5, 0, 0, 25, -5, 10, 20, 10, 10, 30, 15, 10, -25, -10, -5, 0
        ),
        intArrayOf(
            5, 30, -5, 40, 10, 20, 75, 75, -10, 30, 20, 20, -35, -25, 20, 20, -35, -10, -5, 5, -40, 0, 5, 5, -55, 0, 5, 20, -10, -10, 15, 35
        ),
        intArrayOf(
            -10, 10, 0, 25, -10, -40, 10, -45, 20, 5, 10, -5, -5, -20, -20, -20, 10, -10, 10, 0, 5, 35, 20, 25, 10, 30, 45, 50, 35, 35, 40, 50
        ),
        intArrayOf(
            -73, 114, 44, 29, 23, 67, 57, 76, 37, 123, 158, 86, -31, 3, 51, 12, -83, -31, -52, -66, -42, -13, -60, -40, 23, 8, -53, -33, 27, 37, -24, 30
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
            0, 0, 0, 0, 54, 29, -20, -33, 29, 13, -15, -52, 5, -4, -12, -24, -11, -7, -23, -29, -17, -23, -20, -16, -13, -17, -13, -16, 0, 0, 0, 0
        ),
        intArrayOf(
            -50, -30, -5, -25, -25, 0, -35, 5, -25, -25, 5, 5, -15, 5, 25, 30, -5, 0, 20, 25, -15, -15, -5, 20, -25, -15, -10, -5, -15, -20, -5, -10
        ),
        intArrayOf(
            -15, -15, 0, 10, 10, -5, 0, 0, -5, -10, -5, -10, 5, -5, 5, 0, -10, -10, 0, 10, -5, -15, -5, 0, -10, -20, -10, -5, 0, 10, 5, -5
        ),
        intArrayOf(
            15, 5, 20, 20, 15, 15, 0, 0, 0, 0, 5, 0, 15, 10, 10, 5, 5, 10, 5, 0, 0, 0, -10, -5, 5, -10, -5, -10, -15, 0, -5, -15
        ),
        intArrayOf(
            5, 10, 20, 5, -5, 10, 15, 60, -5, 0, -5, 35, 20, 35, 10, 25, -5, 30, -5, 10, 10, -30, -5, -20, -30, -55, -45, -45, -65, -60, -50, -45
        ),
        intArrayOf(
            -42, -39, -8, -24, -8, 10, 26, 11, 4, 23, 22, 11, 10, 33, 31, 36, 2, 12, 41, 47, 0, 13, 36, 36, -26, -6, 23, 22, -68, -41, -6, -26
        )
    )

    val PSQT = Array(Piece.SIZE) { IntArray(Square.SIZE) }

    val MOBILITY_MG = arrayOf(
        intArrayOf(),
        intArrayOf(),
        intArrayOf(-36, -31, -16, -10, 2, 10, 16, 18, 47),
        intArrayOf(-37, -23, -5, -1, 7, 13, 17, 18, 19, 23, 36, 99, 8, 59),
        intArrayOf(-67, -52, -49, -41, -39, -27, -20, -8, 3, 15, 24, 35, 97, 40, 50),
        intArrayOf(8, -4, 3, 5, 5, 12, 9, 10, 14, 10, 13, 13, 12, 17, 18, 11, 11, 22, 15, 81, -14, -10, 112, 106, 90, 123, 64, 14),
        intArrayOf())

    val MOBILITY_EG = arrayOf(
        intArrayOf(),
        intArrayOf(),
        intArrayOf(-34, -31, 4, 15, 16, 23, 19, 17, 0),
        intArrayOf(-43, -38, -22, -2, 4, 8, 12, 16, 18, 17, 8, -4, 31, 1),
        intArrayOf(-80, -70, -1, 9, 21, 24, 35, 31, 31, 29, 30, 33, 14, 39, 34),
        intArrayOf(-71, -122, -103, -116, -104, -125, -96, -70, -72, -16, -2, 6, 18, 22, 23, 54, 59, 44, 63, 72, 117, 123, 27, 85, 81, 71, 82, 107),
        intArrayOf())
    val MOBILITY = Array(Piece.SIZE) { IntArray(32) }

    val PAWN_SUPPORT_MG = intArrayOf(0, 0, 4, 1, 0, 0, 0)
    val PAWN_SUPPORT_EG = intArrayOf(0, 0, 13, 17, 0, 0, 0)
    val PAWN_SUPPORT = IntArray(Piece.SIZE)

    val PAWN_THREAT_MG = intArrayOf(0, 0, 79, 84, 0, 0, 0)
    val PAWN_THREAT_EG = intArrayOf(0, 0, 23, 49, 0, 0, 0)
    val PAWN_THREAT = IntArray(Piece.SIZE)

    const val PAWN_BONUS_SUPPORTED = 0
    const val PAWN_BONUS_PHALANX = 1
    const val PAWN_BONUS_ISOLATED = 2
    const val PAWN_BONUS_STACKED = 3
    const val PAWN_BONUS_BACKWARD = 4

    val PAWN_BONUS_MG = intArrayOf(24, 11, -16, -2, 0)
    val PAWN_BONUS_EG = intArrayOf(5, -2, -10, -11, -2)
    val PAWN_BONUS = IntArray(PAWN_BONUS_EG.size)

    val PASSED_PAWN_MG = intArrayOf(0, -39, -36, -44, -21, -23, 54, 0)
    val PASSED_PAWN_EG = intArrayOf(0, -36, -34, -4, 22, 100, 180, 0)
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
            intArrayOf(0, 43, 56, 19, 2, 121, 0, 0),
            intArrayOf(0, 69, 63, 40, 27, 34, -77, 0),
            intArrayOf(0, 65, 44, 37, 43, 1, -43, 0),
            intArrayOf(0, 26, 21, 18, 35, -30, 32, 0)),
        arrayOf(
            intArrayOf(5, 15, 22, 20, 6, 30, -68, 0),
            intArrayOf(74, 52, 32, 33, 19, 11, -121, 0),
            intArrayOf(55, 45, 7, 11, 8, 40, 71, 0),
            intArrayOf(15, 17, 0, 20, 13, -50, -51, 0))
    )
    val PAWN_SHIELD_EG = arrayOf(
        arrayOf(
            intArrayOf(0, -30, -25, -10, -1, -79, -3, 0),
            intArrayOf(0, -20, -15, -16, -12, -23, -65, 0),
            intArrayOf(0, -10, -4, -10, -19, -15, -42, 0),
            intArrayOf(0, 10, 5, -7, -26, 40, -64, 0)),
        arrayOf(
            intArrayOf(-6, -7, -2, -6, 0, -15, -21, 0),
            intArrayOf(-13, -12, -2, -10, 3, 3, 23, 0),
            intArrayOf(-9, -7, 5, -9, 0, -10, -43, 0),
            intArrayOf(8, 0, 9, -6, -25, 5, 50, 0))
    )
    val PAWN_SHIELD = Array(2) { Array(File.SIZE / 2) { IntArray(Rank.SIZE) } }

    val KING_THREAT_MG = intArrayOf(0, 0, 5, 10, 2, 11, 0)
    val KING_THREAT_EG = intArrayOf(0, 0, 1, 1, 2, 6, 0)
    val KING_THREAT = IntArray(Piece.SIZE)

    val SAFE_CHECK_THREAT_MG = intArrayOf(0, 0, 63, 16, 63, 37, 0)
    val SAFE_CHECK_THREAT_EG = intArrayOf(0, 0, 18, 36, 10, 63, 0)
    val SAFE_CHECK_THREAT = IntArray(Piece.SIZE)

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
    }
}

