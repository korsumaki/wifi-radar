package com.korsumaki.wifiradar

import android.app.Activity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.korsumaki.wifiradar.ui.theme.WiFiRadarTheme

/*
* TODO
*  - timer for iteration
*  - scale to middle of the screen
*
* */

@Composable
fun WifiRadarScreen(activity: Activity) {
    val scanList = remember { mutableStateListOf<WifiAp>() }
    val scanner = WiFiRadarScanner(activity = activity, scanList)
    /*
    ScanListScreen(
        scanList,
        onScanButtonPress = {
            scanner.scan()
        }
    )
    */

    val forceGraph by remember { mutableStateOf<ForceGraph>(ForceGraph()) }

    val centerNode = ForceNode("Center")
    centerNode.coordinate = Coordinate(600f,600f)

    var currentLocationNodeNumber by remember { mutableStateOf(0) }

    MapScreen(
        forceGraph = forceGraph,
        onScanButtonPress = { scanner.scan() },
        onAddNodeButtonPress = {
            println("onAddNodeButtonPress")

            if (scanList.size > 0) {
                val previousLocationNode = ForceNode(id = "Loc-$currentLocationNodeNumber")
                currentLocationNodeNumber++
                val currentLocationNode = ForceNode(id = "Loc-$currentLocationNodeNumber")

                // = estimated distance from previous location
                currentLocationNode.coordinate = Coordinate(300f, currentLocationNodeNumber*100f)

                if (forceGraph.nodeList.contains(previousLocationNode)) {

                    forceGraph.connectNodesWithRelation(
                        currentLocationNode,
                        previousLocationNode,
                        ForceRelation(100f) // = estimated distance from previous location
                    )
                }
                else {
                    // Add first node
                    forceGraph.nodeList.add(currentLocationNode)
                }

                addNodesFromScanList(forceGraph, currentLocationNode, scanList)
                scanList.clear()
            }
        },
        onIterateButtonPress = {
            println("onIterateButtonPress")
            forceGraph.iterateRelations()
        }
    )
}

fun addNodesFromScanList(forceGraph: ForceGraph, currentLocationNode: ForceNode, scanList: List<WifiAp>) {
    for (scanResult in scanList) {
        val node = ForceNode(id = scanResult.mac)
        println("Adding: $scanResult with distance ${scanResult.getDistance()}")

        // TODO moving node sideways with random makes distance longer that it should be.
        //  With many WifiAps it create too high force and too high velocity...
        val xRange = -100..100
        node.name = scanResult.name
        node.coordinate = Coordinate(
            x = currentLocationNode.coordinate.x + xRange.random(),
            y = currentLocationNode.coordinate.y + scanResult.getDistance()
        )
        forceGraph.connectNodesWithRelation(
            node1 = currentLocationNode,
            node2 = node,
            relation = ForceRelation(scanResult.getDistance())
        )
    }
    println("Current nodes:")
    for (node in forceGraph.nodeList) {
        println(node)
    }
    println("with ${forceGraph.relationList.size} relations")
}

@Composable
fun MapScreen(forceGraph: ForceGraph, onScanButtonPress: () -> Unit, onAddNodeButtonPress: () -> Unit, onIterateButtonPress: () -> Unit) {
    Column {
        Text(
            text = "WiFi Map",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onScanButtonPress,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Scan")
            }
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
                if (node.id.contains("Loc-")) {
                    drawCircle(Color.Blue, center = Offset(node.coordinate.x, node.coordinate.y), radius = 15f)
                }
                else {
                    drawCircle(Color.Red, center = Offset(node.coordinate.x, node.coordinate.y), radius = 20f)
                }
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
    val forceGraph by remember { mutableStateOf<ForceGraph>(ForceGraph()) }

    WiFiRadarTheme {
        val centerNode = ForceNode("Center")
        centerNode.coordinate = Coordinate(600f,600f)

        MapScreen(
            forceGraph = forceGraph,
            onScanButtonPress = { },
            onAddNodeButtonPress = {
                println("onAddNodeButtonPress")
                val coordinateRange = 200..1000
                val newNode = ForceNode(id = "new-${forceGraph.nodeList.size}")
                newNode.coordinate = Coordinate(coordinateRange.random().toFloat(), coordinateRange.random().toFloat())

                forceGraph.connectNodesWithRelation(centerNode, newNode, ForceRelation(300f))
                if (forceGraph.nodeList.size > 4) {
                    val node1 = forceGraph.nodeList.random()
                    val node2 = forceGraph.nodeList.random()
                    if (node1 != node2) {
                        forceGraph.connectNodesWithRelation(node1, node2, ForceRelation(150f))
                    }
                }
            },
            onIterateButtonPress = {
                println("onIterateButtonPress")
                forceGraph.iterateRelations()
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
                WifiAp(mac="eka"),
                WifiAp(mac="toka"),
                WifiAp(mac="kolmas")
            ),
            onScanButtonPress = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ScanListItemPreview() {
    WiFiRadarTheme {
        ScanListItem(WifiAp(mac="WiFi 1"))
    }
}
