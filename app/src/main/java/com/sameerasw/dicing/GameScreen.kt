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
import kotlin.random.Random

@Composable
fun GameScreen(
    targetScore: Int,
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
    var humanWins by rememberSaveable { mutableIntStateOf(0) }
    var computerWins by rememberSaveable { mutableIntStateOf(0) }
    var canThrow by rememberSaveable { mutableStateOf(true) }

    fun resetDice() {
        playerDice = rollDice()
        playerScore = playerDice.sum()
        computerDice = rollDice()
        computerScore = computerDice.sum()
        selectedDice = List(5) { false }
        reRollCount = 0
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
                text = "H:$humanWins/C:$computerWins",
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
                enableSelection = canThrow
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
                playerDice = rerollDice(playerDice, selectedDice)
                playerScore = playerDice.sum()
                selectedDice = List(5) { false }
                reRollCount++

            }, enabled =  canThrow  ) {
                Text("Throw")
            }

            Button(onClick = {
                //The total point
                val newPlayerTotalScore = playerTotalScore + playerScore
                val newComputerTotalScore = computerTotalScore + computerScore

                val newComputerDice = performComputerTurn()

                computerDice = newComputerDice
                computerScore = newComputerDice.sum()
                playerTotalScore = newPlayerTotalScore
                computerTotalScore = newComputerTotalScore
                if (isTieBreaker) {
                    winner = if (newPlayerTotalScore > newComputerTotalScore) "Player" else if (newComputerTotalScore >newPlayerTotalScore) "Computer" else "Tie"
                    showWinDialog = true

                }

                else if (playerTotalScore >= targetScore || computerTotalScore >= targetScore) {
                    if(playerTotalScore == computerTotalScore){
                        isTieBreaker = true
                        winner = if (playerTotalScore > newComputerTotalScore) "Player" else if ( newComputerTotalScore>playerTotalScore) "Computer" else "Tie"
                    }

                    else {

                        winner = when {
                            newPlayerTotalScore > newComputerTotalScore -> {
                                humanWins++

                                "Player"
                            }

                            newComputerTotalScore > newPlayerTotalScore -> {
                                computerWins++
                                "Computer"
                            }
                            else -> "Tie"
                        }
                        showWinDialog = true
                    }

                } else {
                    val newComputerDice = performComputerTurn()
                    computerDice = newComputerDice
                    computerScore = newComputerDice.sum()
                    playerDice = rollDice()
                    playerScore = playerDice.sum()
                    resetDice()
                }
            }, enabled = true) {
                Text("Score")
            }
        }

    }


    if (showWinDialog) {
        AlertDialog(
            onDismissRequest = {  }, // Do nothing
            title = { Text("Game Over") },
            text = {
                Column {
                    Text(
                        when (winner) {
                            "Tie" -> "It's a tie!"
                            "Player" -> "Player wins!"
                            "Computer" -> "Computer wins!"
                            else -> "Game Over"
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Continue Playing?")
                }
            },
            confirmButton = {
                Button(onClick = {
                    showWinDialog = false
                    playerTotalScore = 0
                    computerTotalScore = 0
                    showWinDialog = false
                    isTieBreaker = false

                }) {
                    Text("Continue")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showWinDialog = false
                    playerTotalScore = 0
                    computerTotalScore = 0
                    onBack()
                }) {
                    Text("Back to Menu")
                }
            }
        )
    }
}