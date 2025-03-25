package com.sameerasw.dicing

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.sameerasw.dicing.ui.theme.DicingTheme

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up fullscreen mode
        setupFullscreen()

        // Get the initial scores from the intent
        val initialHumanWins = intent.getIntExtra("humanWins", 0)
        val initialComputerWins = intent.getIntExtra("computerWins", 0)
        val useSmartStrategy = intent.getBooleanExtra("useSmartStrategy", true)

        setContent {
            DicingTheme {
                // Determine if we're in dark theme
                val isDarkTheme = isSystemInDarkTheme()
                // Select appropriate background based on theme
                val backgroundImage = if (isDarkTheme) R.drawable.dicebg else R.drawable.dicebg_light

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        // Background image with theme-based selection
                        Image(
                            painter = painterResource(id = backgroundImage),
                            contentDescription = "Background Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )

                        val targetScore = intent.getIntExtra("targetScore", 101)
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

    private fun setupFullscreen() {
        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Keep screen on during gameplay
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Hide system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            // Hide both the status bar and the navigation bar
            controller.hide(WindowInsetsCompat.Type.systemBars())

            // When the user swipes from edge, system bars will temporarily appear and then hide again
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-hide system bars when activity comes back into focus
        setupFullscreen()
    }
}