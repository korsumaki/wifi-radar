package com.korsumaki.wifiradar

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build

class WiFiRadarScanner(val activity: Activity, private var scanList: MutableList<WifiAp>) {
    private val wifiManager = activity.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val rationaleTitleForPermission = "Location service is required"
    private val rationaleForPermission = "Location service is required to listen Wifi access points. " +
            "This App is constructing map based on Wifi Access Points."

    private val locationPermissionController = PermissionController(activity = activity,
        permissionList = listOf(Manifest.permission.ACCESS_FINE_LOCATION),
        rationaleTitle = rationaleTitleForPermission,
        rationale = rationaleForPermission)

    private val wifiScanReceiver = object : BroadcastReceiver() {

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
    * BUG: When orientation is changed and close() is called from Activity, next scanResults
    *  does not call scanDoneCallback() before scan() is called next time.
    *   - This is not so big problem, when next scan is triggered with timer
    *  Possible solution:
    *   - scanDoneCallback is got when WiFiRadarScanner instance is created
    */
    lateinit var scanDoneCallback: (Boolean) -> Unit

    /**
     * Permission request is triggered, and we should not trigger it again (and thus not trigger Wifi scan)
     */
    private var permissionRequestOngoing = false

    fun scan(_scanDoneCallback: (isSuccess: Boolean) -> Unit) {

        // If permission request is already ongoing, check permissions, but do not initiate new permission dialog.
        if (permissionRequestOngoing) {
            if (!locationPermissionController.checkPermission()) {
                return // We still did not have permission, should not scan
            }
            permissionRequestOngoing = false
        }

        if (!locationPermissionController.checkPermissionWithRequest()) {
            permissionRequestOngoing = true
            return
        }
        scanDoneCallback = _scanDoneCallback

        scanList.clear() // Clear old entries from list

        @Suppress("Deprecation") // NOTE: Using deprecated startScan() as this app depends on scan results!
        val success = wifiManager.startScan()
        if (!success) {
            println("wifiManager.startScan() failed")
            // scan failure handling
            scanFailure()
        }
    }

    private fun scanSuccess() {
        if (!locationPermissionController.checkPermission()) {
            return
        }
        val results = wifiManager.scanResults
        println("scanSuccess")
        for (result in results) {
            val ssid = getSSID(result)
            println("SSID: $ssid, BSSID: ${result.BSSID}, level: ${result.level}, frequency: ${result.frequency}")
            val ap = WifiAp(mac = result.BSSID)
            ap.name = ssid
            ap.strength = result.level
            ap.frequency = result.frequency
            scanList.add(ap)
        }

        // Check lateinit scanDoneCallback before use.
        if (this::scanDoneCallback.isInitialized) {
            scanDoneCallback(true)
        }
    }

    /**
     * Get SSID from scanResult
     *
     * Using new getWifiSsid() method on Android 13 (TIRAMISU).
     *
     * @param scanResult    Scan result
     * @return              String containing SSID, empty if WifiSsid does not exist
     */
    private fun getSSID(scanResult: ScanResult): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            scanResult.wifiSsid?.toString() ?: ""
        } else {
            @Suppress("Deprecation")
            scanResult.SSID
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