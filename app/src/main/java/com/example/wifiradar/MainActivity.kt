package com.example.wifiradar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
*       https://developer.android.com/guide/topics/connectivity/wifi-rtt
*       https://github.com/Plinzen/android-rttmanager-sample
*  - BT scanning
*
* TODO application
*  - viewModel
*  - separate
*       - MainActivity
*       - Composables
*       - Data classes
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

data class WifiAp(val name: String, var strength: Int = 100)

@Composable
fun ScanListItem(ap: WifiAp) {
    Text(
        text = "${ap.name} (rssi=${ap.strength})",
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
fun ScanListScreen(wifiApList: List<WifiAp>, onScanButtonPress: () -> Unit ) { /*MutableList*/
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
        Button(onClick = onScanButtonPress) {
            Text(text = "Scan")
        }
    }
}

fun getRandomWifiAp(): WifiAp {
    val nameNumberRange = 1..10
    val strengthRange = 30..100
    val ap = WifiAp(name = "AP-${nameNumberRange.random()}", strength = strengthRange.random() )
    println("getRandomWifiAp(): $ap")
    return ap
}

@Composable
fun WifiRadarScreen() {
    val scanList = remember { mutableStateListOf<WifiAp>() }

    ScanListScreen(
        scanList,
        onScanButtonPress = { scanList.add(getRandomWifiAp()) }
    )
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
            listOf( //mutableListOf
                WifiAp(name="eka"),
                WifiAp(name="toka", strength = 65),
                WifiAp(name="kolmas", strength = 97)
            ),
            onScanButtonPress = { println("scan pressed") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WifiRadarScreenPreview() {
    WiFiRadarTheme {
        WifiRadarScreen()
    }
}
