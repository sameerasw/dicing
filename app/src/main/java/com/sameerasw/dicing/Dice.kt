package com.sameerasw.dicing

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun Dice(value: Int, tint: ColorFilter) {

    // mapping the dice value to the corresponding drawable resource
    val imageResource = when (value) {
        1 -> R.drawable.dice1
        2 -> R.drawable.dice2
        3 -> R.drawable.dice3
        4 -> R.drawable.dice4
        5 -> R.drawable.dice5
        6 -> R.drawable.dice6
        else -> R.drawable.dice1
    }

    Image(
        painter = painterResource(id = imageResource),
        contentDescription = "Dice with value $value",
        modifier = Modifier.size(64.dp),
        colorFilter = tint
    )
}