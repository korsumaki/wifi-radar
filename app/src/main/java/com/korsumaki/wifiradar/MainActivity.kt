package com.korsumaki.wifiradar

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.korsumaki.wifiradar.BuildConfig.APPLICATION_ID
import com.korsumaki.wifiradar.ui.theme.WifiRadarTheme
import java.util.*
import kotlin.concurrent.timer


class MainActivity : ComponentActivity() {

    private val wifiRadarViewModel by viewModels<WifiRadarViewModel>()

    private val scanList = ArrayList<WifiAp>()
    private lateinit var scanner: WiFiRadarScanner

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                println("Permission granted")
                // Permission is granted. Continue the action or workflow in your app.
            } else {
                println("Permission denied")
                // Enable demo mode
                wifiRadarViewModel.isDemoModeEnabled = true
                startTimers()
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    val requestMultiplePermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { isGrantedMap: Map<String, Boolean> ->
            println("RequestMultiplePermissions: $isGrantedMap")
            for((permission,isGranted) in isGrantedMap) {
                if (isGranted) {
                    println("$permission Permission granted")
                    // Permission is granted. Continue the action or workflow in your app.
                } else {
                    println("$permission Permission denied")
                    // Enable demo mode
                    wifiRadarViewModel.isDemoModeEnabled = true
                    startTimers()
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }
        }

    private var scanListFileName = "ScanList0.txt"
    private var enableWriteScanListToFile = false

    private fun onScanTimer() {
        println("MainActivity: onScanTimer()")

        scanner.scan { isSuccess ->
            if (isSuccess) {
                if (enableWriteScanListToFile) {
                    writeScanListToFile(this.filesDir, scanListFileName, scanList)
                }
                wifiRadarViewModel.onScanSuccess(scanList)
            }
        }
    }

    private fun onDemoModeScanTimer() {
        println("MainActivity: onDemoModeScanTimer()")

        // TODO separate open/read to own function
        val inputStream = resources.openRawResource(R.raw.scanlist_demo_mode)
        val scanListFromFile = readScanListFromInputStream(inputStream).toMutableList()
        wifiRadarViewModel.onScanSuccess(scanListFromFile)
    }

    @ExperimentalMaterial3Api
    @ExperimentalTextApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WifiRadarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    WifiRadarModalNavigationDrawer(
                        wifiRadarViewModel = wifiRadarViewModel,
                        drawerContent = {
                            DrawerContent(
                                onOpenSourceLicences = { onLicenseTextView() },
                                onPrivacyNotice = { onPrivacyNoticeView() },
                                isDemoModeEnabled = wifiRadarViewModel.isDemoModeEnabled,
                                onDemoModeChanged = { onDemoModeChanged() }
                            )
                        }
                    )
                }
            }
        }

        // Initialize scanner (remember to close in onDestroy())
        scanner = WiFiRadarScanner(activity = this, scanList)
    }

    private val sharedPreferencesName = "$APPLICATION_ID-Prefs"
    private val isNoteAgreedKey = "isNoteAgreedKey"

    private fun isPermissionAndDataUsageNoteAgreed(): Boolean {
        val sharedPref = getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(isNoteAgreedKey, false)
    }

    private fun onPermissionAndDataUsageNoteAction(agreed: Boolean) {
        when (agreed) {
            true -> {
                startTimers()
                getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(isNoteAgreedKey, true)
                    .apply()
            }
            false -> {
                // Enable demo mode
                wifiRadarViewModel.isDemoModeEnabled = true
                startTimers()
            }
        }
    }

    private fun showPermissionAndDataUsageNote() {
        AlertDialog.Builder(this)
            .setTitle(R.string.permission_and_data_usage_title)
            .setMessage(R.string.permission_and_data_usage_text)
            .setPositiveButton(R.string.ok) { _, _ ->
                onPermissionAndDataUsageNoteAction(true)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                // User cancelled the dialog
                onPermissionAndDataUsageNoteAction(false)
            }
            .setCancelable(false) // No dismiss when tap outside of window
            .create()
            .show()
    }

    private fun onLicenseTextView() {
        startActivity(Intent(this, OssLicensesMenuActivity::class.java))
    }

    private fun onPrivacyNoticeView() {
        val openURL = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(getString(R.string.privacy_policy_web_link)))
        startActivity(openURL)
    }

    private fun onDemoModeChanged() {
        wifiRadarViewModel.isDemoModeEnabled = !wifiRadarViewModel.isDemoModeEnabled

        // Clear screen
        wifiRadarViewModel.clearMap()

        // Stop and start timer again, with new period
        stopScanTimer()
        startScanTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        scanner.close()
    }

    override fun onStart() {
        super.onStart()

        if (isPermissionAndDataUsageNoteAgreed()) {
            startTimers()
        }
        else {
            showPermissionAndDataUsageNote()
        }
    }
    override fun onStop() {
        super.onStop()
        stopTimers()
    }

    private lateinit var iterationTimer: Timer
    private lateinit var scanTimer: Timer

    private fun startTimers() {
        iterationTimer = timer(name = "IterationTimer", period = 100) {
            wifiRadarViewModel.forceGraph.iterateRelations()
            wifiRadarViewModel.onForceGraphUpdate()
        }
        startScanTimer()
    }

    private fun stopTimers() {
        if (this::iterationTimer.isInitialized) {
            iterationTimer.cancel()
        }
        stopScanTimer()
    }

    /**
     * Start Scan timer
     *
     * This function takes care of checking whether we are in demo mode.
     * In demo mode timer period is shorter and timer callback is different.
     */
    private fun startScanTimer() {
        if (wifiRadarViewModel.isDemoModeEnabled) {
            scanTimer = timer(name = "DemoScanTimer", period = 2 * 1000) {
                onDemoModeScanTimer()
            }
        }
        else {
            scanTimer = timer(name = "ScanTimer", period = 10 * 1000) {
                onScanTimer()
            }
        }
    }

    private fun stopScanTimer() {
        if (this::scanTimer.isInitialized) {
            scanTimer.cancel()
        }
    }
}
