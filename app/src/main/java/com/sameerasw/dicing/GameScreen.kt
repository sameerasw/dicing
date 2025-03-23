package com.sameerasw.dicing

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sameerasw.dicing.DiceLogic.rerollDice
import com.sameerasw.dicing.DiceLogic.rollDice
import com.sameerasw.dicing.GameLogic.computerReroll
import kotlin.random.Random

@Composable
fun GameScreen(onBack: () -> Unit) {
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

    var playerDice by remember { mutableStateOf(rollDice()) }
    var playerScore by remember { mutableStateOf(playerDice.sum()) }
    var computerDice by remember { mutableStateOf(rollDice()) }
    var computerScore by remember { mutableStateOf(computerDice.sum()) }
    var selectedDice by remember { mutableStateOf(List(5) { false }) }
    var rerollCount by remember { mutableStateOf(0) }
    var playerTotalScore by remember { mutableStateOf(0) }
    var computerTotalScore by remember { mutableStateOf(0) }
    var showWinDialog by remember { mutableStateOf(false) }
    var winner by remember { mutableStateOf("") }
    var computerRerollCount by remember { mutableStateOf(0) }
    var isTieBreaker by remember { mutableStateOf(false) } // Track tiebreaker state

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Player Total Score: $playerTotalScore", fontSize = 20.sp)
        Text("Computer Total Score: $computerTotalScore", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(32.dp))

        Text("Computer Score: $computerScore", fontSize = 20.sp)
        DiceRow(diceValues = computerDice, isPlayer = false)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Player Score: $playerScore", fontSize = 20.sp)
        DiceRow(diceValues = playerDice, isPlayer = true, selectedDice = selectedDice, onDiceSelected = { index, isSelected ->
            selectedDice = selectedDice.toMutableList().apply { this[index] = isSelected }
        }, enableSelection = rerollCount < 2)

        Row {
            Button(onClick = {
                if (rerollCount < 2 && selectedDice.any { it }) {
                    playerDice = rerollDice(playerDice, selectedDice)
                    playerScore = playerDice.sum()
                    selectedDice = List(5) { false }
                    rerollCount++
                }
            }, enabled = rerollCount < 2 && selectedDice.any { it }) {
                Text("Reroll Selected")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = {
                // Computer's Turn
                var computerDiceTemp = computerDice
                var computerRerollCountTemp = 0
                while (computerRerollCountTemp < 2 && Random.nextBoolean()) {
                    val (newDice, newCount) = computerReroll(computerDiceTemp, computerRerollCountTemp)
                    computerDiceTemp = newDice
                    computerRerollCountTemp = newCount
                }

                computerDice = computerDiceTemp
                computerScore = computerDice.sum()


                val newPlayerTotalScore = playerTotalScore + playerScore
                val newComputerTotalScore = computerTotalScore + computerScore
                playerTotalScore = newPlayerTotalScore
                computerTotalScore = newComputerTotalScore
                if (isTieBreaker) {
                    winner = if (playerScore > computerScore) "Player" else if (computerScore > playerScore) "Computer" else "Tie"
                    showWinDialog = true
                }
                else if (newPlayerTotalScore >= 101 || newComputerTotalScore >= 101) {
                    if(newPlayerTotalScore == newComputerTotalScore){
                        isTieBreaker = true;
                        winner = if (playerScore > computerScore) "Player" else if (computerScore > playerScore) "Computer" else "Tie"
                    }
                    else {
                        winner = when {
                            newPlayerTotalScore > newComputerTotalScore -> "Player"
                            newComputerTotalScore > newPlayerTotalScore -> "Computer"
                            else -> "Tie"
                        }
                        showWinDialog = true
                    }

                } else {
                    playerDice = rollDice()
                    playerScore = playerDice.sum()
                    computerDice = rollDice()
                    computerScore = computerDice.sum()
                    selectedDice = List(5) { false }
                    rerollCount = 0
                }
            }, enabled = true) {
                Text("Score & Roll")
            }
        }

    }

    if (showWinDialog) {
        AlertDialog(
            onDismissRequest = {
                showWinDialog = false
                playerTotalScore = 0
                computerTotalScore = 0
                onBack()
            },
            title = { Text("Game Over") },
            text = {
                when (winner) {
                    "Tie" -> Text("It's a tie! Rolling Again.")
                    "Player" -> Text("Player wins!")
                    "Computer" -> Text("Computer wins!")
                    else -> Text("Game Over")
                }
            },
            confirmButton = {
                Button(onClick = {
                    showWinDialog = false
                    playerTotalScore = 0
                    computerTotalScore = 0
                    onBack()
                }) {
                    Text("OK")
                }
            }
        )
    }
}