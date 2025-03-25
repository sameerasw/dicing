package com.sameerasw.dicing

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import kotlin.random.Random
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star

private val ColorScheme.success: Color
    get() {
        return Color(0xFF4CAF50)
    }

@Composable
fun GameScreen(
    targetScore: Int,
    humanWins: MutableIntState,
    computerWins: MutableIntState,
    useSmartStrategy: Boolean = true,
    onBack: () -> Unit, // Callback to navigate back to the main menu
) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher // handles back button
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, onBackPressedDispatcher) {
        // Handle back button press
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        }
        onBackPressedDispatcher?.addCallback(lifecycleOwner, callback)
        onDispose {
            callback.remove() // clean up when the effect is removed
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
    var canThrow by rememberSaveable { mutableStateOf(true) }
    var playerThrowCount by rememberSaveable { mutableIntStateOf(0) }
    var isLastThrow by rememberSaveable { mutableStateOf(false) }
    var isGameOver by rememberSaveable { mutableStateOf(false) }
    var showGoBackText by rememberSaveable { mutableStateOf(false) }

    fun resetDice() {
        // Reset the dice and scores
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
        // Computer's turn - reroll the dice, up to 2 times of optional rerolls
        var computerDiceTemp = computerDice
        var computerRerollCountTemp = 0
        while (computerRerollCountTemp < 2 && Random.nextBoolean()) {
            // Reroll the dice up to 2 times
            val (newDice, newCount) = computerReroll(
                computerDiceTemp,
                computerRerollCountTemp,
                useSmartStrategy
            )
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

        // Update the total scores
        playerTotalScore = newPlayerTotalScore
        computerTotalScore = newComputerTotalScore

        // Check if the game is over
        if (playerTotalScore >= targetScore && computerTotalScore >= targetScore && playerTotalScore == computerTotalScore) {
            isTieBreaker = true
            canThrow = false
            showWinDialog = true
            winner = "Tie"
            return
        }

        if (isTieBreaker) {
            //Tie breaker round
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
                else -> {
                    // Tie breaker round
                    isTieBreaker = true
                    canThrow = false
                    showWinDialog = true
                    "Tie"
                }
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
            // Top - Scores
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
                Text(
                    text = "Mode: ${if (useSmartStrategy) "Hard" else "Easy"} | Target: $targetScore",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Person, contentDescription = "Player", modifier = Modifier.size(24.dp))
                    Text(" $playerTotalScore", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Filled.Star, contentDescription = "Computer", modifier = Modifier.size(24.dp))
                    Text(" $computerTotalScore", fontSize = 20.sp)
                }
            }

            // Middle - Dice
            Column(horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Star, contentDescription = "Computer", modifier = Modifier.size(24.dp))
                    Text(" Computer Score: $computerScore", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                DiceRow(
                    diceValues = computerDice,
                    isPlayer = false,
                    diceColor = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Person, contentDescription = "Player", modifier = Modifier.size(24.dp))
                    Text(" Player Score: $playerScore", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                DiceRow(
                    diceValues = playerDice,
                    isPlayer = true,
                    selectedDice = selectedDice,
                    onDiceSelected = { index, isSelected ->
                        selectedDice = selectedDice.toMutableList().apply { this[index] = isSelected }
                    },
                    enableSelection = canThrow,
                    diceColor = MaterialTheme.colorScheme.primary
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

                        if (playerThrowCount == 1) {
                            isLastThrow = true
                        }
                    } else {
                        // Automatically handle score and winner check
                        handleScoreAndWinner()
                    }
                }, enabled = canThrow) {
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
            val dialogText = if (isTieBreaker) "Score most points to win!" else
            // Show the winner
                when (winner) {
                    "Tie" -> "It's a tie!"
                    "Player" -> "You win!"
                    "Computer" -> "You lose!"
                    else -> "Game Over"
                }
            val textColor = when (winner) {
                    // Color the text based on the winner
                    "Player" -> MaterialTheme.colorScheme.success
                    "Computer" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.primary
                }
            AlertDialog(
                onDismissRequest = {
                    showWinDialog = false
                    isGameOver = true
                    if (isTieBreaker) {
                        isTieBreaker = false
                        resetDice()
                    }
                    else{
                        showGoBackText = true // Activate the "Go Back" text
                    }
                },
                title = { Text("Target Reached") },
                text = {
                    Text(
                        text = dialogText,
                        color = textColor,
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        showWinDialog = false
                        isGameOver = true
                        if (isTieBreaker) {
                            isTieBreaker = false
                            resetDice()
                        }
                        else{
                            showGoBackText = true // Activate the "Go Back" text
                        }

                    }) {
                        Text("OK")
                    }
                }
            )
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                // Show the "Go Back" text
                text = "Go Back to Restart",
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            )
        }
    }
}