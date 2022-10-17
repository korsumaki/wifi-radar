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

    init {
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        activity.registerReceiver(wifiScanReceiver, intentFilter)
    }

    /**
     * Close WiFiRadarScanner instance
     */
    fun close() {
        activity.unregisterReceiver(wifiScanReceiver)
    }

    /*
    * TODO BUG: When orientation is changed and close() is called from Activity, next scanResults
    *  does not call scanDoneCallback() before scan() is called next time.
    *  Possible solution:
    *   - scanDoneCallback is got when WiFiRadarScanner instance is created
    */
    lateinit var scanDoneCallback: (Boolean) -> Unit

    fun scan(_scanDoneCallback: (isSuccess: Boolean) -> Unit) {
        if (!locationPermissionController.checkPermission()) {
            return
        }
        scanDoneCallback = _scanDoneCallback

        scanList.clear() // Clear old entries from list
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
        for (result in results) {
            println("SSID: ${result.SSID}, BSSID: ${result.BSSID}, level: ${result.level}, frequency: ${result.frequency}")
            val ap = WifiAp(mac = result.BSSID)
            ap.name=result.SSID
            ap.strength = result.level
            ap.frequency = result.frequency
            scanList.add(ap)
        }

        // Check lateinit scanDoneCallback before use.
        if (this::scanDoneCallback.isInitialized) {
            scanDoneCallback(true)
        }
    }

    private fun scanFailure() {
        //val results = wifiManager.scanResults
        println("scanFailure")
        //... potentially use older scan results ...

        if (this::scanDoneCallback.isInitialized) {
            scanDoneCallback(false)
        }
    }
}