package com.sameerasw.dicing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sameerasw.dicing.ui.theme.DicingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DicingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        val showDicingApp = rememberSaveable { mutableStateOf(true) }
                        val targetScore = rememberSaveable { mutableStateOf(101) }

                        if (showDicingApp.value) {
                            DicingApp(onNavigateToGame = { score ->
                                showDicingApp.value = false
                                targetScore.value = score
                            })
                        } else {
                            GameScreen(
                                targetScore = targetScore.value,
                                onBack = {
                                    showDicingApp.value = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}