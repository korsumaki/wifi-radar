package com.example.wifiradar

data class WifiAp(val name: String, var strength: Int = 100)

fun getRandomWifiAp(): WifiAp {
    val nameNumberRange = 1..10
    val strengthRange = 30..100
    val ap = WifiAp(name = "AP-${nameNumberRange.random()}", strength = strengthRange.random() )
    println("getRandomWifiAp(): $ap")
    return ap
}
