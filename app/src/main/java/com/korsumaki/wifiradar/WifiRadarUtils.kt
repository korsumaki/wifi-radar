package com.korsumaki.wifiradar

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Add new location with new scanList
 *
 * Create new location node and nodes for each AP in scanList.
 *
 * @param forceGraph                ForceGraph data structure
 * @param scanList                  Scan list
 * @param currentLocationNodeNumber Number of current location node
 */
fun addLocationAndScanList(forceGraph: ForceGraph, scanList: List<WifiAp>, currentLocationNodeNumber: Int) {
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

        // Calculate direction and decide angle
        var direction = 0
        var angle = 180

        val routeStepsToCheck = 2
        if (currentLocationNodeNumber > routeStepsToCheck) {
            val olderLocationNode = ForceNode(id = "Loc-${currentLocationNodeNumber-routeStepsToCheck}")
            val olderIndex = forceGraph.nodeList.indexOf(olderLocationNode)
            if (olderIndex != -1) {
                val olderCoordinate = forceGraph.nodeList[olderIndex].coordinate
                val latestCoordinate = currentLocationNode.coordinate

                // Get direction if distance is enough
                val vector = latestCoordinate-olderCoordinate
                val distance = vector.distance()
                println("distance=$distance")
                if (distance > 5) {
                    direction = vector.direction().toInt()
                    angle = 80
                }
            }
        }
        addNodesFromScanList(forceGraph, scanList, currentLocationNode, direction, angle)
    }
}

/**
 * Create nodes for scanList items.
 *
 * Connect them with relation to currentLocationNode.
 *
 * @param forceGraph            ForceGraph data structure
 * @param scanList              Scan list
 * @param currentLocationNode   Current location
 * @param direction             Direction where new nodes should be created, in degrees
 * @param angle                 Angle how much direction can differ from [direction], in degrees
 */
fun addNodesFromScanList(forceGraph: ForceGraph, scanList: List<WifiAp>, currentLocationNode: ForceNode, direction: Int, angle: Int) {
    for (scanResult in scanList) {
        val node = ForceNode(id = scanResult.mac)

        // Get initial coordinates for node.
        // Only known value is distance from scanning location.
        // Randomize direction for direction +- angle, then calculate it's coordinates.
        val degreesRange = (direction-angle)..(direction+angle)
        val randomRadians = degreesRange.random() / 180f * PI

        val distance = scanResult.getDistance()
        val y = sin(randomRadians) * distance
        val x = cos(randomRadians) * distance

        node.name = scanResult.name
        node.type = ForceNode.Type.WIFI
        node.coordinate = Coordinate(
            x = currentLocationNode.coordinate.x + x.toFloat(),
            y = currentLocationNode.coordinate.y + y.toFloat()
        )
        forceGraph.connectNodesWithRelation(
            node1 = currentLocationNode,
            node2 = node,
            relation = ForceRelation(distance)
        )
    }
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
