package com.sameerasw.dicing

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainMenu(humanWins: Int, computerWins: Int, onNavigateToGame: (Int, Boolean) -> Unit) {
    // Main menu screen with new game and about buttons
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var targetScore by rememberSaveable { mutableStateOf("101") }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var useSmartStrategy by rememberSaveable { mutableStateOf(true) }

    // scrollable in case of overflow
    val scrollState = rememberScrollState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Main Menu Box
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
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

                // Display the current wins
                Text(
                    text = "Wins: H:$humanWins /C:$computerWins",
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Target score input field
                OutlinedTextField(
                    value = targetScore,
                    onValueChange = {
                        targetScore = it
                        errorMessage = ""
                    },
                    label = { Text("Target Score") },
                    isError = errorMessage.isNotEmpty(),
                    supportingText = {
                        if (errorMessage.isNotEmpty()) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Difficulty switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Difficulty: ${if(useSmartStrategy) "Hard" else "Easy"}",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = useSmartStrategy,
                        onCheckedChange = { useSmartStrategy = it }
                    )
                }

                Text(
                    text = if (useSmartStrategy)
                        "Computer will strategically reroll low dice"
                    else
                        "Computer will randomly reroll dice",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Options
                Row {
                    Button(onClick = { showDialog = true }) {
                        Text(text = "About")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(onClick = {
                        val score = targetScore.toIntOrNull()
                        if (score == null || score <= 0) {
                            errorMessage = "Please enter a valid positive number"
                        } else {
                            onNavigateToGame(score, useSmartStrategy)
                        }
                    }) {
                        Text(text = "New Game")
                    }
                }
            }
        }

        // Game Rules Section
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Game Rules",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "1. Players take turns rolling five dice.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "2. After rolling, you can select specific dice to reroll (tap the checkbox).",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "3. You can reroll up to twice",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "4. The computer will decide what to do depending on difficulty.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "5. Your score is the sum of all dice values.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "6. First player to reach the target score wins.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "7. If both players reach target score in the same round, a tiebreaker round is played.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // all teh dices from 1 to 6 are displayed
        Row {
            for (i in 1..6) {
                Dice(
                    value = i
                )
            }
        }

        // Add some space at the bottom for better scrolling experience
        Spacer(modifier = Modifier.height(16.dp))
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