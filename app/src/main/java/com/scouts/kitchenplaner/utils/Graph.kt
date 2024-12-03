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

/**
 * A directed graph. Can contain both multi-edges and loops. Edges are represented by an adjacency
 * array data structure.
 *
 * @param vertices The vertices names of the graph
 * @param edgePointers For each vertex contains the starting index of outgoing edges in [edges] and
 *                     an additional entry of edges.size as a sentinel value
 * @param edges The destination vertex of the edges
 * @param convertVertexNames A function mapping each vertex name to an index in [0..n] where n is
 *                           the size of this graph
 */
class Graph(
    val vertices: Array<Int>,
    val edgePointers: Array<Int>,
    val edges: Array<Int>,
    val convertVertexNames: (Int) -> Int = { i -> i - 1 }
) {
    companion object {
        /**
         * Create the empty graph (i.e. no vertices, no edges)
         *
         * @return The empty graph
         */
        fun empty(): Graph {
            return Graph(arrayOf(), arrayOf(1), arrayOf())
        }
    }

    /**
     * The size of this graph
     */
    val n = vertices.size

    /**
     * The order of this graph
     */
    val m = edges.size

    /**
     * Get the outward neighbors of a vertex [v], i.e. all vertices u s.t. an edge (v, u) exists
     *
     * @param v The vertex for which to get the neighborhood
     * @return All outward neighbors of [v]
     */
    fun getOutwardNeighbors(v: Int): List<Int> {
        return edges.slice(edgePointers[convertVertexNames(v)]..<edgePointers[convertVertexNames(v) + 1])
    }

    /**
     * Get the subgraph induced by [subset], i.e. the graph that consists of the vertices in
     * [subset] and all edges between vertices in [subset]
     *
     * @param subset A subset of the vertices in this graph
     * @return The subgraph induced by [subset]
     */
    fun inducedSubgraph(subset: List<Int>): Graph {
        val conversion: (Int) -> Int = { i -> subset.indexOf(i) }
        val edgeList = subset.map { v ->
            getOutwardNeighbors(v).filter { subset.contains(it) }
        }
        val newEdges = edgeList.flatten().toTypedArray()
        val newEdgePointers = edgeList.runningFold(0) { acc, es -> acc + es.size }.toTypedArray()

        return Graph(subset.toTypedArray(), newEdgePointers, newEdges, conversion)
    }
}