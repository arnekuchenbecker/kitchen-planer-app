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

class SCCFinder(
    private val graph: Graph
) {
    fun run(): List<Graph> {
        val representatives = Stack<Int>()
        val nodes = Stack<Int>()
        val componentRepresentatives = mutableMapOf<Int, Int>()
        val dfs = DFS(graph)
        dfs.initialize(
            root = { v ->
                representatives.push(v)
                nodes.push(v)
            },
            traverseTreeEdge = { _, w ->
                representatives.push(w)
                nodes.push(w)
            },
            traverseNonTreeEdge = { v, w ->
                if (nodes.contains(w)) {
                    while (dfs[w] < dfs[representatives.top()]) {
                        representatives.pop()
                    }
                }
            },
            backtrack = { _, v ->
                if (v == representatives.top()) {
                    representatives.pop()
                    do {
                        val w = nodes.pop()
                        componentRepresentatives[w] = v
                    } while (w != v)
                }
            }
        )
        dfs.run()
        return componentRepresentatives
            .map { (v, rep) -> Pair(v, rep) }
            .groupBy { it.second }
            .values
            .filter { it.size > 1 }
            .map { list -> list.map { it.first }.sorted() }
            .map { vertices -> graph.inducedSubgraph(vertices) }
    }
}