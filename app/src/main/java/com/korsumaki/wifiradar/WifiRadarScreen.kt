package com.korsumaki.wifiradar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
*       + get center coordinates
*  - limit velocity
*       - to what value?
*       - or is it better to define coordinates
*  - better initial coordinates (less random)
*  - better initial location coordinate (could be related to latest&updated location)
*
* */

@Composable
fun WifiRadarScreen(wifiRadarViewModel: WifiRadarViewModel, onScanButtonPress: () -> Unit) {
    MapScreen(
        forceGraph = wifiRadarViewModel.forceGraph,
        onScanButtonPress = onScanButtonPress,
        onIterateButtonPress = { wifiRadarViewModel.forceGraph.iterateRelations() },
        onClearButtonPress = { wifiRadarViewModel.clearMap() },
        nodeCount = wifiRadarViewModel.forceNodeCount,
        relationCount = wifiRadarViewModel.forceRelationCount
    )
}

@Composable
fun MapScreen(forceGraph: ForceGraph, onScanButtonPress: () -> Unit, onIterateButtonPress: () -> Unit, onClearButtonPress: () -> Unit, nodeCount: Int, relationCount: Int) {
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
                onClick = onIterateButtonPress,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Iterate")
            }
            Button(
                onClick = onClearButtonPress,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Clear")
            }
        }
        Text(
            text = "$nodeCount nodes, $relationCount relations",
            modifier = Modifier.padding(8.dp)
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val centerX = canvasWidth/2
            val centerY = canvasHeight/2
            val scaleFactor = 3

            for (relation in forceGraph.relationList) {
                if (relation.coordinateList.size == 2) {
                    drawLine(
                        color = Color.DarkGray,
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
                        drawCircle(Color.Green,
                            center = Offset(
                                node.coordinate.x*scaleFactor + centerX,
                                node.coordinate.y*scaleFactor + centerY),
                            radius = 15f)

                    ForceNode.Type.WIFI ->
                        drawCircle(Color.Red,
                            center = Offset(
                                node.coordinate.x*scaleFactor + centerX,
                                node.coordinate.y*scaleFactor + centerY),
                            radius = 20f,
                            alpha = 0.5f)

                    ForceNode.Type.BT ->
                        drawCircle(Color.Blue,
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
                        color = Color.Green,
                        start = Offset(
                            startNode.coordinate.x*scaleFactor + centerX,
                            startNode.coordinate.y*scaleFactor + centerY),
                        end = Offset(
                            forceNode.coordinate.x*scaleFactor + centerX,
                            forceNode.coordinate.y*scaleFactor + centerY),
                        strokeWidth = 10f
                    )
                    startNode = forceNode
                }
            }
        }
    }
}


@Preview(showBackground = true)
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
            onScanButtonPress = {
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
            },
            onIterateButtonPress = {
                println("onIterateButtonPress")
                forceGraph.iterateRelations()
            },
            onClearButtonPress = {
                forceGraph.nodeList.clear()
                forceGraph.relationList.clear()
            },
            nodeCount = forceNodeCount,
            relationCount = forceRelationCount
        )
    }
}
