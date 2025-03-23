package com.sameerasw.dicing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp

@Composable
fun DiceRow(
    diceValues: List<Int>,
    isPlayer: Boolean,
    selectedDice: List<Boolean> = List(5) { false },
    onDiceSelected: (Int, Boolean) -> Unit = { _, _ -> },
    enableSelection: Boolean = true,
    enableSelectionFlag: Boolean = true
) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        diceValues.forEachIndexed { index, value ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Dice(value = value, tint = ColorFilter.tint(MaterialTheme.colorScheme.primary))
                if (isPlayer) {
                    Checkbox(
                        checked = selectedDice[index],
                        onCheckedChange = { isChecked ->
                            if (enableSelection) {
                                onDiceSelected(index, isChecked)
                            }
                        },
                        enabled = enableSelection && enableSelectionFlag
                    )
                }
            }
        }
    }
}