package com.korsumaki.wifiradar

import androidx.annotation.Keep
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class WifiAp(val mac: String = "") {
    var name = ""
    var strength: Int = -100
    var frequency: Int = 2412

    /**
     * Get estimated distance from Wifi AP
     *
     * @return  Distance in meters
     */
    fun getDistance(): Float {
        // Limit minimum frequency
        if (frequency < 500) {
            frequency = 500
        }
        return calculateDistance(strength, frequency)
    }

    /**
     * Distance calculation based on Free-space path loss (FSPL)
     *
     * https://stackoverflow.com/questions/11217674/how-to-calculate-distance-from-wifi-router-using-signal-strength
     * https://en.wikipedia.org/wiki/Free-space_path_loss#Free-space_path_loss_in_decibels
     *
     * @param signalLevelInDb   Signal level in dBm
     * @param freqInMHz         Frequency in MHz
     * @return                  Distance in meters
     */
    fun calculateDistance(signalLevelInDb: Int, freqInMHz: Int): Float {
        check(freqInMHz > 0) { "Frequency must be bigger than zero (was now $freqInMHz)"}
        val exp = (27.55f - 20f * log10(freqInMHz.toFloat()) + abs(signalLevelInDb)) / 20.0f
        return 10f.pow(exp)
    }
}

/**
 * Get random WifiAp for testing
 *
 * @return  Random generated WifiAp
 */
fun getRandomWifiAp(): WifiAp {
    val nameNumberRange = 1..10
    val strengthRange = 30..100
    val ap = WifiAp(mac = "MAC")
    ap.name = "AP-${nameNumberRange.random()}"
    ap.strength = strengthRange.random()
    println("getRandomWifiAp(): $ap")
    return ap
}
