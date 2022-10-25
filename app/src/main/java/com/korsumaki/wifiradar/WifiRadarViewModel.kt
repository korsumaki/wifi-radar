package com.korsumaki.wifiradar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * As mentioned in [this discussion](https://stackoverflow.com/questions/68702217/mutablestate-in-view-model/68702455#68702455),
 * ViewModel could use either LiveData or MutableState (mutableStateOf()).
 * Difference is that ViewModel works with Android, MutableState works only with Jetpack Compose.
 */


class WifiRadarViewModel : ViewModel() {
    val forceGraph by mutableStateOf(ForceGraph())
    var forceNodeCount by mutableStateOf(0)
        private set
    var forceRelationCount by mutableStateOf(0)
        private set
    private var currentLocationNodeNumber by mutableStateOf(0)


    fun onScanSuccess(scanList: MutableList<WifiAp>) {
        println("WifiRadarViewModel:onScanSuccess()")

        currentLocationNodeNumber++
        synchronized(forceGraph) {
            addLocationAndScanList(
                scanList,
                forceGraph,
                currentLocationNodeNumber
            )
        }
        scanList.clear()

        // TODO So far compose does not update with changes only to forceGraph. It need more simple variable to trigger recomposing.
        onForceGraphUpdate()
    }

    fun clearMap() {
        forceGraph.nodeList.clear()
        forceGraph.relationList.clear()
        onForceGraphUpdate()
    }

    private var iterationCounter = 0

    /**
     * Trigger Recomposition as it does not detect changes in ForceGraph.
     */
    fun onForceGraphUpdate() {
        forceNodeCount = forceGraph.nodeList.size
        forceRelationCount = forceGraph.relationList.size + iterationCounter
        iterationCounter++
    }
}
