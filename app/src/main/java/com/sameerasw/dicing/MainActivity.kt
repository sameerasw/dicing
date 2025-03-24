package com.sameerasw.dicing

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sameerasw.dicing.ui.theme.DicingTheme

class MainActivity : ComponentActivity() {

    // Start game activity
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
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
                Scaffold(
                    topBar = {
                        DicingTopBar()
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
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
