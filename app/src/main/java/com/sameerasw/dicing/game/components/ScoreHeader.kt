package com.sameerasw.dicing.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScoreHeader(
    humanWins: Int,
    computerWins: Int,
    targetScore: Int,
    playerTotalScore: Int,
    computerTotalScore: Int
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.inverseOnSurface, MaterialTheme.shapes.medium)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display the number of human and computer wins
        Text(
            text = "H:$humanWins/C:$computerWins",
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        // Display the target score
        Text(
            text = "\uD83C\uDFAF $targetScore",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
                .padding(4.dp)
        )
        // Display the total scores of the player and the computer
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Icon(imageVector = Icons.Filled.Person, contentDescription = "Player", modifier = Modifier.size(24.dp))
            Text(" $playerTotalScore", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.Filled.Star, contentDescription = "Computer", modifier = Modifier.size(24.dp))
            Text(" $computerTotalScore", fontSize = 20.sp)
        }
    }
}