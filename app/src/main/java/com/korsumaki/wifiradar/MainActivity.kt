package com.korsumaki.wifiradar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.korsumaki.wifiradar.ui.theme.WiFiRadarTheme
import java.util.*
import kotlin.concurrent.timer


class MainActivity : ComponentActivity() {

    private val wifiRadarViewModel by viewModels<WifiRadarViewModel>()

    private val scanList = ArrayList<WifiAp>()
    private lateinit var scanner: WiFiRadarScanner

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                println("Permission granted")
                // Permission is granted. Continue the action or workflow in your app.
            } else {
                println("Permission denied")
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    val requestMultiplePermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { isGrantedMap: Map<String, Boolean> ->
            println("RequestMultiplePermissions: $isGrantedMap")
            for((permission,isGranted) in isGrantedMap) {
                if (isGranted) {
                    println("$permission Permission granted")
                    // Permission is granted. Continue the action or workflow in your app.
                } else {
                    println("$permission Permission denied")
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }
        }

    private fun onScanButtonPress() {
        println("MainActivity: onScanButtonPress()")
        scanner.scan { isSuccess ->
            if (isSuccess) {
                wifiRadarViewModel.onScanSuccess(scanList)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WiFiRadarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WifiRadarScreen(
                        wifiRadarViewModel = wifiRadarViewModel,
                        onScanButtonPress =  { onScanButtonPress() }
                    )
                }
            }
        }

        // Initialize scanner (remember to close in onDestroy())
        scanner = WiFiRadarScanner(activity = this, scanList)
    }

    override fun onDestroy() {
        super.onDestroy()
        scanner.close()
    }

    override fun onStart() {
        super.onStart()
        startIterationTimer()
    }
    override fun onStop() {
        super.onStop()
        stopIterationTimer()
    }

    private lateinit var iterationTimer: Timer

    private fun startIterationTimer() {
        iterationTimer = timer(name = "IterationTimer", period = 100) {
            wifiRadarViewModel.forceGraph.iterateRelations()
            wifiRadarViewModel.onForceGraphUpdate()
        }
    }

    private fun stopIterationTimer() {
        iterationTimer.cancel()
    }
}
