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

class Graph(
    val vertices: Array<Int>,
    val edgePointers: Array<Int>,
    val edges: Array<Int>,
    val convertVertexNames: (Int) -> Int = { i -> i - 1 }
) {
    companion object {
        fun empty(): Graph {
            return Graph(arrayOf(), arrayOf(1), arrayOf())
        }
    }

    val n = vertices.size
    val m = edges.size

    fun getOutwardNeighbours(v: Int): List<Int> {
        return edges.slice(edgePointers[convertVertexNames(v)]..<edgePointers[convertVertexNames(v) + 1])
    }

    fun inducedSubgraph(subset: List<Int>): Graph {
        val conversion: (Int) -> Int = { i -> subset.indexOf(i) }
        val edgeList = subset.map { v ->
            getOutwardNeighbours(v).filter { subset.contains(it) }
        }
        val newEdges = edgeList.flatten().toTypedArray()
        val newEdgePointers = edgeList.runningFold(0) { acc, es -> acc + es.size }.toTypedArray()

        return Graph(subset.toTypedArray(), newEdgePointers, newEdges, conversion)
    }
}