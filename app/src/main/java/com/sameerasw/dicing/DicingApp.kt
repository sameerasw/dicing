package com.sameerasw.dicing

import androidx.compose.runtime.Composable

@Composable
fun DicingApp(onNavigateToGame: (Int) -> Unit) {
    MainMenu(onNavigateToGame = onNavigateToGame)
}