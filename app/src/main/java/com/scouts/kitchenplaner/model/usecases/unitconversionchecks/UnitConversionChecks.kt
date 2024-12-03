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
                val circleCheck = UnitConversionGraph(conversions)
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
            failureCause = UnitConversionCheckFailureCause.AMBIGUOUS
            problemTextConversions[id] = handledList
        }
        handledTextConversions[id] = handledList
    }

    @OptIn(DomainLayerRestricted::class)
    private fun handleRegexConversion(conversion: UnitConversion) {
        var handledList = handledRegexConversions[conversion.sourceUnit]
        if (handledList == null) {
            handledList = mutableListOf()
        }
        handledList.add(conversion)
        if (handledList.size > 1) {
            failureCause = UnitConversionCheckFailureCause.AMBIGUOUS
            problemRegexConversions[conversion.sourceUnit] = handledList
        }
        handledRegexConversions[conversion.sourceUnit] = handledList
    }
}

/**
 * Represents the result of a UnitConversionCheck. On unsuccessful check, problematic conversions
 * can be identified by their name and unit (in case of text conversions) or just their unit (in
 * case of regex conversions) and the iterated through by using the operator get() functions.
 *
 * @param problemTextConversions All problematic text conversions found by the check
 * @param problemRegexConversion All problematic regex conversions found by the check
 * @param circles All circles found
 * @param failureCause The result of the check (either NONE if it was successful or the reason for
 *                     it to fail if it was not)
 */
class UnitConversionCheckResult internal constructor(
    private val problemTextConversions: Map<Pair<String, String>, List<UnitConversion>>,
    private val problemRegexConversion: Map<String, List<UnitConversion>>,
    val circles: List<Circle<UnitConversion>>,
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

/**
 * Encodes the reason for a UnitConversionCheck to fail
 */
enum class UnitConversionCheckFailureCause {
    /**
     * The UnitConversionCheck was successful
     */
    NONE,

    /**
     * The UnitConversionCheck failed because there was some ingredient for which there exist more
     * than one unit conversion
     */
    AMBIGUOUS,

    /**
     * The UnitConversionCheck failed because there were circular conversions
     */
    CIRCLE
}
