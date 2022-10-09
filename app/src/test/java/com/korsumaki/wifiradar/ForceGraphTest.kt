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
        node.relations.add(5)
        assertThat(node.relations.size).isEqualTo(1)
        assertThat(node.relations[0]).isEqualTo(5)
        node.relations.add(87)
        assertThat(node.relations[1]).isEqualTo(87)
    }

    // ====================
    // ForceRelation
    // ====================
    @Test
    fun test_ForceRelation_create() {
        val relation = ForceRelation(45f)
        assertThat(relation.targetLength).isEqualTo(45f)
        assertThat(relation.force).isEqualTo(0f)
    }

    @Test
    fun test_ForceRelation_addAndClearCoordinates() {
        val relation = ForceRelation(45f)

        relation.coordinates.add(Coordinate(2f,8f))
        assertThat(relation.coordinates.size).isEqualTo(1)

        relation.coordinates.add(Coordinate(123f,44.4f))
        assertThat(relation.coordinates.size).isEqualTo(2)

        assertThat(relation.coordinates[0]).isEqualTo(Coordinate(2f, 8f))
        assertThat(relation.coordinates[1]).isEqualTo(Coordinate(123f, 44.4f))
        relation.coordinates.clear()

        assertThat(relation.coordinates.size).isEqualTo(0)
    }

    @Test
    fun test_ForceRelation_calculateForce_simple() {
        val relation = ForceRelation(10f)
        relation.coordinates.add(Coordinate(0f,0f))
        relation.coordinates.add(Coordinate(5f,0f))

        relation.calculateForce()
        assertThat(relation.force).isEqualTo(50f)
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

        assertThat(forceGraph.nodes.size).isEqualTo(3)
        assertThat(forceGraph.relations.size).isEqualTo(2)

        val apNodeButNewInstance = ForceNode("ap1")
        forceGraph.connectNodesWithRelation(apNodeButNewInstance, currentPositionNode, ForceRelation(5f))

        assertThat(forceGraph.nodes.size).isEqualTo(3)
        assertThat(forceGraph.relations.size).isEqualTo(3)

        //println("nodes: ${forceGraph.nodes}")
        //println("relations: ${forceGraph.relations}")

        //println("nodes[0].relations: ${forceGraph.nodes[0].relations}")
        //println("nodes[1].relations: ${forceGraph.nodes[1].relations}")
    }
}

