package com.korsumaki.wifiradar

class ForceGraph() {

    var nodes = ArrayList<ForceNode>()
    var relations = ArrayList<ForceRelation>()

    fun iterateRelations() {
        // For all ForceRelations: Clear coordinates

        // For all ForceNodes: Update coordinates to ForceRelations

        // For all ForceRelations: Calculate force

        // For all ForceNodes: Calculate sum force vector, calculate new x,y

    }
}

/**
 * ForceNode is "position of something" which is connected to other ForceNodes with ForceRelation
 *
 * TODO should coordinate be out of constructor? Then it would be out of equals()
 */
data class ForceNode(val id: String, var coordinate: Coordinate = Coordinate(0f, 0f)) {
    var name = ""
    var relations = ArrayList<Int>(0)
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
}


data class ForceRelation(val targetLength: Float) {
    var coordinates = ArrayList<Coordinate>()
    var force = 0f // TODO should this be Coordinate, both components separately?
}
