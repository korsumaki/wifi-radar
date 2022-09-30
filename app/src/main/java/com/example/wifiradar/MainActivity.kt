package com.example.wifiradar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.wifiradar.ui.theme.WiFiRadarTheme

/*
* TODO functionality
*  - ScanList on UI
*  - WiFi scanning
*  - distance calculation
*  - map chart element on UI
*  - more advanced methods for getting distance
*  - BT scanning
*
* TODO application
*  - icon
*  - enable crashlytics
*  - publish to play store
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
                    ScanListScreen()
                }
            }
        }
    }
}

@Composable
fun ScanListItem(name: String, strength: Int) {
    Text(
        text = "$name (rssi=$strength)",
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
fun ScanListScreen() {
    Column {
        Text(
            text = "WiFi Radar",
            style = MaterialTheme.typography.titleMedium,
        )
        ScanListItem("AP2", 64)
        ScanListItem("My WiFi network", 37)
        ScanListItem("Some other SSID", 49)
    }
}


@Preview(showBackground = true)
@Composable
fun ScanListItemPreview() {
    WiFiRadarTheme {
        ScanListItem("WiFi 1", 54)
    }
}

@Preview(showBackground = true)
@Composable
fun ScanListPagePreview() {
    WiFiRadarTheme {
        ScanListScreen()
    }
}