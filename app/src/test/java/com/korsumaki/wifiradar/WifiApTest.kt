package com.korsumaki.wifiradar

import org.junit.Test
import com.google.common.truth.Truth.assertThat

/**
 * Unit tests for WifiAp classes
 */
class WifiApTest {

    // ====================
    // Coordinate
    // ====================
    @Test
    fun test_WifiAp_create() {
        val ap = WifiAp("identifier")
        assertThat(ap.mac).isEqualTo("identifier")
    }

    @Test
    fun test_WifiAp_getDistance() {
        val ap = WifiAp("identifier")

        // Example: frequency = 2412MHz, signalLevel = -57dbm, result = 7.000397427391188m
        assertThat(ap.calculateDistance(-57, 2412)).isWithin(0.001f).of(7.0f)

        // Some other values
        assertThat(ap.calculateDistance(-39, 2412)).isWithin(0.001f).of(0.8812f)
        assertThat(ap.calculateDistance(-48, 5220)).isWithin(0.001f).of(1.1477f)
        assertThat(ap.calculateDistance(-88, 2437)).isWithin(0.001f).of(245.835f)
    }
}

