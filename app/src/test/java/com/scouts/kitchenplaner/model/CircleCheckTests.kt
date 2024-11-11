/*
 * KitchenPlanerApp is the android app frontend for the KitchenPlaner, a tool
 * to cooperatively plan a meal plan for a campout.
 * Copyright (C) 2023-2024 Arne Kuchenbecker, Antonia Heiming, Anton Kadelbach, Sandra Lanz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.scouts.kitchenplaner.model

import com.scouts.kitchenplaner.model.entities.UnitConversion
import com.scouts.kitchenplaner.model.usecases.unitconversionchecks.Circle
import com.scouts.kitchenplaner.model.usecases.unitconversionchecks.UnitConversionGraph
import com.scouts.kitchenplaner.utils.CircleSearch
import com.scouts.kitchenplaner.utils.Graph
import com.scouts.kitchenplaner.utils.SCCFinder
import com.scouts.kitchenplaner.utils.Stack
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

/**
 * Contains unit tests for checking the circle search algorithm
 */
class CircleCheckTests {
    /**
     * Checks functionality of the [Stack] class
     */
    @Test
    fun testStackContent() {
        val stack = Stack<Int>()
        stack.push(1)
        stack.push(2)
        stack.push(3)

        val content = stack.content

        Assertions.assertEquals(3, content.size, "Found the wrong number of items.")

        for (i in 1..3) {
            Assertions.assertEquals(i, content[i - 1], "Items were not in the correct order.")
        }
    }

    /**
     * Checks the inducedSubgraph method of [Graph] with a simple graph
     */
    @Test
    fun testEasyInducedSubgraph() {
        val g = Graph(arrayOf(1, 2, 3, 4), arrayOf(0, 3, 5, 5, 6), arrayOf(2, 3, 4, 1, 3, 2))

        val g_prime = g.inducedSubgraph(listOf(1, 2, 3))

        Assertions.assertArrayEquals(
            arrayOf(1, 2, 3),
            g_prime.vertices,
            "Induced Subgraph had the wrong vertices"
        )
        Assertions.assertArrayEquals(
            arrayOf(0, 2, 4, 4),
            g_prime.edgePointers,
            "Induced Subgraph had the wrong edge pointers"
        )
        Assertions.assertArrayEquals(
            arrayOf(2, 3, 1, 3),
            g_prime.edges,
            "Induced Subgraph had the wrong edges"
        )
    }

    /**
     * Checks the inducedSubgraph() method of [Graph] with a more complex graph
     */
    @Test
    fun testComplexInducedSubgraph() {
        val g = Graph(arrayOf(1, 2, 3, 4), arrayOf(0, 3, 5, 5, 6), arrayOf(2, 3, 4, 1, 3, 2))

        val g_prime = g.inducedSubgraph(listOf(1, 2, 4))

        Assertions.assertArrayEquals(
            arrayOf(1, 2, 4),
            g_prime.vertices,
            "Induced Subgraph had the wrong vertices"
        )
        Assertions.assertArrayEquals(
            arrayOf(0, 2, 3, 4),
            g_prime.edgePointers,
            "Induced Subgraph had the wrong edge pointers"
        )
        Assertions.assertArrayEquals(
            arrayOf(2, 4, 1, 2),
            g_prime.edges,
            "Induced Subgraph had the wrong edges"
        )
    }

    /**
     * Checks the functionality of the [SCCFinder] class
     */
    @Test
    fun testStrongComponents() {
        val g = Graph(
            Array(9) { it + 1 },
            arrayOf(0, 0, 1, 4, 6, 7, 8, 11, 12, 12),
            arrayOf(3, 1, 4, 6, 3, 5, 1, 7, 4, 8, 9, 6)
        )

        val sccFinder = SCCFinder(g)

        val components = sccFinder.run()

        Assertions.assertEquals(
            1,
            components.size,
            "Found an incorrect number of non-trivial strong components"
        )

        val comp = components[0]

        Assertions.assertArrayEquals(
            arrayOf(3, 4, 6, 7, 8),
            comp.vertices,
            "Found incorrect vertices"
        )
        Assertions.assertArrayEquals(
            arrayOf(0, 2, 3, 4, 6, 7),
            comp.edgePointers,
            "Found incorrect edge pointers"
        )
        Assertions.assertArrayEquals(
            arrayOf(4, 6, 3, 7, 4, 8, 6),
            comp.edges,
            "Found incorrect edges"
        )
    }

    /**
     * Checks the equals() and hashCode() methods of [Circle]
     */
    @Test
    fun checkCircle() {
        val c1 = Circle(listOf(1, 2, 3))
        val c2 = Circle(1, 2, 3)
        val c3 = Circle(2, 3, 1)
        val c4 = Circle(1, 3, 2)

        Assertions.assertTrue { c1 == c2 }
        Assertions.assertTrue { c2 == c1 }
        Assertions.assertTrue { c2 == c3 }
        Assertions.assertTrue { c3 == c2 }
        Assertions.assertTrue { c3 == c1 }
        Assertions.assertTrue { c1 == c3 }

        Assertions.assertFalse { c1 == c4 }
        Assertions.assertFalse { c4 == c1 }
        Assertions.assertFalse { c2 == c4 }
        Assertions.assertFalse { c4 == c2 }
        Assertions.assertFalse { c3 == c4 }
        Assertions.assertFalse { c4 == c3 }

        Assertions.assertTrue { c1.hashCode() == c2.hashCode() }
        Assertions.assertTrue { c2.hashCode() == c3.hashCode() }
    }

    /**
     * Checks the [CircleSearch] class on easy examples
     */
    @Test
    fun findEasyCircles() {
        val g1 = Graph(
            arrayOf(1, 2, 3, 4),
            arrayOf(0, 1, 2, 3, 4),
            arrayOf(2, 3, 1, 1)
        )

        val cs1 = CircleSearch(g1)

        val circles1 = cs1.run()

        Assertions.assertEquals(1, circles1.size, "Found an incorrect number of circles")
        Assertions.assertEquals(Circle(listOf(1, 2, 3)), circles1[0], "Found an incorrect circle")

        val g2 = Graph(
            arrayOf(1, 2, 3, 4),
            arrayOf(0, 1, 3, 4, 5),
            arrayOf(2, 3, 4, 1, 1)
        )

        val cs2 = CircleSearch(g2)

        val circles2 = cs2.run()

        Assertions.assertEquals(2, circles2.size)
        Assertions.assertTrue { circles2.contains(Circle(listOf(1, 2, 3))) }
        Assertions.assertTrue { circles2.contains(Circle(listOf(1, 2, 4))) }
    }

    /**
     * Checks the [CircleSearch] class on more complex examples
     */
    @Test
    fun findComplexCircles() {
        val g1 = Graph(
            Array(9) { it + 1 },
            arrayOf(0, 0, 1, 4, 6, 7, 8, 11, 12, 12),
            arrayOf(3, 1, 4, 6, 3, 5, 1, 7, 4, 8, 9, 6)
        )

        val cs1 = CircleSearch(g1)

        val circles1 = cs1.run()

        Assertions.assertEquals(3, circles1.size)
        Assertions.assertTrue { circles1.contains(Circle(listOf(3, 4))) }
        Assertions.assertTrue { circles1.contains(Circle(listOf(6, 7, 8))) }
        Assertions.assertTrue { circles1.contains(Circle(listOf(3, 6, 7, 4))) }

        val v2 = Array(9) { it + 1 }
        val edgeList = v2.map {
            val e = mutableListOf<Int>()
            if (it - 3 > 0) {
                e.add(it - 3)
            }
            if (it % 3 != 1) {
                e.add(it - 1)
            }
            if (it % 3 != 0) {
                e.add(it + 1)
            }
            if (it + 3 <= 9) {
                e.add(it + 3)
            }
            e
        }

        val edges = edgeList.flatten().toTypedArray()
        val edgePointers = edgeList.runningFold(0) { acc, es -> acc + es.size }.toTypedArray()

        val g2 = Graph(v2, edgePointers, edges)

        val cs2 = CircleSearch(g2)

        val circles2 = cs2.run()

        Assertions.assertEquals(38, circles2.size)
        Assertions.assertEquals(12, circles2.filter { it.length == 2 }.size)
        Assertions.assertEquals(8, circles2.filter { it.length == 4 }.size)
        Assertions.assertEquals(8, circles2.filter { it.length == 6 }.size)
        Assertions.assertEquals(10, circles2.filter { it.length == 8 }.size)

        val lengthTwoCircles = listOf(
            Circle(1, 2),
            Circle(2, 3),
            Circle(1, 4),
            Circle(2, 5),
            Circle(3, 6),
            Circle(4, 5),
            Circle(5, 6),
            Circle(4, 7),
            Circle(5, 8),
            Circle(6, 9),
            Circle(7, 8),
            Circle(8, 9)
        )

        Assertions.assertTrue { circles2.containsAll(lengthTwoCircles) }

        val lengthFourCircles = listOf(
            Circle(1, 4, 5, 2),
            Circle(2, 5, 6, 3),
            Circle(4, 7, 8, 5),
            Circle(5, 8, 9, 6),
            Circle(1, 2, 5, 4),
            Circle(2, 3, 6, 5),
            Circle(4, 5, 8, 7),
            Circle(5, 6, 9, 8)
        )

        Assertions.assertTrue { circles2.containsAll(lengthFourCircles) }

        val lengthSixCircles = listOf(
            Circle(1, 2, 3, 6, 5, 4),
            Circle(4, 5, 6, 9, 8, 7),
            Circle(1, 2, 5, 8, 7, 4),
            Circle(2, 3, 6, 9, 8, 5),
            Circle(1, 4, 5, 6, 3, 2),
            Circle(4, 7, 8, 9, 6, 5),
            Circle(1, 4, 7, 8, 5, 2),
            Circle(2, 5, 8, 9, 6, 3)
        )

        Assertions.assertTrue { circles2.containsAll(lengthSixCircles) }

        val lengthEightCircles = listOf(
            Circle(1, 2, 3, 6, 9, 8, 7, 4),
            Circle(1, 2, 3, 6, 5, 8, 7, 4),
            Circle(1, 2, 3, 6, 9, 8, 5, 4),
            Circle(1, 2, 5, 6, 9, 8, 7, 4),
            Circle(2, 3, 6, 9, 8, 7, 4, 5),
            Circle(1, 4, 7, 8, 9, 6, 3, 2),
            Circle(1, 4, 7, 8, 5, 6, 3, 2),
            Circle(1, 4, 5, 8, 9, 6, 3, 2),
            Circle(1, 4, 7, 8, 9, 6, 5, 2),
            Circle(2, 5, 4, 7, 8, 9, 6, 3)
        )

        Assertions.assertTrue { circles2.containsAll(lengthEightCircles) }
    }

    /**
     * Checks the creation of UnitConversionGraphs
     */
    @OptIn(DomainLayerRestricted::class)
    @Test
    fun checkGraphCreation() {
        val conversions1 = listOf(
            UnitConversion.of("Mehl", "kg", "g", BigDecimal("1000")),
            UnitConversion.of("Mehl", "g", "Pck", BigDecimal("0.001")),
            UnitConversion.of("Mehl", "Pck", "kg", BigDecimal("1"))
        )

        val forest1 = UnitConversionGraph(conversions1)

        Assertions.assertEquals(2, forest1.parts.size, "Found an incorrect number of trees")
        Assertions.assertArrayEquals(
            arrayOf(1, 2, 3),
            forest1.parts[0].graph.vertices,
            "Tree contained incorrect vertices"
        )
        Assertions.assertArrayEquals(
            arrayOf(0, 1, 2, 3),
            forest1.parts[0].graph.edgePointers,
            "Tree contained incorrect edge pointers"
        )
        Assertions.assertArrayEquals(
            arrayOf(2, 3, 1),
            forest1.parts[0].graph.edges,
            "Tree contained incorrect edges"
        )

        val conversions2 = listOf(
            UnitConversion.of("Mehl", "kg", "g", BigDecimal("1000.0")),
            UnitConversion.of("Mehl", "g", "Pck", BigDecimal("0.001")),
            UnitConversion.of("Mehl", "Pck", "kg", BigDecimal("1.0")),
            UnitConversion.of(Regex("[a-zA-Z]*"), "kg", "g", BigDecimal("1000.0")),
            UnitConversion.of("Zucker", "g", "kg", BigDecimal("0.001"))
        )

        val forest2 = UnitConversionGraph(conversions2)

        Assertions.assertEquals(3, forest2.parts.size, "Found an incorrect number of trees")
        val mehlTree = forest2.parts.find {
            it.conversions.any { conversion -> conversion.representation == "Mehl" }
        }
        if (mehlTree == null) {
            Assertions.fail<Unit>("Did not find all trees")
        } else {
            val g = mehlTree.graph
            Assertions.assertEquals(4, g.n)
            Assertions.assertEquals(5, g.m)
            Assertions.assertTrue {
                g.vertices.filter {
                    g.edgePointers[g.convertVertexNames(it) + 1] - g.edgePointers[g.convertVertexNames(
                        it
                    )] == 2
                }.size == 1
            }
            Assertions.assertTrue {
                g.vertices.filter { v -> g.edges.filter { it == v }.size == 2 }.size == 1
            }
            g.vertices.forEach {
                Assertions.assertTrue { g.edges.contains(it) }
            }
        }

        val zuckerTree = forest2.parts.find {
            it.conversions.any { conversion -> conversion.representation == "Zucker" }
        }
        if (zuckerTree == null) {
            Assertions.fail<Unit>("Did not find all trees")
        } else {
            val g = zuckerTree.graph
            Assertions.assertEquals(2, g.n)
            Assertions.assertEquals(2, g.m)
            g.vertices.forEach {
                Assertions.assertTrue { g.edges.contains(it) }
            }
            Assertions.assertTrue{g.inducedSubgraph(listOf(g.vertices[0])).m == 0}
            Assertions.assertTrue{g.inducedSubgraph(listOf(g.vertices[1])).m == 0}
        }
    }

    /**
     * Checks the circle searching in [UnitConversionGraph] on easy examples
     */
    @Test
    fun checkEasyUnitConversions() {
        val conversions1 = listOf(
            UnitConversion.of("Mehl", "kg", "g", BigDecimal("1000.0")),
            UnitConversion.of("Mehl", "g", "Pck", BigDecimal("0.001")),
            UnitConversion.of("Mehl", "Pck", "kg", BigDecimal("1.0"))
        )

        val forest1 = UnitConversionGraph(conversions1)
        val circles1 = forest1.findCircles()

        Assertions.assertEquals(1, circles1.size, "Found an incorrect number of circles")
        Assertions.assertEquals(Circle(conversions1), circles1[0], "Found an incorrect circle")

        val conversions2 = listOf(
            UnitConversion.of("Mehl", "kg", "g", BigDecimal("1000.0")),
            UnitConversion.of("Zucker", "g", "Pck", BigDecimal("0.001")),
            UnitConversion.of("Mehl", "Pck", "kg", BigDecimal("1.0"))
        )

        val forest2 = UnitConversionGraph(conversions2)
        val circles2 = forest2.findCircles()

        Assertions.assertEquals(0, circles2.size, "Found an incorrect number of circles")

        val conversions3 = listOf(
            UnitConversion.of(Regex("[a-z]"), "kg", "g", BigDecimal("1000.0")),
            UnitConversion.of(Regex("[A-Z]"), "g", "kg", BigDecimal("0.001"))
        )

        val forest3 = UnitConversionGraph(conversions3)
        val circles3 = forest3.findCircles()

        Assertions.assertEquals(1, circles3.size, "Found an incorrect number of circles")
        Assertions.assertEquals(Circle(conversions3), circles3[0], "Found an incorrect circle")
    }

    /**
     * Checks the circle searching in [UnitConversionGraph] on a more complex example
     */
    @Test
    fun checkComplexUnitConversions() {
        val conversions = listOf(
            UnitConversion.of(Regex("[a-zA-Z]*"), "g", "kg", BigDecimal("0.001")),
            UnitConversion.of("Milch", "ml", "l", BigDecimal("0.001")),
            UnitConversion.of("Mehl", "kg", "g", BigDecimal("1000.0")),
            UnitConversion.of("Zucker", "kg", "Pck", BigDecimal("1.0")),
            UnitConversion.of("Zucker", "Pck", "g", BigDecimal("1000.0")),
            UnitConversion.of("Milch", "l", "g", BigDecimal("1000.0"))
        )

        val forest = UnitConversionGraph(conversions)
        val circles = forest.findCircles()

        Assertions.assertEquals(2, circles.size)
    }
}