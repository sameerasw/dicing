package com.sameerasw.dicing

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainMenu() {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var showGameScreen by rememberSaveable { mutableStateOf(false) }

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