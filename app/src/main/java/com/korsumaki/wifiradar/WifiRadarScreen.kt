package com.korsumaki.wifiradar

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.korsumaki.wifiradar.ui.theme.WiFiRadarTheme


@Composable
fun WifiRadarScreen(context: Context) {
    val scanList = remember { mutableStateListOf<WifiAp>() }
    val scanner = WiFiRadarScanner(context = context, scanList)

    ScanListScreen(
        scanList,
        onScanButtonPress = {
            scanner.scan()
        }
    )
}

@Composable
fun ScanListScreen(wifiApList: List<WifiAp>, onScanButtonPress: () -> Unit ) {
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

@Composable
fun ScanListItem(ap: WifiAp) {
    Text(
        text = "${ap.name} (rssi=${ap.strength})",
        style = MaterialTheme.typography.bodyMedium,
    )
}


/*@Preview(showBackground = true)
@Composable
fun WifiRadarScreenPreview() {
    WiFiRadarTheme {
        WifiRadarScreen(null)
    }
}*/

@Preview(showBackground = true)
@Composable
fun ScanListPagePreview() {
    WiFiRadarTheme {
        ScanListScreen(
            listOf(
                WifiAp(name="eka"),
                WifiAp(name="toka", strength = 65),
                WifiAp(name="kolmas", strength = 97)
            ),
            onScanButtonPress = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ScanListItemPreview() {
    WiFiRadarTheme {
        ScanListItem(WifiAp(name="WiFi 1", strength = 54))
    }
}
