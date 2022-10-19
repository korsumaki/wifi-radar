package com.korsumaki.wifiradar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


class WifiRadarViewModel : ViewModel() {
    val forceGraph by mutableStateOf(ForceGraph())
    var forceNodeCount by mutableStateOf(0)
    var forceRelationCount by mutableStateOf(0)
    var currentLocationNodeNumber by mutableStateOf(0)


    fun onScanSuccess(scanList: MutableList<WifiAp>) {
        println("WifiRadarViewModel:onScanSuccess()")

        currentLocationNodeNumber++
        addLocationAndScanList(
            scanList,
            forceGraph,
            currentLocationNodeNumber
        )
        scanList.clear()

        forceNodeCount = forceGraph.nodeList.size
        forceRelationCount = forceGraph.relationList.size
    }
}
