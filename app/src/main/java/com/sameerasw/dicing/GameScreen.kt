package com.sameerasw.dicing

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sameerasw.dicing.DiceLogic.rerollDice
import com.sameerasw.dicing.DiceLogic.rollDice
import com.sameerasw.dicing.GameLogic.computerReroll
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import kotlin.random.Random

@Composable
fun GameScreen(
    targetScore: Int,
    humanWins: MutableIntState,
    computerWins: MutableIntState,
    onBack: () -> Unit,
) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, onBackPressedDispatcher) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        }
        onBackPressedDispatcher?.addCallback(lifecycleOwner, callback)
        onDispose {
            callback.remove()
        }
    }

    var playerDice by rememberSaveable { mutableStateOf(rollDice()) }
    var playerScore by rememberSaveable { mutableIntStateOf(playerDice.sum()) }
    var computerDice by rememberSaveable { mutableStateOf(rollDice()) }
    var computerScore by rememberSaveable { mutableIntStateOf(computerDice.sum()) }
    var selectedDice by rememberSaveable { mutableStateOf(List(5) { false }) }
    var reRollCount by rememberSaveable { mutableIntStateOf(0) }
    var playerTotalScore by rememberSaveable { mutableIntStateOf(0) }
    var computerTotalScore by rememberSaveable { mutableIntStateOf(0) }
    var showWinDialog by rememberSaveable { mutableStateOf(false) }
    var winner by rememberSaveable { mutableStateOf("") }
    var isTieBreaker by rememberSaveable { mutableStateOf(false) }
    var playerThrowCount by rememberSaveable { mutableIntStateOf(0) }
    var isLastThrow by rememberSaveable { mutableStateOf(false) }
    var isGameOver by rememberSaveable { mutableStateOf(false) }
    var showGoBackText by rememberSaveable { mutableStateOf(false) } // New state variable

    fun resetDice() {
        playerDice = rollDice()
        playerScore = playerDice.sum()
        computerDice = rollDice()
        computerScore = computerDice.sum()
        selectedDice = List(5) { false }
        reRollCount = 0
        playerThrowCount = 0
        isLastThrow = false
    }

    fun performComputerTurn(): List<Int> {
        var computerDiceTemp = computerDice
        var computerRerollCountTemp = 0
        while (computerRerollCountTemp < 2 && Random.nextBoolean()) {
            val (newDice, newCount) = computerReroll(computerDiceTemp, computerRerollCountTemp)
            computerDiceTemp = newDice
            computerRerollCountTemp = newCount
        }
        return computerDiceTemp
    }

    fun handleScoreAndWinner() {
        // The total points
        val newPlayerTotalScore = playerTotalScore + playerScore
        val newComputerTotalScore = computerTotalScore + computerScore

        // Computer's turn
        val newComputerDice = performComputerTurn()
        computerDice = newComputerDice
        computerScore = newComputerDice.sum()

        playerTotalScore = newPlayerTotalScore
        computerTotalScore = newComputerTotalScore

        if (isTieBreaker) {
            winner = when {
                newPlayerTotalScore > newComputerTotalScore -> "Player"
                newComputerTotalScore > newPlayerTotalScore -> "Computer"
                else -> "Tie"
            }
            isGameOver = true
            showWinDialog = true
        } else if (playerTotalScore >= targetScore || computerTotalScore >= targetScore) {
            winner = when {
                newPlayerTotalScore > newComputerTotalScore -> {
                    humanWins.intValue++
                    "Player"
                }
                newComputerTotalScore > newPlayerTotalScore -> {
                    computerWins.intValue++
                    "Computer"
                }
                else -> "Tie"
            }
            isGameOver = true
            showWinDialog = true
        } else {
            // Reset and continue to the next round
            resetDice()
        }
    }

    // UI logic
    if (!showGoBackText) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "H:${humanWins.intValue}/C:${computerWins.intValue}",
                    fontSize = 20.sp
                )
                Row {
                    Text("A:$playerTotalScore", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("B:$computerTotalScore", fontSize = 20.sp)
                }
            }

            Column(horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Text("Computer Score: $computerScore", fontSize = 20.sp)
                DiceRow(diceValues = computerDice, isPlayer = false)

                Spacer(modifier = Modifier.height(32.dp))

                Text("Player Score: $playerScore", fontSize = 20.sp)
                DiceRow(
                    diceValues = playerDice,
                    isPlayer = true,
                    selectedDice = selectedDice,
                    onDiceSelected = { index, isSelected ->
                        selectedDice = selectedDice.toMutableList().apply { this[index] = isSelected }
                    },
                )
            }

            // Bottom - Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            ) {
                Button(onClick = {
                    if (playerThrowCount < 1) {
                        playerDice = rerollDice(playerDice, selectedDice)
                        playerScore = playerDice.sum()
                        selectedDice = List(5) { false }
                        reRollCount++
                        playerThrowCount++

                        // Check if it's the last throw
                        if (playerThrowCount == 1) {
                            isLastThrow = true
                        }
                    } else {
                        // Automatically handle score and winner check after last throw
                        handleScoreAndWinner()
                    }
                }) {
                    Text(if (isLastThrow) "Throw and Score" else "Throw")
                }

                Button(onClick = {
                    // Handle scoring and winner check
                    handleScoreAndWinner()
                }) {
                    Text("Score")
                }
            }
        }

        if (showWinDialog) {
            AlertDialog(
                onDismissRequest = {  }, // Do nothing on dismiss
                title = { Text("Game Over") },
                text = {
                    Text(
                        when (winner) {
                            "Tie" -> "It's a tie!"
                            "Player" -> "Player wins!"
                            "Computer" -> "Computer wins!"
                            else -> "Game Over"
                        },
                        color = when (winner) {
                            "Player" -> Color.Green
                            "Computer" -> Color.Red
                            else -> Color.Black
                        }
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        showWinDialog = false
                        isGameOver = true
                        showGoBackText = true

                    }) {
                        Text("OK")
                    }
                }
            )
        }
    } else { // if (!showGame)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Go back to play again!",
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            )
        }
    }
}