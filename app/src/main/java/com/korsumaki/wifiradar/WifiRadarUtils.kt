package com.korsumaki.wifiradar

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.math.abs


fun addLocationAndScanList(scanList: List<WifiAp>, forceGraph: ForceGraph, currentLocationNodeNumber: Int) {
    println("addLocationAndScanList")
    if (scanList.isNotEmpty()) {
        // Add new node for current location
        val currentLocationNode = ForceNode(id = "Loc-$currentLocationNodeNumber")
        currentLocationNode.type = ForceNode.Type.ROUTE
        currentLocationNode.coordinate = Coordinate(0f, 0f)

        // Get coordinates from previous location node (if exists)
        val previousLocationNode = ForceNode(id = "Loc-${currentLocationNodeNumber-1}")
        val index = forceGraph.nodeList.indexOf(previousLocationNode)
        if (index != -1) {
            currentLocationNode.coordinate = forceGraph.nodeList[index].coordinate
        }
        forceGraph.nodeList.add(currentLocationNode)

        addNodesFromScanList(forceGraph, currentLocationNode, scanList)
    }
}

fun addNodesFromScanList(forceGraph: ForceGraph, currentLocationNode: ForceNode, scanList: List<WifiAp>) {
    for (scanResult in scanList) {
        val node = ForceNode(id = scanResult.mac)
        println("Adding: $scanResult with distance ${scanResult.getDistance()}")

        // Moving node sideways with random makes distance longer that it should be.
        // With many WifiAps it create too high force and too high velocity...
        // Now randomNumber usage (below) tries to eliminate that problem.
        val xRange = -100..100
        val sign = listOf(-1, 1)
        val randomNumber = xRange.random()
        node.name = scanResult.name
        node.type = ForceNode.Type.WIFI
        node.coordinate = Coordinate(
            x = currentLocationNode.coordinate.x + randomNumber,
            y = currentLocationNode.coordinate.y + (scanResult.getDistance() - abs(randomNumber/3)) * sign.random()
        )
        forceGraph.connectNodesWithRelation(
            node1 = currentLocationNode,
            node2 = node,
            relation = ForceRelation(scanResult.getDistance())
        )
    }
    println("Current nodes:")
    for (node in forceGraph.nodeList) {
        println(node)
    }
    println("with ${forceGraph.relationList.size} relations")
}

// File handling functions

lateinit var serializationFile: File

fun writeScanListToFile(path: File, filename: String, scanList: ArrayList<WifiAp>) {
    val encodedData = Json.encodeToString(scanList)
    println("writeScanListToFile: $encodedData")

    if (!::serializationFile.isInitialized) {
        serializationFile = File(path, filename)
        println("File path= ${serializationFile.absolutePath}")
    }
    serializationFile.appendText(encodedData + "\n")
}


var scanResultCount = 0
var scanListStringList = emptyList<String>()

fun readScanListFromFile(path: File, filename: String): List<WifiAp> {
    if (scanResultCount == 0) {
        if (!::serializationFile.isInitialized) {
            serializationFile = File(path, filename)
        }
        scanListStringList = serializationFile.readLines()
        println("readScanListFromFile(): size=${scanListStringList.size}")
    }

    if (scanListStringList.isEmpty()) {
        println("readScanListFromFile(): Empty list")
        return emptyList()
    }

    if (scanResultCount >= scanListStringList.size) {
        scanResultCount = 0
    }
    println("readScanListFromFile() scan ${scanResultCount+1} / ${scanListStringList.size}")
    val oneScanList = scanListStringList[scanResultCount]
    val scanList = Json.decodeFromString<ArrayList<WifiAp>>(oneScanList)
    scanResultCount++
    return scanList
}
