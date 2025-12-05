package com.example.csci3081_app

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GameLogicTest { // Renamed class to reflect what it's testing

    private lateinit var gameLogic: GameLogic

    @Before
    fun setUp() {
        gameLogic = GameLogic()
    }

    private fun setBoardState(vararg moves: Pair<Pair<Int, Int>, String>) {
        moves.forEach { (pos, player) ->
            gameLogic.board[pos.first][pos.second] = player
        }
    }

    @Test
    fun `checkForWinner - Horizontal Win`() {
        setBoardState(
            Pair(0, 0) to "X",
            Pair(0, 1) to "X",
            Pair(0, 2) to "X"
        )
        val result = gameLogic.checkForWinner("X")
        assertTrue("Horizontal win was not detected", result)
    }

    @Test
    fun `checkForWinner - Vertical Win`() {
        setBoardState(
            Pair(0, 1) to "O",
            Pair(1, 1) to "O",
            Pair(2, 1) to "O"
        )
        val result = gameLogic.checkForWinner("O")
        assertTrue("Vertical win was not detected", result)
    }

    @Test
    fun `checkForWinner - Diagonal Win`() {
        setBoardState(
            Pair(0, 0) to "X",
            Pair(1, 1) to "X",
            Pair(2, 2) to "X"
        )
        val result = gameLogic.checkForWinner("X")
        assertTrue("Diagonal win was not detected", result)
    }

    @Test
    fun `checkForWinner - No Win`() {
        setBoardState(
            Pair(0, 0) to "X",
            Pair(0, 1) to "O",
            Pair(1, 1) to "X"
        )
        val xWins = gameLogic.checkForWinner("X")
        val oWins = gameLogic.checkForWinner("O")
        assertFalse("Should not detect a win for X", xWins)
        assertFalse("Should not detect a win for O", oWins)
    }

    @Test
    fun `isBoardFull - Board is full`() {
        setBoardState(
            Pair(0,0) to "X", Pair(0,1) to "O", Pair(0,2) to "X",
            Pair(1,0) to "X", Pair(1,1) to "O", Pair(1,2) to "O",
            Pair(2,0) to "O", Pair(2,1) to "X", Pair(2,2) to "X"
        )
        assertTrue("Board should be full", gameLogic.isBoardFull())
    }

    @Test
    fun `getAiMove - Hard AI should find winning move`() {
        setBoardState(
            Pair(0, 0) to "O", Pair(0, 1) to "O",
            Pair(1, 0) to "X", Pair(2, 0) to "X"
        )
        val winningMove = gameLogic.getAiMove("Hard", "O", "X")
        assertEquals("AI failed to find its winning move", Pair(0, 2), winningMove)
    }

    @Test
    fun `getAiMove - Hard AI should block opponent's winning move`() {
        setBoardState(
            Pair(1, 0) to "X", Pair(1, 1) to "X",
            Pair(0, 0) to "O", Pair(2, 2) to "O"
        )
        val blockingMove = gameLogic.getAiMove("Hard", "O", "X")
        assertEquals("AI failed to find the blocking move", Pair(1, 2), blockingMove)
    }

    @Test
    fun `getAiMove - Hard AI should take center`() {
        val strategicMove = gameLogic.getAiMove("Hard", "O", "X")
        assertEquals("AI should prioritize the center square", Pair(1, 1), strategicMove)
    }

    @Test
    fun `getAiMove - Hard AI should take a corner if center is taken`() {
        setBoardState(Pair(1, 1) to "X")
        val strategicMove = gameLogic.getAiMove("Hard", "O", "X")
        val corners = listOf(Pair(0, 0), Pair(0, 2), Pair(2, 0), Pair(2, 2))
        assertTrue(
            "AI should choose a corner when the center is taken",
            strategicMove in corners
        )
    }
}
