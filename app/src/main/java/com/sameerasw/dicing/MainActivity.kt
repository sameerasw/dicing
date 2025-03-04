package com.sameerasw.dicing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import com.sameerasw.dicing.ui.theme.DicingTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}

@Composable
fun App() {
    DicingTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                TitleBar()
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = CenterHorizontally
                ) {
                    MainMenu()
                }
            }
        }
    }
}

@Composable
fun TitleBar() {
    Text(
        text = "Dicing",
        textAlign = TextAlign.Center,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun MainMenu() {
    var showDialog by remember { mutableStateOf(false) }
    var showGameScreen by remember { mutableStateOf(false) }

    if (showGameScreen) {
        GameScreen(onBack = { showGameScreen = false })
        return
    }

    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier
            .padding(32.dp)
            .border(width = 2.dp, color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium)
            .padding(32.dp)
    ) {
        Text(
            text = "Main Menu",
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showGameScreen = true }) {
            Text(text = "New Game")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showDialog = true }) {
            Text(text = "About")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("About") },
            text = {
                Text(
                    "Developed by Sameera Wijerathna. (20223140)\n" +
                            "I confirm that I understand what plagiarism is and have read and\n" +
                            "understood the section on Assessment Offences in the Essential Information for Students. The work that I have submitted is entirely my own. Any work from other authors is duly referenced and acknowledged.",
                    modifier = Modifier.padding(top = 16.dp)
                )
            },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

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

    var playerDice by remember { mutableStateOf(List(5) { 1 }) }
    var playerScore by remember { mutableStateOf(0) }
    var computerDice by remember { mutableStateOf(List(5) { 1 }) }
    var computerScore by remember { mutableStateOf(0) }
    var isPlayerTurn by remember { mutableStateOf(true) }
    var selectedDice by remember { mutableStateOf(List(5) { false }) }
    var rerollCount by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Player Score: $playerScore", fontSize = 20.sp)
        DiceRow(diceValues = playerDice, isPlayer = true, selectedDice = selectedDice, onDiceSelected = { index, isSelected ->
            selectedDice = selectedDice.toMutableList().apply { this[index] = isSelected }
        }, enableSelection = rerollCount < 2)

        Row {
            Button(onClick = {
                if (isPlayerTurn) {
                    if (rerollCount < 2 && selectedDice.any { it }) {
                        playerDice = rerollDice(playerDice, selectedDice)
                        playerScore = playerDice.sum()
                        selectedDice = List(5) { false }
                        rerollCount++
                    }
                }
            }, enabled = isPlayerTurn && rerollCount < 2 && selectedDice.any { it }) {
                Text("Reroll Selected")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = {
                if (isPlayerTurn) {
                    playerDice = rollDice()
                    playerScore = playerDice.sum()
                    isPlayerTurn = false
                    rerollCount = 0
                    computerTurn(computerDice, computerScore, onComputerTurnComplete = {
                        computerDice = it.first
                        computerScore = it.second
                        isPlayerTurn = true
                    })
                }
            }, enabled = isPlayerTurn) {
                Text(if (rerollCount < 2 && selectedDice.any { it }) "Skip Reroll" else "Roll Dice")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Computer Score: $computerScore", fontSize = 20.sp)
        DiceRow(diceValues = computerDice, isPlayer = false)
    }
}

@Composable
fun DiceRow(diceValues: List<Int>, isPlayer: Boolean, selectedDice: List<Boolean> = List(5) { false }, onDiceSelected: (Int, Boolean) -> Unit = { _, _ -> }, enableSelection: Boolean = true) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        diceValues.forEachIndexed { index, value ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Dice(value = value, tint = ColorFilter.tint(MaterialTheme.colorScheme.primary))
                if (isPlayer) {
                    Checkbox(checked = selectedDice[index], onCheckedChange = { isChecked ->
                        if (enableSelection) {
                            onDiceSelected(index, isChecked)
                        }
                    }, enabled = enableSelection)
                }
            }
        }
    }
}
fun rerollDice(diceValues: List<Int>, selectedDice: List<Boolean>): List<Int> {
    return diceValues.mapIndexed { index, value ->
        if (selectedDice[index]) Random.nextInt(1, 7) else value
    }
}

fun computerTurn(dice: List<Int>, score: Int, onComputerTurnComplete: (Pair<List<Int>, Int>) -> Unit) {
    val newDice = rollDice()
    val newScore = newDice.sum()
    onComputerTurnComplete(Pair(newDice, newScore))
}

fun rollDice(): List<Int> {
    return List(5) { Random.nextInt(1, 7) }
}

@Preview(showBackground = true, device = Devices.PIXEL_6A)
@Composable
fun GreetingPreview() {
    App()
}