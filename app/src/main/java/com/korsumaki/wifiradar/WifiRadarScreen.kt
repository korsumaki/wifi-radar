package com.korsumaki.wifiradar

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.korsumaki.wifiradar.ui.theme.WiFiRadarTheme


@Composable
fun WifiRadarScreen(activity: Activity) {
    /*
    val scanList = remember { mutableStateListOf<WifiAp>() }
    val scanner = WiFiRadarScanner(activity = activity, scanList)

    ScanListScreen(
        scanList,
        onScanButtonPress = {
            scanner.scan()
        }
    )
    */

    val forceGraph = remember { mutableStateOf<ForceGraph>(ForceGraph()) }

    val centerNode = ForceNode("Center", Coordinate(600f,600f))

    MapScreen(
        forceGraph = forceGraph.value,
        onAddNodeButtonPress = {
            println("onAddNodeButtonPress")
            val coordinateRange = 200..1000
            val newNode = ForceNode(
                id = "new-${forceGraph.value.nodeList.size}",
                coordinate = Coordinate(coordinateRange.random().toFloat(), coordinateRange.random().toFloat()))

            forceGraph.value.connectNodesWithRelation(centerNode, newNode, ForceRelation(300f))
        },
        onIterateButtonPress = {
            println("onIterateButtonPress")
            forceGraph.value.iterateRelations()
        }
    )
}


@Composable
fun MapScreen(forceGraph: ForceGraph, onAddNodeButtonPress: () -> Unit, onIterateButtonPress: () -> Unit) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "WiFi Map",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
            Button(
                onClick = { onAddNodeButtonPress() },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Add node")
            }
            Button(
                onClick = { onIterateButtonPress() },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Iterate")
            }
        }
        Canvas(modifier = Modifier.fillMaxSize()) {

            for (relation in forceGraph.relationList) {
                if (relation.coordinateList.size == 2) {
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(relation.coordinateList[0].x, relation.coordinateList[0].y),
                        end = Offset(relation.coordinateList[1].x, relation.coordinateList[1].y),
                        strokeWidth = 5f
                    )
                }
            }

            for (node in forceGraph.nodeList) {
                drawCircle(Color.Red, center = Offset(node.coordinate.x, node.coordinate.y), radius = 20f)
            }
        }
    }
}



@Composable
fun ScanListScreen(wifiApList: List<WifiAp>, onScanButtonPress: () -> Unit ) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "WiFi Radar",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
            Button(
                onClick = onScanButtonPress,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Scan")
            }
        }
        LazyColumn {
            items(wifiApList) { wifiAp ->
                ScanListItem(ap = wifiAp)
            }
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
fun MapScreenPreview() {
    val forceGraph = remember { mutableStateOf<ForceGraph>(ForceGraph()) }

    WiFiRadarTheme {
        val centerNode = ForceNode("Center", Coordinate(600f,600f))

        MapScreen(
            forceGraph = forceGraph.value,
            onAddNodeButtonPress = {
                println("onAddNodeButtonPress")
                val coordinateRange = 200..1000
                val newNode = ForceNode(
                    id = "new-${forceGraph.value.nodeList.size}",
                    coordinate = Coordinate(coordinateRange.random().toFloat(), coordinateRange.random().toFloat()))

                forceGraph.value.connectNodesWithRelation(centerNode, newNode, ForceRelation(300f))
            },
            onIterateButtonPress = {
                println("onIterateButtonPress")
                forceGraph.value.iterateRelations()
            }
        )
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
