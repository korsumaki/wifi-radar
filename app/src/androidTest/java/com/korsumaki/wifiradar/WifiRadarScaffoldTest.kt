package com.korsumaki.wifiradar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
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
    @Test
    fun test_WifiRadarScaffold() {
        composeTestRule.setContent {
            WifiRadarTheme {
                WifiRadarScaffold(wifiRadarViewModel = WifiRadarViewModel())
            }
        }
        composeTestRule.onNodeWithContentDescription("Zoom in").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithContentDescription("Zoom out").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithContentDescription("Clear screen").assertIsDisplayed().performClick()
    }
}