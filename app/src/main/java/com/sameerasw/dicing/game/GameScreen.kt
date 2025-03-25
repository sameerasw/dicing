package com.sameerasw.dicing.game

import android.content.res.Configuration
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
import com.sameerasw.dicing.game.GameLogic.computerReroll
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import kotlin.random.Random
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import com.sameerasw.dicing.game.components.ComputerSection
import com.sameerasw.dicing.game.components.GameControls
import com.sameerasw.dicing.game.components.PlayerSection
import com.sameerasw.dicing.game.components.ScoreHeader
import com.sameerasw.dicing.game.components.WinDialog

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
    val onBackPressedDispatcher =
        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher // handles back button
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
        val orientation = LocalConfiguration.current.orientation
        val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    if (isLandscape) 32.dp else 48.dp
                ),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            ScoreHeader(
                humanWins = humanWins.intValue,
                computerWins = computerWins.intValue,
                targetScore = targetScore,
                playerTotalScore = playerTotalScore,
                computerTotalScore = computerTotalScore
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Conditional layout based on orientation
            if (isLandscape) {
                // Horizontal layout for landscape
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            MaterialTheme.colorScheme.inverseOnSurface,
                            MaterialTheme.shapes.medium
                        )
                        .padding(32.dp)
                ) {

                    // Player section
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        PlayerSection(
                            playerScore = playerScore,
                            playerDice = playerDice,
                            selectedDice = selectedDice,
                            onDiceSelected = { index, isSelected ->
                                selectedDice =
                                    selectedDice.toMutableList().apply { this[index] = isSelected }
                            },
                            enableSelection = canThrow
                        )
                    }

                    // Computer section
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        ComputerSection(
                            computerScore = computerScore,
                            computerDice = computerDice
                        )
                    }

                }
            } else {
                // Vertical layout for portrait
                Column(
                    horizontalAlignment = CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.inverseOnSurface,
                            MaterialTheme.shapes.medium
                        )
                        .padding(8.dp)
                ) {
                    ComputerSection(
                        computerScore = computerScore,
                        computerDice = computerDice
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    PlayerSection(
                        playerScore = playerScore,
                        playerDice = playerDice,
                        selectedDice = selectedDice,
                        onDiceSelected = { index, isSelected ->
                            selectedDice =
                                selectedDice.toMutableList().apply { this[index] = isSelected }
                        },
                        enableSelection = canThrow
                    )
                }
            }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.shapes.medium
                        )
                ) {
                    Text(
                        "Tap dices and select to keep and click on 'Throw' to roll the remaining dices.",
                        modifier = Modifier.padding(8.dp)
                    )
                }

            // Controls - always at bottom
            GameControls(
                canThrow = canThrow,
                isLastThrow = isLastThrow,
                onThrowClick = {
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
                        handleScoreAndWinner()
                    }
                },
                onScoreClick = {
                    handleScoreAndWinner()
                }
            )
        }

        // Dialog code remains unchanged
        if (showWinDialog) {
            WinDialog(
                isTieBreaker = isTieBreaker,
                winner = winner,
                onDismiss = {
                    showWinDialog = false
                    isGameOver = true
                    if (isTieBreaker) {
                        isTieBreaker = false
                        resetDice()
                    } else {
                        showGoBackText = true
                    }
                }
            )
        }
    } else {
        // "Go back" screen remains unchanged
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Star",
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Go Back to Restart",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                )
                Text(
                    text = "Press the back button to go back to the main menu",
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                )
            }
        }
    }
}