package com.sameerasw.dicing.game.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val success: Color
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
            "Player" -> "You win! \uD83D\uDC4F\uD83D\uDE2E\u200D\uD83D\uDCA8"
            "Computer" -> "You lose! \uD83E\uDEF5\uD83D\uDE1D"
            else -> "Game Over"
        }

    // Set text color based on the winner
    val textColor = when (winner) {
        "Player" -> success
        "Computer" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Round over!") },
        text = {
            Text(
                text = dialogText,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}