package com.sameerasw.dicing

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.sameerasw.dicing.ui.theme.DicingTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {

    // Start game activity
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Init starting scores
        val humanWinsState = mutableIntStateOf(0)
        val computerWinsState = mutableIntStateOf(0)

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // OK result from game activity (back button pressed and score updated)
                    val data = result.data
                    if (data != null) {
                        humanWinsState.intValue = data.getIntExtra("humanWins", 0)
                        computerWinsState.intValue = data.getIntExtra("computerWins", 0)
                    }
                }
            }

        setContent {
            DicingTheme {
                // Determine if we're in dark theme
                val isDarkTheme = isSystemInDarkTheme()
                // Select appropriate background based on theme
                val backgroundImage = if (isDarkTheme) R.drawable.dicebg else R.drawable.dicebg_light
                Scaffold(
                    topBar = {
                        DicingTopBar()
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Background image with theme-based selection
                        Image(
                                painter = painterResource(id = backgroundImage),
                                contentDescription = "Background Image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer(alpha = 0.6f),
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.Center
                            )

                        // Remember the scores
                        val humanWins = rememberSaveable { humanWinsState }
                        val computerWins = rememberSaveable { computerWinsState }

                        MainMenu(
                            humanWins = humanWins.intValue,
                            computerWins = computerWins.intValue,
                            onNavigateToGame = { score, useSmartStrategy ->
                                val intent = Intent(this@MainActivity, GameActivity::class.java)

                                // Pass the target score and difficulty
                                intent.putExtra("targetScore", score)
                                intent.putExtra("useSmartStrategy", useSmartStrategy)

                                // Pass the current scores
                                intent.putExtra("humanWins", humanWins.intValue)
                                intent.putExtra("computerWins", computerWins.intValue)

                                activityResultLauncher.launch(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}