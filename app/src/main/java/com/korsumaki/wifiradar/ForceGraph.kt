package com.korsumaki.wifiradar

import kotlin.math.pow
import kotlin.math.sqrt

class ForceGraph {

    var nodeList = ArrayList<ForceNode>()
    var relationList = ArrayList<ForceRelation>()

    fun connectNodesWithRelation(node1: ForceNode, node2: ForceNode, relation: ForceRelation) {

        // Add nodes to list if not already there
        if (!nodeList.contains(node1)) {
            nodeList.add(node1)
        }
        if (!nodeList.contains(node2)) {
            nodeList.add(node2)
        }

        // Add relation to list
        relationList.add(relation)

        // Update to both nodes with index to relation table
        val relationIndex = relationList.size - 1
        node1.relationIndexList.add(relationIndex)
        node2.relationIndexList.add(relationIndex)
    }

    fun iterateRelations() {
        // For all ForceRelations: Clear coordinates
        for (relation in relationList) {
            relation.coordinateList.clear()
        }

        // For all ForceNodes: Update coordinate to all ForceRelations
        for (node in nodeList) {
            for (relationIndex in node.relationIndexList) {
                relationList[relationIndex].coordinateList.add(node.coordinate)
            }
        }

        // For all ForceRelations: Calculate force
        for (relation in relationList) {
            relation.calculateForce()
        }

        // For all ForceNodes: Calculate sum force vector, calculate new x,y
        for (node in nodeList) {
            val sumForceVector = calculateSumForceVector(node)
            // TODO sum force vector need to be calculated here in ForceGraph class
            node.calculateNewCoordinates(sumForceVector)
        }
    }

    fun calculateSumForceVector(node: ForceNode): Coordinate {

        return Coordinate(0f, 0f)
    }
}

/**
 * ForceNode is "position of something" which is connected to other ForceNodes with ForceRelation
 *
 * TODO should coordinate be out of constructor? Then it would be out of equals()
 */
data class ForceNode(val id: String, var coordinate: Coordinate = Coordinate(0f, 0f)) {
    var name = ""
    var relationIndexList = ArrayList<Int>(0)

    fun calculateNewCoordinates(sumForceVector: Coordinate) {
        // TODO would be easier to have forces already in components?

        // calculate a -> v -> s == new coordinates
    }
}


/**
 * Coordinate is for storing (x,y) coordinates and calculating with those
 */
data class Coordinate(val x: Float, val y: Float) {
    operator fun plus(increment: Coordinate): Coordinate {
        return Coordinate(x = x + increment.x, y = y + increment.y)
    }

    operator fun minus(decrement: Coordinate): Coordinate {
        return Coordinate(x = x - decrement.x, y = y - decrement.y)
    }

    /**
     * Calculate distance from origin (0,0).
     *
     * Vector length can be calculated with subtracting start and end coordinates,
     * and getting distance().
     *
     * val length = (startCoordinate-endCoordinate).distance()
     */
    fun distance(): Float {
        return sqrt(x.pow(2) + y.pow(2))
    }
}


data class ForceRelation(val targetLength: Float) {
    var coordinateList = ArrayList<Coordinate>()
    var force = 0f // TODO should this be Coordinate, both components separately?

    val springConstant = 10f // spring constant, N/m

    /**
     * Calculate force for this relation.
     */
    fun calculateForce() {
        val deltaCoordinate = coordinateList[0] - coordinateList[1]
        val x = deltaCoordinate.distance() - targetLength

        // Spring force
        force = -springConstant * x
    }
}
