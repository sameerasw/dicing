package com.sameerasw.dicing.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorScheme.success: Color
    get() {
        return Color(0xFF4CAF50)
    }

@Composable
fun WinDialog(
    isTieBreaker: Boolean,
    winner: String,
    onDismiss: () -> Unit
) {
    val dialogText = if (isTieBreaker) "Score most points to win!" else
        when (winner) {
            "Tie" -> "It's a tie!"
            "Player" -> "You win!"
            "Computer" -> "You lose!"
            else -> "Game Over"
        }

    val textColor = when (winner) {
        "Player" -> MaterialTheme.colorScheme.success
        "Computer" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Target Reached") },
        text = {
            Text(
                text = dialogText,
                color = textColor,
            )
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}