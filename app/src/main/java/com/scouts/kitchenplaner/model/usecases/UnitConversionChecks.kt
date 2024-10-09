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

package com.scouts.kitchenplaner.model.usecases

import androidx.compose.ui.graphics.Vertices
import com.scouts.kitchenplaner.model.DomainLayerRestricted
import com.scouts.kitchenplaner.model.entities.UnitConversion

@OptIn(DomainLayerRestricted::class)
class UnitConversionForest(conversions: List<UnitConversion>) {
    private val literalMatcher = Regex("^[a-zA-Z0-9]*$")
    val trees: List<UnitConversionTree>

    init {
        val treeContents = mutableMapOf<String, MutableList<UnitConversion>>()
        val regexConversions = mutableListOf<UnitConversion>()
        conversions.forEach { conversion ->
            if (isRegexLiteral(conversion.pattern)) {
                if (treeContents[conversion.pattern] == null) {
                    treeContents[conversion.pattern] = mutableListOf()
                }
                treeContents[conversion.pattern]?.add(conversion)
            } else {
                regexConversions.add(conversion)
            }
        }
        regexConversions.forEach { conversion ->
            treeContents.keys.forEach { pattern ->
                treeContents[pattern]?.add(conversion)
            }
        }
        trees = treeContents.map { (_, contents) ->
            val edgeList = mutableListOf<Int>()
            val vertices = Array(contents.size) { i ->
                val edgeStart = edgeList.size
                contents
                    .filter { it.sourceUnit == contents[i].destinationUnit }
                    .forEachIndexed { index, _ -> edgeList.add(index) }
                edgeStart
            }

            edgeList.add(edgeList.size)

            val edges = Array(edgeList.size) { i -> edgeList[i] }

            UnitConversionTree(
                patterns = Array(contents.size) { i -> contents[i].pattern },
                sourceUnits = Array(contents.size) { i -> contents[i].sourceUnit },
                destinationUnits = Array(contents.size) { i -> contents[i].destinationUnit },
                vertexArray = vertices,
                edgeArray = edges
            )
        }
    }

    fun findCircles(): List<Circle> = trees.map { it.findCircles() }.flatten()

    private fun isRegexLiteral(pattern: String): Boolean {
        return literalMatcher.matches(pattern)
    }
}

class UnitConversionTree(
    val patterns: Array<String>,
    val sourceUnits: Array<String>,
    val destinationUnits: Array<String>,
    val vertexArray: Array<Int>,
    val edgeArray: Array<Int>
) {
    fun findCircles(): List<Circle> {
        val circles = mutableListOf<Circle>()
        val predecessors = Array(vertexArray.size) { i -> i }
        val dfs = DFS(
            vertexArray = vertexArray,
            edgeArray = edgeArray,
            traverseBackwardsEdge = { v, w -> // There is a circle w -> v ->* w
                circles.add(Circle(v, ))
            },
            traverseTreeEdge = { v, w ->
                predecessors[w] = v
            }
        )
        dfs.run()
        return circles
    }


}

class DFS(
    private val vertexArray: Array<Int>,
    private val edgeArray: Array<Int>,
    private val init: () -> Unit = {},
    private val root: (Int) -> Unit = {},
    private val traverseForwardEdge: (Int, Int) -> Unit = { _, _ -> },
    private val traverseCrossEdge: (Int, Int) -> Unit = { _, _ -> },
    private val traverseBackwardsEdge: (Int, Int) -> Unit = { _, _ -> },
    private val traverseTreeEdge: (Int, Int) -> Unit = { _, _ -> },
    private val backtrack: (Int, Int) -> Unit = { _, _ -> }
) {
    private val n = vertexArray.size

    private var marked = Array(n) { false }

    private var currentDFSNumber = 0
    private var currentFinishNumber = 0

    private var dfsNumber = Array(n) { 0 }
    private var finishNumber = Array(n) { 0 }

    fun run() {
        marked = Array(n) { false }
        dfsNumber = Array(n) { 0 }
        finishNumber = Array(n) { 0 }
        currentDFSNumber = 0
        currentFinishNumber = 0
        init()
        for (i in 0..<n) {
            if (!marked[i]) {
                marked[i] = true
                root(i)
                dfs(i, i)
            }
        }
    }

    private fun dfs(u: Int, v: Int) {
        currentDFSNumber++
        dfsNumber[v] = currentDFSNumber
        for (i in vertexArray[v]..<vertexArray[v + 1]) {
            val w = edgeArray[i]
            if (marked[w]) {
                if (isForwardEdge(v, w)) {
                    traverseForwardEdge(v, w)
                } else if (isCrossEdge(v, w)) {
                    traverseCrossEdge(v, w)
                } else if (isBackwardsEdge(v, w)) {
                    traverseBackwardsEdge(v, w)
                }
            } else {
                traverseTreeEdge(v, w)
                marked[w] = true
                dfs(v, w)
            }
        }
        currentFinishNumber++
        finishNumber[v] = currentFinishNumber
        backtrack(u, v)
    }

    private fun isForwardEdge(v: Int, w: Int): Boolean {
        return marked[w]
                && dfsNumber[v] < dfsNumber[w]
                && finishNumber[w] != 0
    }

    private fun isCrossEdge(v: Int, w: Int): Boolean {
        return marked[w]
                && dfsNumber[v] > dfsNumber[w]
                && finishNumber[w] != 0
    }

    private fun isBackwardsEdge(v: Int, w: Int): Boolean {
        return marked[w]
                && dfsNumber[v] > dfsNumber[w]
                && finishNumber[w] == 0
    }
}

class Circle(vararg vertices: Vertices) {

}