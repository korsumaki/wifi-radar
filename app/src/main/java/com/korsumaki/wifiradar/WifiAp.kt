package com.korsumaki.wifiradar

import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow

data class WifiAp(val mac: String = "") {
    var name = ""
    var strength: Int = -100
    var frequency: Int = 2412

    /**
     * Get estimated distance from Wifi AP
     */
    fun getDistance(): Float {
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
     */
    fun calculateDistance(signalLevelInDb: Int, freqInMHz: Int): Float {
        val exp = (27.55f - 20f * log10(freqInMHz.toFloat()) + abs(signalLevelInDb)) / 20.0f
        return 10f.pow(exp)
    }
}

fun getRandomWifiAp(): WifiAp {
    val nameNumberRange = 1..10
    val strengthRange = 30..100
    val ap = WifiAp(mac = "MAC")
    ap.name = "AP-${nameNumberRange.random()}"
    ap.strength = strengthRange.random()
    println("getRandomWifiAp(): $ap")
    return ap
}
