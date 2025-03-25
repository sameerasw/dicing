package com.sameerasw.dicing.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GameControls(
    canThrow: Boolean,
    isLastThrow: Boolean,
    onThrowClick: () -> Unit,
    onScoreClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        Button(onClick = onThrowClick, enabled = canThrow) {
            Text(if (isLastThrow) "Throw and Score" else "Throw")
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(onClick = onScoreClick) {
            Text("Score")
        }
    }
}