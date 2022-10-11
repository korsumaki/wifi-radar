package com.korsumaki.wifiradar

import kotlin.math.pow
import kotlin.math.sqrt

class ForceGraph {

    var nodeList = ArrayList<ForceNode>()
    var relationList = ArrayList<ForceRelation>()

    fun connectNodesWithRelation(node1: ForceNode, node2: ForceNode, relation: ForceRelation) {
        require(node1 != node2) { "ForceNode cannot have ForceRelation with itself" }

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

    /**
     * Iterate all relations and movements once.
     *
     * Calculate forces and update node coordinates.
     */
    fun iterateRelations() {
        // For all ForceRelations: Clear coordinates
        clearRelationCoordinates()

        // For all ForceNodes: Update coordinate to all ForceRelations
        updateNodeCoordinatesToRelations()

        // For all ForceRelations: Calculate force
        calculateRelationForces()

        // For all ForceNodes: Calculate sum force vector, calculate new x,y
        for (node in nodeList) {
            // Sum force vector need to be calculated here in ForceGraph class
            val sumForceVector = calculateSumForceVector(node)
            node.calculateNewCoordinates(sumForceVector)
        }

        clearRelationCoordinates()
        updateNodeCoordinatesToRelations()
    }

    fun clearRelationCoordinates() {
        for (relation in relationList) {
            relation.coordinateList.clear()
        }
    }

    fun updateNodeCoordinatesToRelations() {
        for (node in nodeList) {
            for (relationIndex in node.relationIndexList) {
                relationList[relationIndex].coordinateList.add(node.coordinate)
            }
        }
    }

    fun calculateRelationForces() {
        for (relation in relationList) {
            relation.calculateForce()
        }
    }

    /**
     * Calculate sum force vector for node
     *
     * @param node  ForceNode for which force vector should be calculated
     */
    fun calculateSumForceVector(node: ForceNode): Coordinate {
        var sumForceVector = Coordinate(0f, 0f)

        for (relationIndex in node.relationIndexList) {
            // Check direction of force.
            // If node's coordinate is first in relation's list, then force vector is pointing
            // already to correct direction. Otherwise force should be inverted.
            if (node.coordinate == relationList[relationIndex].coordinateList[0]) {
                sumForceVector += relationList[relationIndex].force
            }
            else {
                sumForceVector -= relationList[relationIndex].force
            }
        }
        return sumForceVector
    }
}

/**
 * ForceNode is "position of something" which is connected to other ForceNodes with ForceRelation
 *
 * @param id    Identifier for node. This should be unique withing ForceNodes in ForceGraph.
 */
data class ForceNode(val id: String) {
    var name = ""
    var coordinate: Coordinate = Coordinate(0f, 0f)
    var relationIndexList = ArrayList<Int>(0)

    var vX = 0f // Velocity in m/s
    var vY = 0f // Velocity in m/s

    fun calculateNewCoordinates(sumForceVector: Coordinate) {
        val m = 1f // Mass in kg
        val t = 0.1f // time in seconds

        // Acceleration -> velocity -> position
        // a = F/m  <- F=m*a
        // v = v0 + a*t
        // s = v*t

        val aX = sumForceVector.x / m
        val aY = sumForceVector.y / m

        vX += aX * t
        vY += aY * t

        // Slow down movement
        vX *= 0.9f
        vY *= 0.9f

        val sX = vX * t
        val sY = vY * t

        coordinate = Coordinate(x = coordinate.x+sX, y = coordinate.y+sY)
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
    val springConstant = 10f // spring constant, N/m

    /**
     * Force vector, direction is for first coordinate.
     */
    var force = Coordinate(0f, 0f)

    /**
     * Calculate force for this relation.
     */
    fun calculateForce() {
        check(coordinateList.size == 2) { "ForceRelation shall have exactly two Coordinates (now is has ${coordinateList.size})" }

        // Calculate force magnitude
        val deltaCoordinate = coordinateList[0] - coordinateList[1]
        val length = deltaCoordinate.distance()

        val deltaS = length - targetLength
        // Spring force
        val forceMagnitude = -springConstant * deltaS

        // Divide force to components with unit vector
        force = Coordinate(
            x = deltaCoordinate.x/length * forceMagnitude,
            y = deltaCoordinate.y/length * forceMagnitude)
    }
}
