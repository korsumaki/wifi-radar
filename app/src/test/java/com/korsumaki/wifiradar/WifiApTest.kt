package com.korsumaki.wifiradar

import org.junit.Test
import com.google.common.truth.Truth.assertThat

/**
 * Unit tests for WifiAp classes
 */
class WifiApTest {

    @Test
    fun test_WifiAp_create() {
        val ap = WifiAp("identifier")
        assertThat(ap.mac).isEqualTo("identifier")
    }

    @Test
    fun test_WifiAp_getRandomWifiAp() {
        val ap = getRandomWifiAp()
        assertThat(ap.mac).isNotEmpty()
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

    @Test
    fun test_WifiAp_getDistance_illegalValues() {
        val ap = WifiAp("identifier")

        assertThat(ap.calculateDistance(0, 2412)).isWithin(0.001f).of(0.00988f)
        assertThat(ap.calculateDistance(-39, 500)).isWithin(0.001f).of(4.25138f)
        ap.strength = -39
        ap.frequency = 0
        assertThat(ap.getDistance()).isWithin(0.001f).of(4.25138f)
    }
}

