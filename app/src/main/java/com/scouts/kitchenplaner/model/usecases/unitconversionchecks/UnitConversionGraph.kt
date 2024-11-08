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

@OptIn(DomainLayerRestricted::class)
class UnitConversionGraph(conversions: List<UnitConversion>) {
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

class UnitConversionSubgraph(
    val conversions: List<UnitConversion>,
    val graph: Graph
) {
    fun findCircles(): List<Circle<UnitConversion>> {
        val circleFinder = CircleSearch(graph)

        return circleFinder.run().map { circle -> circle.map { v -> conversions[graph.convertVertexNames(v)] } }
    }
}