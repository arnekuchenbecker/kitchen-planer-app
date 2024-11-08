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

import com.scouts.kitchenplaner.model.DomainLayerRestricted
import com.scouts.kitchenplaner.model.entities.UnitConversion

/**
 * Class for checking if a set of unit conversions is valid. A set of unit conversions is considered
 * valid if and only if
 *      a) for every source unit there is at most one regex conversion from that unit
 *         and at most one regex conversion with any given text
 *      b) no circles are present
 *
 * @param conversions The set of UnitConversions that should be checked
 */
class UnitConversionChecks(private val conversions: List<UnitConversion>) {
    private var hasRun = false

    private val handledTextConversions = mutableMapOf<Pair<String, String>, MutableList<UnitConversion>>()
    private val handledRegexConversions = mutableMapOf<String, MutableList<UnitConversion>>()

    private var failureCause = UnitConversionCheckFailureCause.NONE
    private val problemTextConversions = mutableMapOf<Pair<String, String>, List<UnitConversion>>()
    private val problemRegexConversions = mutableMapOf<String, List<UnitConversion>>()
    private var circles = listOf<Circle<UnitConversion>>()

    /**
     * Checks the [conversions] this object was created with.
     *
     * @return A [UnitConversionCheckResult] object representing the result of the check
     */
    fun run() : UnitConversionCheckResult {
        if (!hasRun) {
            conversions.forEach {
                when(it) {
                    is UnitConversion.TextConversion -> handleTextConversion(it)
                    is UnitConversion.RegexConversion -> handleRegexConversion(it)
                }
            }
            hasRun = true

            if (failureCause == UnitConversionCheckFailureCause.NONE) {
                val circleCheck = UnitConversionForest(conversions)
                circles = circleCheck.findCircles()
                if (circles.isNotEmpty()) {
                    failureCause = UnitConversionCheckFailureCause.CIRCLE
                }
            }
        }

        return UnitConversionCheckResult(problemTextConversions, problemRegexConversions, circles, failureCause)
    }

    @OptIn(DomainLayerRestricted::class)
    private fun handleTextConversion(conversion: UnitConversion) {
        val id = Pair(conversion.representation, conversion.sourceUnit)
        var handledList = handledTextConversions[id]
        if (handledList == null) {
            handledList = mutableListOf()
        }
        handledList.add(conversion)
        if (handledList.size > 1) {
            problemTextConversions[id] = handledList
            failureCause = UnitConversionCheckFailureCause.AMBIGUOUS
        }
    }

    @OptIn(DomainLayerRestricted::class)
    private fun handleRegexConversion(conversion: UnitConversion) {
        var handledList = handledRegexConversions[conversion.sourceUnit]
        if (handledList == null) {
            handledList = mutableListOf()
        }
        handledList.add(conversion)
        if (handledList.size > 1) {
            problemRegexConversions[conversion.sourceUnit] = handledList
            failureCause = UnitConversionCheckFailureCause.AMBIGUOUS
        }
    }
}

/**
 * Represents the result of a UnitConversionCheck. On unsuccessful check, problematic conversions
 * can be identified by their name and unit (in case of text conversions) or just their unit (in
 * case of regex conversions) and the iterated through by using the operator get() functions.
 *
 * @param problemTextConversions All problematic text conversions found by the check
 * @param problemRegexConversion All problematic regex conversions found by the check
 */
class UnitConversionCheckResult internal constructor(
    private val problemTextConversions: Map<Pair<String, String>, List<UnitConversion>>,
    private val problemRegexConversion: Map<String, List<UnitConversion>>,
    private val circles: List<Circle<UnitConversion>>,
    val failureCause: UnitConversionCheckFailureCause
) {
    /**
     * Whether the check was successful. A check is considered successful if and only if no
     * problematic text conversions and no problematic regex conversions have been found.
     */
    val isSuccessful: Boolean
        get() = failureCause == UnitConversionCheckFailureCause.NONE

    /**
     * Pair(name, sourceUnit) for all problematic text conversions
     */
    val textProblems = problemTextConversions.keys

    /**
     * sourceUnit for all problematic regex conversions
     */
    val regexProblems = problemRegexConversion.keys

    /**
     * Get the problematic regex conversions with the specified source unit
     */
    operator fun get(unit: String) = problemRegexConversion[unit]

    /**
     * Get the problematic text conversions with the specified name and source unit
     */
    operator fun get(text: String, unit: String) = problemTextConversions[Pair(text, unit)]
}

enum class UnitConversionCheckFailureCause {
    NONE,
    AMBIGUOUS,
    CIRCLE
}

@OptIn(DomainLayerRestricted::class)
class UnitConversionForest(conversions: List<UnitConversion>) {
    val trees: List<UnitConversionTree>

    init {
        val treeContents = mutableMapOf<String, MutableList<UnitConversion>>()
        val regexConversions = mutableListOf<UnitConversion>()
        conversions.forEach { conversion ->
            when (conversion) {
                is UnitConversion.TextConversion -> {
                    if (treeContents[conversion.representation] == null) {
                        treeContents[conversion.representation] = mutableListOf()
                    }
                    treeContents[conversion.representation]?.add(conversion)
                }
                is UnitConversion.RegexConversion -> {
                    regexConversions.add(conversion)
                }
            }
        }
        regexConversions.forEach { conversion ->
            treeContents.keys.forEach { pattern ->
                treeContents[pattern]?.add(conversion)
            }
        }
        trees = treeContents.map { (_, contents) -> createTree(contents) } +
                listOf(createTree(regexConversions))
    }

    fun findCircles(): List<Circle<UnitConversion>> = trees.map { it.findCircles() }.flatten()

    private fun createTree(conversions: List<UnitConversion>) : UnitConversionTree {
        val edgeList = mutableListOf<Int>()
        val vertices = Array(conversions.size) { i -> i + 1 }
        val edgePointers = Array(conversions.size + 1) { i ->
            if (i < conversions.size) {
                val edgeStart = edgeList.size
                conversions.forEachIndexed { index, it ->
                    if (it.sourceUnit == conversions[i].destinationUnit) {
                        edgeList.add(index + 1)
                    }
                }
                edgeStart
            } else {
                edgeList.size
            }
        }

        val edges = Array(edgeList.size) { i -> edgeList[i] }

        return UnitConversionTree(
            conversions = conversions,
            graph = Graph(vertices, edgePointers, edges)
        )
    }
}

class UnitConversionTree(
    val conversions: List<UnitConversion>,
    val graph: Graph
) {
    fun findCircles(): List<Circle<UnitConversion>> {
        val circleFinder = CircleSearch(graph)

        return circleFinder.run().map { circle -> circle.map { v -> conversions[graph.convertVertexNames(v)] } }
    }


}

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
        var f = false
        stack.push(v)
        blocked[graph.convertVertexNames(v)] = true

        for (u in g_k.getOutwardNeighbours(v)) {
            if (u == s) {
                circles.add(Circle(stack.content))
                f = true
            } else if (!blocked[graph.convertVertexNames(u)]) {
                if (circuit(u)) {
                    f = true
                }
            }
        }

        if (f) {
            unblock(v)
        } else {
            for (u in g_k.getOutwardNeighbours(v)) {
                if (!(b[graph.convertVertexNames(u)].contains(v))) {
                    b[graph.convertVertexNames(u)].add(v)
                }
            }
        }
        stack.pop()
        return f
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
        val componentGraphs = componentRepresentatives
            .map { (v, rep) -> Pair(v, rep) }
            .groupBy { it.second }
            .values
            .filter { it.size > 1 }
            .map { list -> list.map { it.first }.sorted() }
            .map { vertices -> graph.inducedSubgraph(vertices) }
        return componentGraphs
    }
}

class Stack<T> {
    private val _deque = ArrayDeque<T>()

    fun push(e: T) = _deque.addLast(e)

    fun pop() = _deque.removeLast()

    fun top() = _deque.last()

    fun contains(e: T) = _deque.contains(e)

    val content: List<T>
        get() = _deque.toList()
}

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

class Circle<T : Any>(private val vertices: List<T>) {
    val length: Int
        get() = vertices.size

    constructor(vararg v: T) : this(v.toList())

    fun <S : Any> map(transform: (T) -> S) : Circle<S> {
        return Circle(vertices.map(transform))
    }

    override fun toString(): String {
        return vertices.toString()
    }

    override fun hashCode(): Int {
        return vertices.fold(0) { acc, t -> acc + t.hashCode() }
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Circle<*>) {
            if (vertices.isEmpty()) {
                other.vertices.isEmpty()
            } else if (vertices.size != other.vertices.size) {
                false
            } else {
                val startIndex = vertices.indexOf(other.vertices[0])
                if (startIndex == -1) {
                    false
                } else {
                    var equal = true
                    for (i in vertices.indices) {
                        if (vertices[(startIndex + i) % vertices.size] != other.vertices[i]) {
                            equal = false
                        }
                    }
                    equal
                }
            }
        } else {
            false
        }
    }
}
