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

import com.scouts.kitchenplaner.model.usecases.unitconversionchecks.Graph

class DFS(
    private val graph: Graph
) {
    private var init: () -> Unit = {}
    private var root: (Int) -> Unit = { _ -> }
    private var traverseNonTreeEdge: (Int, Int) -> Unit = { _, _ -> }
    private var traverseTreeEdge: (Int, Int) -> Unit = { _, _ -> }
    private var backtrack: (Int, Int) -> Unit = { _, _ -> }

    private var dfsPos = -1
    private val dfsNums = Array(graph.n) { graph.n + 1 }

    private var marked = Array(graph.n) { false }
    operator fun get(v: Int) = dfsNums[graph.convertVertexNames(v)]
    fun initialize(
        init: () -> Unit = {},
        root: (Int) -> Unit = { _ -> },
        traverseNonTreeEdge: (Int, Int) -> Unit = { _, _ -> },
        traverseTreeEdge: (Int, Int) -> Unit = { _, _ -> },
        backtrack: (Int, Int) -> Unit = { _, _ -> }
    ) {
        this.init = {
            dfsPos = 1
            init()
        }
        this.root = { s ->
            dfsNums[graph.convertVertexNames(s)] = dfsPos++
            root(s)
        }
        this.traverseNonTreeEdge = traverseNonTreeEdge
        this.traverseTreeEdge = { v, w ->
            dfsNums[graph.convertVertexNames(w)] = dfsPos++
            traverseTreeEdge(v, w)
        }
        this.backtrack = backtrack
    }

    fun run() {
        marked = Array(graph.n) { false }
        init()
        for (s in graph.vertices) {
            if (!marked[graph.convertVertexNames(s)]) {
                marked[graph.convertVertexNames(s)] = true
                root(s)
                dfs(s, s)
            }
        }
    }

    private fun dfs(u: Int, v: Int) {
        for (w in graph.getOutwardNeighbours(v)) {
            if (marked[graph.convertVertexNames(w)]) {
                traverseNonTreeEdge(v, w)
            } else {
                traverseTreeEdge(v, w)
                marked[graph.convertVertexNames(w)] = true
                dfs(v, w)
            }
        }
        backtrack(u, v)
    }
}