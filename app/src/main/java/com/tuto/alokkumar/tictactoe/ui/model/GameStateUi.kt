package com.tuto.alokkumar.tictactoe.ui.model

import android.os.Parcelable
import com.tuto.alokkumar.tictactoe.data.BoardSize
import kotlinx.parcelize.Parcelize

/**
 * UI-ready representation of the match state.
 * Contains symbols and labels instead of internal IDs.
 */
@Parcelize
data class GameStateUi(
    val matchId: String,
    val board: List<String?>,
    val currentPlayerSymbol: String,
    val winnerSymbol: String?,
    val winLine: List<Int>?,
    val lastMove: Int?,
    val p1Wins: Int,
    val p2Wins: Int,
    val draws: Int,
    val boardSize: BoardSize
) : Parcelable
