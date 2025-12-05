package com.example.csci3081_app

import kotlin.random.Random

class GameLogic {

    val board = Array(3) { Array(3) { "" } }

    fun resetBoard() {for (i in 0..2) {
        for (j in 0..2) {
            board[i][j] = ""
        }
    }
    }

    fun makeMove(row: Int, col: Int, player: String): Boolean {
        if (board[row][col].isEmpty()) {
            board[row][col] = player
            return true
        }
        return false
    }

    fun getAiMove(difficulty: String, aiIcon: String, playerIcon: String): Pair<Int, Int>? {
        return when (difficulty) {
            "Easy" -> findRandomMove()
            "Medium" -> {
                if (Random.nextBoolean()) {
                    findWinningMove(aiIcon) ?: findWinningMove(playerIcon) ?: findRandomMove()
                } else {
                    findRandomMove()
                }
            }
            "Hard" -> {
                findWinningMove(aiIcon)
                    ?: findWinningMove(playerIcon)
                    ?: findBestStrategicMove()
            }
            else -> findRandomMove()
        }
    }

    fun checkForWinner(player: String): Boolean {
        // Check rows
        for (i in 0..2) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) return true
        }
        // Check columns
        for (i in 0..2) {
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) return true
        }
        // Check diagonals
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) return true
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) return true
        return false
    }

    fun isBoardFull(): Boolean {
        return board.all { row -> row.all { it.isNotEmpty() } }
    }

    private fun findWinningMove(player: String): Pair<Int, Int>? {
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j].isEmpty()) {
                    board[i][j] = player // Try move
                    if (checkForWinner(player)) {
                        board[i][j] = "" // Reset
                        return Pair(i, j)
                    }
                    board[i][j] = "" // Reset
                }
            }
        }
        return null
    }

    private fun findRandomMove(): Pair<Int, Int> {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j].isEmpty()) {
                    emptyCells.add(Pair(i, j))
                }
            }
        }
        return emptyCells.random()
    }

    private fun findBestStrategicMove(): Pair<Int, Int>? {
        if (board[1][1].isEmpty()) return Pair(1, 1)

        val corners = listOf(Pair(0, 0), Pair(0, 2), Pair(2, 0), Pair(2, 2))
        val availableCorners = corners.filter { board[it.first][it.second].isEmpty() }
        if (availableCorners.isNotEmpty()) return availableCorners.random()

        // Fallback to any available side
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j].isEmpty()) emptyCells.add(Pair(i, j))
            }
        }
        return if (emptyCells.isNotEmpty()) emptyCells.random() else null
    }
}
