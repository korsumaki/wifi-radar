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
}

