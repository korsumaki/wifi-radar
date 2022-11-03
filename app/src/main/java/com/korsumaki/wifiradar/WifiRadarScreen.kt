package com.korsumaki.wifiradar

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.color.MaterialColors
import com.korsumaki.wifiradar.ui.theme.WiFiRadarTheme


@ExperimentalMaterial3Api
@Composable
fun WifiRadarScreen(wifiRadarViewModel: WifiRadarViewModel, onScanButtonPress: () -> Unit, onSaveButtonPress: (String) -> Unit) {
    MapScreen(
        forceGraph = wifiRadarViewModel.forceGraph,
        onScanButtonPress = onScanButtonPress,
        onSaveButtonPress = { onSaveButtonPress(it) },
        onClearButtonPress = { wifiRadarViewModel.clearMap() },
        nodeCount = wifiRadarViewModel.forceNodeCount,
        relationCount = wifiRadarViewModel.forceRelationCount
    )
}

@ExperimentalMaterial3Api
@Composable
fun MapScreen(forceGraph: ForceGraph, onScanButtonPress: () -> Unit, onSaveButtonPress: (String) -> Unit, onClearButtonPress: () -> Unit, nodeCount: Int, relationCount: Int) {
    var scaleFactor: Float by rememberSaveable { mutableStateOf(3.0f) }
    var filenamePostFix by remember { mutableStateOf("ScanList1.txt") }
    var fileStoreButtonText by remember { mutableStateOf("File store") }
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
                onClick = {
                    onSaveButtonPress( filenamePostFix )
                    fileStoreButtonText = filenamePostFix
                          },
                modifier = Modifier.padding(4.dp)
            ) {
                Text(text = fileStoreButtonText)
            }
            Button(
                onClick = { scaleFactor *= 1.1f },
                modifier = Modifier.padding(4.dp)
            ) {
                Text(text = "+")
            }
            Button(
                onClick = { scaleFactor *= 0.9f },
                modifier = Modifier.padding(4.dp)
            ) {
                Text(text = "-")
            }
            Button(
                onClick = onClearButtonPress,
                modifier = Modifier.padding(4.dp)
            ) {
                Text(text = "Clr")
            }
        }
        TextField(
            value = filenamePostFix,
            onValueChange = { filenamePostFix = it }
        )
        Text(
            text = "$nodeCount nodes, $relationCount relations",
            modifier = Modifier.padding(4.dp)
        )
        val wifiColor = Color.Red
        val bluetoothColor = Color.Cyan
        val routeColor = Color.Green
        val relationColor = Color.Gray

        val wifiColorHarmonized = Color(
            MaterialColors.harmonize(
                wifiColor.toArgb(),
                colorScheme.primary.toArgb()
            ) )
        val routeColorHarmonized = Color(
            MaterialColors.harmonize(
                routeColor.toArgb(),
                colorScheme.primary.toArgb()
            ) )
        val relationColorHarmonized = Color(
            MaterialColors.harmonize(
                relationColor.toArgb(),
                colorScheme.primary.toArgb()
            ) )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val centerX = canvasWidth/2
            val centerY = canvasHeight/2

            synchronized(forceGraph) {
                // Prevent modification of ForceGraph during drawing (due iteration or new scan results)
                for (relation in forceGraph.relationList) {
                    if (relation.coordinateList.size == 2) {
                        drawLine(
                            color = relationColorHarmonized,
                            start = Offset(
                                relation.coordinateList[0].x*scaleFactor + centerX,
                                relation.coordinateList[0].y*scaleFactor + centerY),
                            end = Offset(
                                relation.coordinateList[1].x*scaleFactor + centerX,
                                relation.coordinateList[1].y*scaleFactor + centerY),
                            strokeWidth = 5f
                        )
                    }
                }

                for (node in forceGraph.nodeList) {
                    when (node.type) {
                        ForceNode.Type.ROUTE ->
                            drawCircle(routeColorHarmonized,
                                center = Offset(
                                    node.coordinate.x*scaleFactor + centerX,
                                    node.coordinate.y*scaleFactor + centerY),
                                radius = 15f)

                        ForceNode.Type.WIFI ->
                            drawCircle(wifiColorHarmonized,
                                center = Offset(
                                    node.coordinate.x*scaleFactor + centerX,
                                    node.coordinate.y*scaleFactor + centerY),
                                radius = 20f,
                                alpha = 0.3f)

                        ForceNode.Type.BT ->
                            drawCircle(bluetoothColor,
                                center = Offset(
                                    node.coordinate.x*scaleFactor + centerX,
                                    node.coordinate.y*scaleFactor + centerY),
                                radius = 20f,
                                alpha = 0.5f)
                    }
                }

                // Draw lines between route points
                val routeList = forceGraph.nodeList.filter { it.type == ForceNode.Type.ROUTE }
                lateinit var startNode: ForceNode
                routeList.forEachIndexed { index, forceNode ->
                    if (index == 0) {
                        startNode = forceNode
                    }
                    else {
                        drawLine(
                            color = routeColorHarmonized,
                            start = Offset(
                                startNode.coordinate.x*scaleFactor + centerX,
                                startNode.coordinate.y*scaleFactor + centerY),
                            end = Offset(
                                forceNode.coordinate.x*scaleFactor + centerX,
                                forceNode.coordinate.y*scaleFactor + centerY),
                            strokeWidth = 10f
                        )
                        startNode = forceNode

                        // Draw last route node with bigger circle
                        if (index == routeList.size-1) {
                            drawCircle(routeColorHarmonized,
                                center = Offset(
                                    forceNode.coordinate.x*scaleFactor + centerX,
                                    forceNode.coordinate.y*scaleFactor + centerY),
                                radius = 35f,
                                alpha = 0.4f)
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@ExperimentalMaterial3Api
@Composable
fun MapScreenPreview() {
    // TODO update to use ViewModel
    val forceGraph by remember { mutableStateOf(ForceGraph()) }

    WiFiRadarTheme {
        val centerNode = ForceNode("Center")
        centerNode.type = ForceNode.Type.ROUTE
        centerNode.coordinate = Coordinate(0f,0f)

        var forceNodeCount by remember { mutableStateOf(0) }
        var forceRelationCount by remember { mutableStateOf(0) }

        MapScreen(
            forceGraph = forceGraph,
            onSaveButtonPress = {
                println("onScanButtonPress")
                val coordinateRange = -150..150
                val newNode = ForceNode(id = "new-${forceGraph.nodeList.size}")
                newNode.type = ForceNode.Type.values().random()
                newNode.coordinate = Coordinate(coordinateRange.random().toFloat(), coordinateRange.random().toFloat())

                forceGraph.connectNodesWithRelation(centerNode, newNode, ForceRelation(200f))
                if (forceGraph.nodeList.size > 4) {
                    val node1 = forceGraph.nodeList.random()
                    val node2 = forceGraph.nodeList.random()
                    if (node1 != node2) {
                        forceGraph.connectNodesWithRelation(node1, node2, ForceRelation(100f))
                    }
                }
                forceNodeCount = forceGraph.nodeList.size
                forceRelationCount = forceGraph.relationList.size
                forceGraph.iterateRelations()
            },
            onScanButtonPress = { },
            onClearButtonPress = {
                forceGraph.nodeList.clear()
                forceGraph.relationList.clear()
            },
            nodeCount = forceNodeCount,
            relationCount = forceRelationCount
        )
    }
}
