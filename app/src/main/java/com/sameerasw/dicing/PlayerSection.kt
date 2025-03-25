package com.sameerasw.dicing.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
fun PlayerSection(
    playerScore: Int,
    playerDice: List<Int>,
    selectedDice: List<Boolean>,
    onDiceSelected: (Int, Boolean) -> Unit,
    enableSelection: Boolean
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.Person, contentDescription = "Player", modifier = Modifier.size(24.dp))
            Text(" Player Score: $playerScore", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        DiceRow(
            diceValues = playerDice,
            isPlayer = true,
            selectedDice = selectedDice,
            onDiceSelected = onDiceSelected,
            enableSelection = enableSelection,
            diceColor = MaterialTheme.colorScheme.primary
        )
    }
}