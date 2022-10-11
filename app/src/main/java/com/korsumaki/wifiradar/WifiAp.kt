package com.korsumaki.wifiradar

import kotlin.math.abs

data class WifiAp(val mac: String = "") {
    var name = ""
    var strength = -100

    // TODO implement better estimation
    fun getDistance(): Float {
        return abs(strength)/3f * 10
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
