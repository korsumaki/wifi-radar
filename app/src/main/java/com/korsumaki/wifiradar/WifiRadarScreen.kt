package com.korsumaki.wifiradar

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.launch


/**
 * TopBar for application
 */
@ExperimentalMaterial3Api
@Composable
fun WifiRadarTopBar(zoomIn: () -> Unit, zoomOut: () -> Unit, clearMap: () -> Unit, onMenuClick: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name)) },
        navigationIcon = {
            IconButton(onClick = { onMenuClick() } ) {
                Icon(Icons.Outlined.Menu, null)
            }
        },
        actions = {
            IconButton(onClick = zoomIn) {
                Icon(
                    painterResource(id = R.drawable.outline_zoom_in_24),
                    stringResource(id = R.string.zoom_in_content_description))
            }
            IconButton(onClick = zoomOut) {
                Icon(
                    painterResource(id = R.drawable.outline_zoom_out_24),
                    stringResource(id = R.string.zoom_out_content_description))
            }
            IconButton(onClick = clearMap) {
                Icon(
                    Icons.Outlined.Delete,
                    stringResource(id = R.string.clear_screen_content_description))
            }
        }
    )
}

@Suppress("UNUSED_PARAMETER") // iterationCount
@ExperimentalMaterial3Api
@Composable
fun WifiRadarContent(forceGraph: ForceGraph, scaleFactor: Float, iterationCount: Int) {
    // NOTE iterationCount is required in parameter to get Compose updated.

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

@ExperimentalMaterial3Api
@Composable
fun WifiRadarScaffold(wifiRadarViewModel: WifiRadarViewModel, onMenuClick: () -> Unit) {
    var scaleFactor: Float by rememberSaveable { mutableStateOf(3.0f) }

    Scaffold(
        topBar =  { WifiRadarTopBar(
            zoomIn = { scaleFactor *= 1.1f },
            zoomOut = { scaleFactor *= 0.9f },
            clearMap = { wifiRadarViewModel.clearMap() },
            onMenuClick = { onMenuClick() }
            )
        },
        containerColor = Color.Transparent //colorScheme.primaryContainer
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            WifiRadarContent(
                forceGraph = wifiRadarViewModel.forceGraph,
                scaleFactor = scaleFactor,
                iterationCount = wifiRadarViewModel.iterationCounter)
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun DrawerContent() {
    // TODO Dummy items so far
    Text("Clear")
    Text("Open Source Licenses")
    Text("Version: 0.0.0")
    IconButton(onClick = { } ) {
        Icon(
            Icons.Outlined.Delete,
            stringResource(id = R.string.clear_screen_content_description))
    }
}


@ExperimentalMaterial3Api
@Composable
fun WifiRadarModalNavigationDrawer(wifiRadarViewModel: WifiRadarViewModel = WifiRadarViewModel(), drawerContent: @Composable ColumnScope.() -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                content = {
                    Text("Header line", modifier = Modifier
                        .padding(16.dp)
                    ) // TODO Add icon and app name?
                    //Divider()
                    drawerContent()
                }
            )
        },
        content = {
            WifiRadarScaffold(
                wifiRadarViewModel = wifiRadarViewModel,
                onMenuClick = { scope.launch { drawerState.open() }  }
            )
        }
    )
}



@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@ExperimentalMaterial3Api
@Composable
fun WifiRadarTopBarPreview() {
    WifiRadarTopBar(
        zoomIn = {},
        zoomOut = {},
        clearMap = {},
        onMenuClick = {}
    )
}

/*
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@ExperimentalMaterial3Api
@Composable
fun WifiRadarContentPreview() {
    WifiRadarContent(
        forceGraph = ForceGraph(),
        scaleFactor = 3.0f,
        iterationCount = 0
    )
}
*/

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@ExperimentalMaterial3Api
@Composable
fun WifiRadarScaffoldPreview() {
    WifiRadarScaffold(wifiRadarViewModel = WifiRadarViewModel(), onMenuClick = {})
}


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@ExperimentalMaterial3Api
@Composable
fun DrawerContentPreview() {
    Column {
        DrawerContent()
    }
}


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@ExperimentalMaterial3Api
@Composable
fun WifiRadarModalNavigationDrawerPreview() {
    WifiRadarModalNavigationDrawer(
        drawerContent = {
            DrawerContent()
        }
    )
}
