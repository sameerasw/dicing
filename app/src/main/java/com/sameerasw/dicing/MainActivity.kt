package com.sameerasw.dicing

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sameerasw.dicing.ui.theme.DicingTheme

class MainActivity : ComponentActivity() {

    // Start game activity
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    @OptIn(ExperimentalMaterial3Api::class)
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
                        // Top App Bar
                        TopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.dice6),
                                        contentDescription = "Dicing",
                                        modifier = Modifier.padding(16.dp).size(32.dp)
                                    )
                                    Text(
                                        text = "Dicing",
                                        style = MaterialTheme.typography.headlineLarge,
                                    )
                                }
                            },
                            colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                                containerColor = Color(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f).value),
                            )
                        )
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
