package com.korsumaki.wifiradar

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.nio.charset.Charset
import java.util.regex.Pattern
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

        addNodesFromScanList(forceGraph, scanList, currentLocationNode)
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
 */
fun addNodesFromScanList(forceGraph: ForceGraph, scanList: List<WifiAp>, currentLocationNode: ForceNode) {
    for (scanResult in scanList) {
        val node = ForceNode(id = scanResult.mac)

        // Get initial coordinates for node.
        // Only known value is distance from scanning location.
        // Randomize in which direction node is, then calculate it's coordinates.
        val degreesRange = 0..359
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

/*
* TODO
*  - separate file/res reading from ScanList creation
*/

fun readScanListFromInputStream(inputStream: InputStream): List<WifiAp> {
    if (scanResultCount == 0) {
        val byteArray = inputStream.readBytes()
        val string = byteArray.toString(Charset.defaultCharset())
        inputStream.close()
        // Split string to List of Strings
        scanListStringList = string.split(regex = Pattern.compile("\n"))

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
    scanResultCount++

    // Skip decoding if line is empty
    if (oneScanList.isNotEmpty()) {
        return Json.decodeFromString<ArrayList<WifiAp>>(oneScanList)
    }
    return emptyList()
}
