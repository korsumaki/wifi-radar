package com.korsumaki.wifiradar

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager

class WiFiRadarScanner(val activity: Activity, var scanList: MutableList<WifiAp>) {
    val wifiManager = activity.getSystemService(Context.WIFI_SERVICE) as WifiManager

    var rationaleTitleForPermission = "Location service is required"
    var rationaleForPermission = "Location service is required to listen Wifi access points. " +
            "This App is constructing map based on Wifi Access Points."

    val locationPermissionController = PermissionController(activity = activity,
        permissionList = listOf(Manifest.permission.ACCESS_FINE_LOCATION),
        rationaleTitle = rationaleTitleForPermission,
        rationale = rationaleForPermission)

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
        if (!locationPermissionController.checkPermission()) {
            return
        }

        if (firstCall) {
            // TODO move these to some constructor etc.
            val intentFilter = IntentFilter()
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            activity.registerReceiver(wifiScanReceiver, intentFilter)
            /*
            * TODO: Fix intent receiver unregisterReceiver()
            *   Activity com.korsumaki.wifiradar.MainActivity has leaked IntentReceiver com.korsumaki.wifiradar.WiFiRadarScanner$wifiScanReceiver$1@7a2f7f5 that was originally registered here. Are you missing a call to unregisterReceiver()?
            *   android.app.IntentReceiverLeaked: Activity com.korsumaki.wifiradar.MainActivity has leaked IntentReceiver com.korsumaki.wifiradar.WiFiRadarScanner$wifiScanReceiver$1@7a2f7f5 that was originally registered here. Are you missing a call to unregisterReceiver()?
            *       at com.korsumaki.wifiradar.WiFiRadarScanner.scan(WiFiRadarScanner.kt:30)
            * */
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