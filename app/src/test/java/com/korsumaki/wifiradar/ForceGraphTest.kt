package com.korsumaki.wifiradar

import org.junit.Test
import com.google.common.truth.Truth.assertThat

/**
 * Unit tests for ForceGraph classes
 */
class ForceGraphTest {

    // ====================
    // Coordinate
    // ====================
    @Test
    fun test_Coordinate_create() {
        val coordinate = Coordinate(5f, 12f)
        assertThat(coordinate.x).isEqualTo(5f)
        assertThat(coordinate.y).isEqualTo(12f)
    }

    @Test
    fun test_Coordinate_compare() {
        val coordinate1 = Coordinate(5f, 12f)
        val coordinate2 = Coordinate(5f, 12f)
        val coordinate3 = Coordinate(28f, 55f)
        assertThat(coordinate1).isEqualTo(coordinate2)
        assertThat(coordinate1).isNotEqualTo(coordinate3)
    }

    @Test
    fun test_Coordinate_calculationPlus() {
        val coordinate1 = Coordinate(28f, 55f)
        val coordinate2 = Coordinate(5f, 12f)
        val coordinate3 = Coordinate(33f, 67f)
        assertThat(coordinate1+coordinate2).isEqualTo(coordinate3)
    }

    @Test
    fun test_Coordinate_calculationMinus() {
        val coordinate1 = Coordinate(15f, 20f)
        val coordinate2 = Coordinate(8f, 7f)
        val coordinate3 = Coordinate(7f, 13f)
        assertThat(coordinate1-coordinate2).isEqualTo(coordinate3)
    }

    @Test
    fun test_Coordinate_distance() {
        assertThat(Coordinate(10f, 0f).distance()).isEqualTo(10)
        assertThat(Coordinate(0f, 31f).distance()).isEqualTo(31)
        assertThat(Coordinate(30f, 40f).distance()).isEqualTo(50)

        assertThat(Coordinate(-30f, 40f).distance()).isEqualTo(50)
        assertThat(Coordinate(30f, -40f).distance()).isEqualTo(50)
        assertThat(Coordinate(-30f, -40f).distance()).isEqualTo(50)
    }

    // ====================
    // ForceNode
    // ====================
    @Test
    fun test_ForceNode_create() {
        val node = ForceNode("some id")
        assertThat(node.id).isEqualTo("some id")
        assertThat(node.coordinate).isEqualTo(Coordinate(0f, 0f))
    }

    @Test
    fun test_ForceNode_relationsList() {
        val node = ForceNode("some id")
        node.relationIndexList.add(5)
        assertThat(node.relationIndexList.size).isEqualTo(1)
        assertThat(node.relationIndexList[0]).isEqualTo(5)
        node.relationIndexList.add(87)
        assertThat(node.relationIndexList[1]).isEqualTo(87)
    }

    @Test
    fun test_ForceNode_calculateNewCoordinates() {
        val node = ForceNode("some id", Coordinate(10f,10f))

        node.calculateNewCoordinates(sumForceVector =  Coordinate(0f,0f)) // No force yet
        assertThat(node.coordinate).isEqualTo(Coordinate(10f,10f))

        node.calculateNewCoordinates(sumForceVector =  Coordinate(100f,0f)) // 100N to right
        assertThat(node.coordinate).isEqualTo(Coordinate(11f,10f))
        node.calculateNewCoordinates(sumForceVector =  Coordinate(100f,0f)) // 100N to right
        assertThat(node.coordinate).isEqualTo(Coordinate(13f,10f))
        node.calculateNewCoordinates(sumForceVector =  Coordinate(100f,0f)) // 100N to right
        assertThat(node.coordinate).isEqualTo(Coordinate(16f,10f))

        node.calculateNewCoordinates(sumForceVector =  Coordinate(0f,0f)) // No force, but speed is already to right
        assertThat(node.coordinate).isEqualTo(Coordinate(19f,10f))
    }

    // ====================
    // ForceRelation
    // ====================
    @Test
    fun test_ForceRelation_create() {
        val relation = ForceRelation(45f)
        assertThat(relation.targetLength).isEqualTo(45f)
    }

    @Test
    fun test_ForceRelation_addAndClearCoordinates() {
        val relation = ForceRelation(45f)

        relation.coordinateList.add(Coordinate(2f,8f))
        assertThat(relation.coordinateList.size).isEqualTo(1)

        relation.coordinateList.add(Coordinate(123f,44.4f))
        assertThat(relation.coordinateList.size).isEqualTo(2)

        assertThat(relation.coordinateList[0]).isEqualTo(Coordinate(2f, 8f))
        assertThat(relation.coordinateList[1]).isEqualTo(Coordinate(123f, 44.4f))
        relation.coordinateList.clear()

        assertThat(relation.coordinateList.size).isEqualTo(0)
    }

    @Test
    fun test_ForceRelation_calculateForce_simple() {
        // x direction
        val relation = ForceRelation(10f)
        relation.coordinateList.add(Coordinate(0f,0f))
        relation.coordinateList.add(Coordinate(11f,0f))
        relation.calculateForce()
        assertThat(relation.force).isEqualTo(Coordinate(10f, -0f))

        // y direction
        val relationY = ForceRelation(20f)
        relationY.coordinateList.add(Coordinate(100f,100f))
        relationY.coordinateList.add(Coordinate(100f,122f))
        relationY.calculateForce()
        assertThat(relationY.force).isEqualTo(Coordinate(-0f, 20f))

        // y direction (other way)
        val relationY2 = ForceRelation(20f)
        relationY2.coordinateList.add(Coordinate(100f,122f))
        relationY2.coordinateList.add(Coordinate(100f,100f))
        relationY2.calculateForce()
        assertThat(relationY2.force).isEqualTo(Coordinate(-0f, -20f))

        // Zero force
        val relation2 = ForceRelation(50f)
        relation2.coordinateList.add(Coordinate(100f,100f))
        relation2.coordinateList.add(Coordinate(130f,140f))
        relation2.calculateForce()
        assertThat(relation2.force).isEqualTo(Coordinate(0f, 0f))
    }

    @Test
    fun test_ForceRelation_calculateForce_skewedForce() {
        val relation = ForceRelation(50f)
        relation.coordinateList.add(Coordinate(100f,100f))
        relation.coordinateList.add(Coordinate(160f,180f))
        relation.calculateForce()
        assertThat(relation.force).isEqualTo(Coordinate(300f,400f))
    }

    // ====================
    // ForceGraph
    // ====================
    @Test
    fun test_ForceGraph_usage() {
        val forceGraph = ForceGraph()

        val currentPosition = Coordinate(4f, 7f)
        val currentPositionNode = ForceNode("Position-1", currentPosition)

        // Current position
        val newPositionNode = ForceNode("Position-2", currentPosition.copy(y = currentPosition.y + 5))
        forceGraph.connectNodesWithRelation(currentPositionNode, newPositionNode, ForceRelation(10f))

        val strength = 3f
        val apNode = ForceNode("ap1")
        forceGraph.connectNodesWithRelation(apNode, newPositionNode, ForceRelation(strength))

        assertThat(forceGraph.nodeList.size).isEqualTo(3)
        assertThat(forceGraph.relationList.size).isEqualTo(2)

        val apNodeButNewInstance = ForceNode("ap1")
        forceGraph.connectNodesWithRelation(apNodeButNewInstance, currentPositionNode, ForceRelation(5f))

        assertThat(forceGraph.nodeList.size).isEqualTo(3)
        assertThat(forceGraph.relationList.size).isEqualTo(3)

        //println("nodes: ${forceGraph.nodes}")
        //println("relations: ${forceGraph.relations}")

        //println("nodes[0].relations: ${forceGraph.nodes[0].relations}")
        //println("nodes[1].relations: ${forceGraph.nodes[1].relations}")
    }

    @Test
    fun test_ForceGraph_calculateSumForceVector() {
        val forceGraph = ForceGraph()

        // Setup graph
        val centerNode = ForceNode("Center", Coordinate(0f,100f))
        val rightNode = ForceNode("Right", Coordinate(10f,100f))
        val leftNode = ForceNode("Left", Coordinate(-11f,100f))
        forceGraph.connectNodesWithRelation(centerNode, rightNode, ForceRelation(10f))
        forceGraph.connectNodesWithRelation(centerNode, leftNode, ForceRelation(10f))

        // Calculations (forces for each relation)
        forceGraph.clearRelationCoordinates()
        forceGraph.updateNodeCoordinatesToRelations()
        forceGraph.calculateRelationForces()

        // Calculate sum force vectors
        val centerForceVector = forceGraph.calculateSumForceVector(centerNode)
        val rightForceVector = forceGraph.calculateSumForceVector(rightNode)
        val leftForceVector = forceGraph.calculateSumForceVector(leftNode)

        // testing
        assertThat(rightForceVector).isEqualTo(Coordinate(0f,0f)) // No force
        assertThat(centerForceVector).isEqualTo(Coordinate(-10f,0f)) // Force to left
        assertThat(leftForceVector).isEqualTo(Coordinate(10f,0f)) // Force to right
    }

    @Test
    fun test_ForceGraph_calculateSumForceVector_2() {
        val forceGraph = ForceGraph()

        // Setup graph
        val centerNode = ForceNode("Center", Coordinate(0f,100f))
        val rightNode = ForceNode("Right", Coordinate(11f,100f))
        val upNode = ForceNode("Up", Coordinate(0f,109f))
        forceGraph.connectNodesWithRelation(centerNode, rightNode, ForceRelation(10f))
        forceGraph.connectNodesWithRelation(centerNode, upNode, ForceRelation(10f))

        // Calculations (forces for each relation)
        forceGraph.clearRelationCoordinates()
        forceGraph.updateNodeCoordinatesToRelations()
        forceGraph.calculateRelationForces()

        // Calculate sum force vectors
        val centerForceVector = forceGraph.calculateSumForceVector(centerNode)
        val rightForceVector = forceGraph.calculateSumForceVector(rightNode)
        val upForceVector = forceGraph.calculateSumForceVector(upNode)

        // testing
        assertThat(rightForceVector).isEqualTo(Coordinate(-10f,0f)) // Force to left
        assertThat(centerForceVector).isEqualTo(Coordinate(10f,-10f)) // Force to down and right
        assertThat(upForceVector).isEqualTo(Coordinate(0f,10f)) // Force to up
    }
}

