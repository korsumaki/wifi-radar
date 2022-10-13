package com.korsumaki.wifiradar

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.korsumaki.wifiradar.ui.theme.WiFiRadarTheme

import org.junit.Test

import org.junit.Assert.*
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

    @Test
    fun test_ScanListScreen() {
        var isScanButtonPressed = false
        composeTestRule.setContent {
            WiFiRadarTheme {
                ScanListScreen(
                    listOf(
                        WifiAp(mac="eka"),
                        WifiAp(mac="toka"),
                        WifiAp(mac="kolmas")
                    ),
                    onScanButtonPress = { isScanButtonPressed = true }
                )
            }
        }
        composeTestRule.onNodeWithText("Scan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Scan").performClick()
        assertTrue(isScanButtonPressed)
    }
}