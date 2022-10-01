package com.example.wifiradar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.wifiradar.ui.theme.WiFiRadarTheme

/*
* TODO functionality
*  + ScanList on UI
*  - WiFi scanning
*  - distance calculation
*  - map chart element on UI
*  - more advanced methods for getting distance
*  - BT scanning
*
* TODO application
*  - App layout
*  - icon
*  - enable crashlytics
*  - publish to play store
*
* TODO project/github
*  - project description
*  - readme
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
                    ScanListScreen(
                        listOf(
                            WifiAp(name="eka", strength = 14),
                            WifiAp(name="toka", strength = 65)
                        )
                    )
                }
            }
        }
    }
}

class WifiAp(val name: String, var strength: Int = 100)

@Composable
fun ScanListItem(ap: WifiAp) {
    Text(
        text = "${ap.name} (rssi=${ap.strength})",
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
fun ScanListScreen(wifiApList: List<WifiAp>) {
    Column {
        Text(
            text = "WiFi Radar",
            style = MaterialTheme.typography.titleMedium,
        )
        LazyColumn {
            items(wifiApList) { wifiAp ->
                ScanListItem(ap = wifiAp)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ScanListItemPreview() {
    WiFiRadarTheme {
        ScanListItem(WifiAp(name="WiFi 1", strength = 54))
    }
}

@Preview(showBackground = true)
@Composable
fun ScanListPagePreview() {
    WiFiRadarTheme {
        ScanListScreen(
            listOf(
                WifiAp(name="eka"),
                WifiAp(name="toka", strength = 65),
                WifiAp(name="kolmas", strength = 97)
            )
        )
    }
}