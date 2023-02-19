package com.korsumaki.wifiradar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.ExperimentalTextApi
import com.korsumaki.wifiradar.ui.theme.WifiRadarTheme

import org.junit.Test

import org.junit.Rule

/**
 * Compose test, which will execute on an Android device.
 *
 * See [testing documentation](https://developer.android.com/jetpack/compose/testing).
 *
 */
class WifiRadarScaffoldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @ExperimentalMaterial3Api
    @ExperimentalTextApi
    @Test
    fun test_WifiRadarScaffold() {
        var clickDetected = false
        composeTestRule.setContent {
            WifiRadarTheme {
                WifiRadarScaffold(wifiRadarViewModel = WifiRadarViewModel(), onMenuClick = { clickDetected = true })
            }
        }
        composeTestRule.onNodeWithContentDescription("Zoom in").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithContentDescription("Zoom out").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithContentDescription("Clear screen").assertIsDisplayed().performClick()

        composeTestRule.onNodeWithContentDescription("Menu").assertIsDisplayed().performClick()
        composeTestRule.waitUntil(1000) { clickDetected }
    }
}