package com.sameerasw.dicing.game.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sameerasw.dicing.R

@Composable
fun Dice(
    value: Int,
    tint: ColorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
) {
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

    val borderModifier = if (selected) {
        Modifier.border(
            width = 4.dp,  // Increased from 2.dp to 4.dp
            color = MaterialTheme.colorScheme.tertiary,
            shape = MaterialTheme.shapes.medium
        )
    } else {
        Modifier
    }

    val clickableModifier = if (onClick != null && enabled) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box {
        // Base dice image
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = "Dice with value $value",
            modifier = Modifier
                .padding(4.dp)
                .size(64.dp)
                .then(borderModifier)
                .clip(MaterialTheme.shapes.medium)
                .then(clickableModifier),
            colorFilter = tint
        )

        // Add lock icon with circular background if the dice is selected
        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 4.dp)  // Position at the bottom edge of the dice
                    .size(24.dp)
                    .background(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Locked dice",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}