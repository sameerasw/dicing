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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sameerasw.dicing.ui.theme.DicingTheme

class MainActivity : ComponentActivity() {

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val humanWinsState = mutableStateOf(0)
        val computerWinsState = mutableStateOf(0)

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    if (data != null) {
                        humanWinsState.value = data.getIntExtra("humanWins", 0)
                        computerWinsState.value = data.getIntExtra("computerWins", 0)
                    }
                }
            }

        setContent {
            DicingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        val humanWins = remember { humanWinsState }
                        val computerWins = remember { computerWinsState }

                        MainMenu(
                            humanWins = humanWins.value,
                            computerWins = computerWins.value,
                            onNavigateToGame = { score ->
                                val intent = Intent(this@MainActivity, GameActivity::class.java)
                                intent.putExtra("targetScore", score)
                                intent.putExtra("humanWins", humanWins.value)
                                intent.putExtra("computerWins", computerWins.value)
                                activityResultLauncher.launch(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}