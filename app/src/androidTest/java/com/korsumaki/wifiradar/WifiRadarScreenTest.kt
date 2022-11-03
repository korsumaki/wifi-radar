package com.korsumaki.wifiradar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.korsumaki.wifiradar.ui.theme.WiFiRadarTheme

import org.junit.Test

import org.junit.Rule

/**
 * Compose test, which will execute on an Android device.
 *
 * See [testing documentation](https://developer.android.com/jetpack/compose/testing).
 *
 */
class WifiRadarScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @ExperimentalMaterial3Api
    @Test
    fun test_MapScreen() {
        // TODO change testcase to test MapScreen instead of MapScreenPreview
        composeTestRule.setContent {
            WiFiRadarTheme {
                MapScreenPreview()
            }
        }
        //composeTestRule.onNodeWithText("Scan").assertIsDisplayed()
        composeTestRule.onNodeWithText("0 nodes, 0 relations").assertIsDisplayed()
        //composeTestRule.onNodeWithText("Scan").performClick()
        //composeTestRule.onNodeWithText("2 nodes, 1 relations").assertIsDisplayed()
    }
}