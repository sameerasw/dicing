package com.sameerasw.dicing

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sameerasw.dicing.ui.theme.DicingTheme

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get the initial scores from the intent
        val initialHumanWins = intent.getIntExtra("humanWins", 0)
        val initialComputerWins = intent.getIntExtra("computerWins", 0)
        val useSmartStrategy = intent.getBooleanExtra("useSmartStrategy", true)

        setContent {
            DicingTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        DicingTopBar(title = "Playing to ${intent.getIntExtra("targetScore", 101)}")
                    },
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        val targetScore = intent.getIntExtra("targetScore", 101) // Default value 101
                        val humanWinsState = remember { mutableIntStateOf(initialHumanWins) }
                        val computerWinsState = remember { mutableIntStateOf(initialComputerWins) }

                        GameScreen(
                            targetScore = targetScore,
                            humanWins = humanWinsState,
                            computerWins = computerWinsState,
                            useSmartStrategy = useSmartStrategy,
                            onBack = {
                                // Send the updated scores back to the main activity
                                val resultIntent = Intent()
                                resultIntent.putExtra("humanWins", humanWinsState.intValue)
                                resultIntent.putExtra("computerWins", computerWinsState.intValue)
                                setResult(RESULT_OK, resultIntent)
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
}