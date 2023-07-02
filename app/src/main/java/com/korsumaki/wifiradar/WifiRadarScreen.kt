package com.korsumaki.wifiradar

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.color.MaterialColors
import com.korsumaki.wifiradar.ui.theme.Typography
import kotlinx.coroutines.launch


/**
 * TopBar for application
 */
@ExperimentalMaterial3Api // required by: TopAppBar
@Composable
fun WifiRadarTopBar(zoomIn: () -> Unit, zoomOut: () -> Unit, clearMap: () -> Unit, onMenuClick: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name)) },
        navigationIcon = {
            IconButton(onClick = { onMenuClick() } ) {
                Icon(Icons.Outlined.Menu, stringResource(id = R.string.menu_content_description))
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
@ExperimentalTextApi // required by: drawText, textMeasurer
@Composable
fun WifiRadarContent(forceGraph: ForceGraph, scaleFactor: Float, iterationCount: Int, demoMode: Boolean) {
    // NOTE iterationCount is required in parameter to get Compose updated.
    val textMeasurer = rememberTextMeasurer()
    val emptyScreenNote = stringResource(id = R.string.empty_screen_note)
    val demoModeText = stringResource(id = R.string.menu_demo_mode)

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

        if (demoMode) {
            drawText(
                textMeasurer = textMeasurer,
                text = demoModeText,
                style = Typography.titleLarge,
                topLeft = Offset(10f, 10f)
            )
        }

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

            // If nodeList is empty, show note to describe why screen is empty.
            if (forceGraph.nodeList.isEmpty()) {
                drawText(
                    textMeasurer = textMeasurer,
                    text = emptyScreenNote,
                    style = Typography.titleLarge,
                    topLeft = Offset(centerX/5, centerY/2)
                )
            }

            for (node in forceGraph.nodeList) {
                val nodeLocation = Offset(
                    node.coordinate.x*scaleFactor + centerX,
                    node.coordinate.y*scaleFactor + centerY)
                when (node.type) {
                    ForceNode.Type.ROUTE ->
                        drawCircle(routeColorHarmonized,
                            center = nodeLocation,
                            radius = 15f)

                    ForceNode.Type.WIFI -> {
                        drawCircle(
                            wifiColorHarmonized,
                            center = nodeLocation,
                            radius = 20f,
                            alpha = 0.3f)
                        try {
                            drawText(
                                textMeasurer = textMeasurer, //apNameTextMeasurer,
                                text = node.name,
                                style = Typography.bodySmall,
                                topLeft = nodeLocation,
                                maxLines = 1)
                        }
                        catch (_: IllegalArgumentException) {
                            // Ignore exceptions: "java.lang.IllegalArgumentException: maxWidth(-46) must be >= than minWidth(0)"
                            // This happens sometimes when trying to draw text outside of the canvas
                        }
                    }

                    ForceNode.Type.BT ->
                        drawCircle(bluetoothColor,
                            center = nodeLocation,
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

@ExperimentalMaterial3Api // required by: WifiRadarTopBar
@ExperimentalTextApi // required by: WifiRadarContent
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
                iterationCount = wifiRadarViewModel.iterationCounter,
                demoMode = wifiRadarViewModel.isDemoModeEnabled
            )
        }
    }
}

@Composable
fun DrawerContent(onOpenSourceLicences: () -> Unit, onPrivacyNotice: () -> Unit, isDemoModeEnabled: Boolean, onDemoModeChanged: () -> Unit) {
    DropdownMenuItem(
        text = { Text(stringResource(id = R.string.menu_demo_mode)) },
        onClick = { onDemoModeChanged() },
        trailingIcon = {
            if (isDemoModeEnabled) {
                Icon(
                    Icons.Outlined.Check,
                    stringResource(id = R.string.demo_mode_enabled_content_description)
                )
            }
        }
    )
    DropdownMenuItem(
        text = { Text(stringResource(id = R.string.menu_open_source_licenses)) },
        onClick = { onOpenSourceLicences() },
    )
    DropdownMenuItem(
        text = { Text(stringResource(id = R.string.menu_privacy_notice)) },
        onClick = { onPrivacyNotice() },
    )
    DropdownMenuItem(
        text = { Text(stringResource(id = R.string.menu_version, BuildConfig.VERSION_NAME)) },
        onClick = {  },
        enabled = false,
    )
}


@ExperimentalMaterial3Api // required by: WifiRadarScaffold
@ExperimentalTextApi // required by: WifiRadarScaffold
@Composable
fun WifiRadarModalNavigationDrawer(wifiRadarViewModel: WifiRadarViewModel = WifiRadarViewModel(), content: @Composable ColumnScope.() -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                content = {
                    Row {
                        IconButton(onClick = { scope.launch { drawerState.close() } } ) {
                            Icon(Icons.Outlined.ArrowBack, stringResource(id = R.string.back_content_description))
                        }
                        Text(
                            stringResource(R.string.app_name),
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    //Divider()
                    content()
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
@ExperimentalMaterial3Api // required by: WifiRadarTopBar
@Composable
fun WifiRadarTopBarPreview() {
    WifiRadarTopBar(
        zoomIn = {},
        zoomOut = {},
        clearMap = {},
        onMenuClick = {}
    )
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@ExperimentalMaterial3Api // required by: WifiRadarScaffold
@ExperimentalTextApi // required by: WifiRadarScaffold
@Composable
fun WifiRadarScaffoldPreview() {
    WifiRadarScaffold(wifiRadarViewModel = WifiRadarViewModel(), onMenuClick = {})
}


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun DrawerContentPreview() {
    var demoMode: Boolean by rememberSaveable { mutableStateOf(false) }

    Column {
        DrawerContent({}, {}, isDemoModeEnabled = demoMode, onDemoModeChanged = { demoMode = !demoMode })
    }
}


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@ExperimentalMaterial3Api // required by: WifiRadarModalNavigationDrawer
@ExperimentalTextApi // required by: WifiRadarModalNavigationDrawer
@Composable
fun WifiRadarModalNavigationDrawerPreview() {
    WifiRadarModalNavigationDrawer(
        content = {
            DrawerContent({}, {}, isDemoModeEnabled = true, {})
        }
    )
}
