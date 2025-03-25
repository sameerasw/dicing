package com.sameerasw.dicing.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter

@Composable
fun DiceRow(
    diceValues: List<Int>,
    isPlayer: Boolean,
    selectedDice: List<Boolean> = List(5) { false },
    onDiceSelected: (Int, Boolean) -> Unit = { _, _ -> },
    enableSelection: Boolean = true,
    diceColor: Color = MaterialTheme.colorScheme.primary
) {
    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        diceValues.forEachIndexed { index, value ->
            if (isPlayer) {
                Dice(
                    value = value,
                    tint = ColorFilter.tint(diceColor),
                    selected = selectedDice[index],
                    enabled = enableSelection,
                    onClick = {
                        onDiceSelected(index, !selectedDice[index])
                    }
                )
            } else {
                Dice(
                    value = value,
                    tint = ColorFilter.tint(diceColor)
                )
            }
        }
    }
}