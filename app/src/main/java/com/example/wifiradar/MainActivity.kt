package com.example.wifiradar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.wifiradar.ui.theme.WiFiRadarTheme

/*
* TODO functionality
*  + ScanList on UI
*  - WiFi scanning
*  - distance calculation
*  - map chart element on UI
*  - more advanced methods for getting distance
*       https://developer.android.com/guide/topics/connectivity/wifi-rtt
*       https://github.com/Plinzen/android-rttmanager-sample
*  - BT scanning
*
* TODO application
*  - remember list during orientation change
*  - scroll scanList
*  - viewModel
*  + separate
*       + MainActivity
*       + Composables
*       + Data classes
*  - App layout (Scaffold)
*  - icon
*  - enable crashlytics
*  - enable leakcanary
*  - publish to play store
*  - compose testing?
*       https://developer.android.com/jetpack/compose/testing
*
* TODO project/github
*  - project description
*  - readme
*  - package name -> korsumaki
*
*/

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WiFiRadarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WifiRadarScreen()
                }
            }
        }
    }
}
