/*
 * KitchenPlanerApp is the android app frontend for the KitchenPlaner, a tool
 * to cooperatively plan a meal plan for a campout.
 * Copyright (C) 2023-2024 Arne Kuchenbecker, Antonia Heiming
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

package com.scouts.kitchenplaner.model.entities

import com.scouts.kitchenplaner.model.DomainLayerRestricted

class AllergenCheck {
    /**
     * Contains for a meal slot, which combination of allergens is definitely not covered by any
     * recipe (first), for which combination of allergens it is not known whether a recipe covers
     * them (second) and for which combination of allergens there is a recipe that covers them (third)
     */
    private val _coveredPersons = mutableListOf<AllergenPerson>()
    val coveredPersons: List<AllergenPerson>
        get() = _coveredPersons

    private val _unknownPersons = mutableListOf<AllergenPerson>()
    val unknownPersons: List<AllergenPerson>
        get() = _unknownPersons

    private val _notCoveredPersons = mutableListOf<AllergenPerson>()
    val notCoveredPersons: List<AllergenPerson>
        get() = _notCoveredPersons


    /**
     * Returns the worst AllergenMealCover for the given meal, i.e. if there is at least one
     * combination of allergens which is not covered, returns NOT_COVERED, if all combinations of
     * allergens are covered, returns COVERED otherwise UNKNOWN
     */
    val mealCover : AllergenMealCover
        get() = if (notCoveredPersons.isNotEmpty()) {
            AllergenMealCover.NOT_COVERED
        } else if (unknownPersons.isNotEmpty()) {
            AllergenMealCover.UNKNOWN
        } else {
            AllergenMealCover.COVERED
        }

    @DomainLayerRestricted
    fun addAllergenPerson(coverType: AllergenMealCover, person: AllergenPerson) {
        val list = when (coverType) {
            AllergenMealCover.COVERED -> _coveredPersons
            AllergenMealCover.UNKNOWN -> _unknownPersons
            AllergenMealCover.NOT_COVERED -> _notCoveredPersons
        }
        list.add(person)
    }

    override fun equals(other: Any?): Boolean = (other is AllergenCheck)
            && _coveredPersons == other._coveredPersons
            && _unknownPersons == other._unknownPersons
            && _notCoveredPersons == other._unknownPersons

    override fun hashCode(): Int {
        var result = _coveredPersons.hashCode()
        result = 31 * result + _unknownPersons.hashCode()
        result = 31 * result + _notCoveredPersons.hashCode()
        return result
    }
}