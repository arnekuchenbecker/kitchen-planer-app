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

import com.scouts.kitchenplaner.model.usecases.unitconversionchecks.Circle

class CircleSearch(
    private val graph: Graph
) {
    private val blocked = Array(graph.n) { false }
    private val b = Array(graph.n) { mutableListOf<Int>() }
    private val stack = Stack<Int>()
    private var s = 1
    private val circles = mutableListOf<Circle<Int>>()
    private var g_k = Graph.empty()

    private var hasRun = false
    fun run(): List<Circle<Int>> {
        if (!hasRun) {
            while (s < graph.n) {
                val comps: List<Graph> =
                    findNonTrivialStrongComponents()
                if (comps.isEmpty()) {
                    s = graph.n
                } else {
                    g_k = comps.sortedBy { g -> g.vertices.min() }[0]
                    s = g_k.vertices.min()
                    for (i in g_k.vertices.map { graph.convertVertexNames(it) }) {
                        blocked[i] = false
                        b[i] = mutableListOf()
                    }
                    circuit(s)
                    s++
                }
            }
        }
        return circles
    }

    private fun circuit(v: Int): Boolean {
        var found = false
        stack.push(v)
        blocked[graph.convertVertexNames(v)] = true

        for (u in g_k.getOutwardNeighbours(v)) {
            if (u == s) {
                circles.add(Circle(stack.content))
                found = true
            } else if (!blocked[graph.convertVertexNames(u)]) {
                if (circuit(u)) {
                    found = true
                }
            }
        }

        if (found) {
            unblock(v)
        } else {
            for (u in g_k.getOutwardNeighbours(v)) {
                if (!(b[graph.convertVertexNames(u)].contains(v))) {
                    b[graph.convertVertexNames(u)].add(v)
                }
            }
        }
        stack.pop()
        return found
    }

    private fun unblock(v: Int) {
        blocked[graph.convertVertexNames(v)] = false
        for (i in 0..<b[graph.convertVertexNames(v)].size) {
            val u = b[graph.convertVertexNames(v)].removeAt(0)
            if (blocked[graph.convertVertexNames(u)]) {
                unblock(u)
            }
        }
    }

    private fun findNonTrivialStrongComponents(): List<Graph> {
        val subset = (s..graph.n).toList()
        val subgraph = graph.inducedSubgraph(subset)
        val sccFinder = SCCFinder(subgraph)
        return sccFinder.run()
    }
}