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

package com.scouts.kitchenplaner.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Contains Tests for the generic algorithm classes
 */
class AlgorithmChecks {
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
            5,
            components.size,
            "Found an incorrect number of strong components"
        )

        val comp = components.find { it.n > 1 }

        Assertions.assertNotNull(comp)

        Assertions.assertArrayEquals(
            arrayOf(3, 4, 6, 7, 8),
            comp?.vertices,
            "Found incorrect vertices"
        )
        Assertions.assertArrayEquals(
            arrayOf(0, 2, 3, 4, 6, 7),
            comp?.edgePointers,
            "Found incorrect edge pointers"
        )
        Assertions.assertArrayEquals(
            arrayOf(4, 6, 3, 7, 4, 8, 6),
            comp?.edges,
            "Found incorrect edges"
        )
    }
}