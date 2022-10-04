package com.korsumaki.wifiradar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager

class WiFiRadarScanner(val context: Context, var scanList: MutableList<WifiAp>) {
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    val wifiScanReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) {
                scanSuccess()
            } else {
                scanFailure()
            }
        }
    }

    var firstCall = true
    fun scan() {
        if (firstCall) {
            // TODO move these to some constructor etc.
            val intentFilter = IntentFilter()
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            context.registerReceiver(wifiScanReceiver, intentFilter)
            firstCall = false
        }

        val success = wifiManager.startScan()
        if (!success) {
            println("wifiManager.startScan() failed")
            // scan failure handling
            scanFailure()
        }
    }

    private fun scanSuccess() {
        val results = wifiManager.scanResults
        println("scanSuccess")
        //... use new scan results ...
        for (result in results) {
            println("SSID: ${result.SSID}, BSSID: ${result.BSSID}, level: ${result.level}")
            scanList.add(WifiAp(name=result.SSID, strength = result.level))
        }
    }

    private fun scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        val results = wifiManager.scanResults
        println("scanFailure: $results")
        //... potentially use older scan results ...
    }
}