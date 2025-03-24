package com.sameerasw.dicing

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DicingTopBar(title: String = "Dicing") {
    // Top app bar with the app icon and title
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.dice6),
                    contentDescription = "Dicing",
                    modifier = Modifier
                        .padding(16.dp)
                        .size(32.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f).value),
        )
    )
}