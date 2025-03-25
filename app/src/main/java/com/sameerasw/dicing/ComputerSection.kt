package com.sameerasw.dicing.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sameerasw.dicing.DiceRow

@Composable
fun ComputerSection(
    computerScore: Int,
    computerDice: List<Int>
) {
    Column {
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
    }
}