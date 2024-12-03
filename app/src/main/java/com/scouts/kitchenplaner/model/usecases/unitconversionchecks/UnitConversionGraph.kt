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

package com.scouts.kitchenplaner.model.usecases.unitconversionchecks

import com.scouts.kitchenplaner.model.DomainLayerRestricted
import com.scouts.kitchenplaner.model.entities.UnitConversion
import com.scouts.kitchenplaner.utils.CircleSearch
import com.scouts.kitchenplaner.utils.Graph

/**
 * A graph for checking unit conversions on circular dependencies. Unit conversions for different
 * ingredients are split into subgraphs, which are not connected to each other. Regex conversions
 * are present in all subgraphs as checking if two regular expressions are equivalent is hard.
 *
 * @param conversions The conversions to be checked for circles
 */
@OptIn(DomainLayerRestricted::class)
class UnitConversionGraph(conversions: List<UnitConversion>) {
    /**
     * The subgraphs for the different ingredients
     */
    val parts: List<UnitConversionSubgraph>

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
        parts = treeContents.map { (_, contents) -> createSubgraph(contents) } +
                listOf(createSubgraph(regexConversions))
    }

    /**
     * Searches for circles in each subgraph
     *
     * @return A list of the found circles
     */
    fun findCircles(): List<Circle<UnitConversion>> = parts.map { it.findCircles() }.flatten()

    private fun createSubgraph(conversions: List<UnitConversion>) : UnitConversionSubgraph {
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

        return UnitConversionSubgraph(
            conversions = conversions,
            graph = Graph(vertices, edgePointers, edges)
        )
    }
}

/**
 * A subgraph of a [UnitConversionGraph] containing vertices only for unit conversions with the same
 * ingredient name and regex conversions
 *
 * @param conversions The unit conversions represented by the vertices of the graph
 * @param graph The graph representing the dependencies between the unit conversions
 */
class UnitConversionSubgraph(
    val conversions: List<UnitConversion>,
    val graph: Graph
) {
    /**
     * Finds all circles in this subgraph
     *
     * @return All circles in this subgraph
     */
    fun findCircles(): List<Circle<UnitConversion>> {
        val circleFinder = CircleSearch(graph)

        return circleFinder.run().map { circle -> circle.map { v -> conversions[graph.convertVertexNames(v)] } }
    }
}