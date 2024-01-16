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
     * Contains for every meal slot, which combination of allergens is definitely not covered by any
     * recipe (first), for which combination of allergens it is not known whether a recipe covers
     * them (second) and for which combination of allergens there is a recipe that covers them (third)
     */
    private val _checks = mutableMapOf<
            MealSlot,
            Triple<
                    MutableList<AllergenPerson>,
                    MutableList<AllergenPerson>,
                    MutableList<AllergenPerson>
                    >
            >()

    /**
     * Returns the worst AllergenMealCover for the given meal, i.e. if there is at least one
     * combination of allergens which is not covered, returns NOT_COVERED, if all combinations of
     * allergens are covered, returns COVERED otherwise UNKNOWN
     */
    operator fun get(slot: MealSlot) : AllergenMealCover {
        val check = _checks[slot] ?: return AllergenMealCover.UNKNOWN
        return if (check.first.isNotEmpty()) {
            AllergenMealCover.NOT_COVERED
        } else if (check.second.isNotEmpty()) {
            AllergenMealCover.UNKNOWN
        } else {
            AllergenMealCover.COVERED
        }
    }

    /**
     * Returns the AllergenPersons whose combination of allergens have the given AllergenMealCover
     * for the given meal slot.
     */
    operator fun get(slot: MealSlot, coverType: AllergenMealCover) : List<AllergenPerson> {
        val check = _checks[slot] ?: return listOf()
        return when(coverType) {
            AllergenMealCover.COVERED -> check.third
            AllergenMealCover.UNKNOWN -> check.second
            AllergenMealCover.NOT_COVERED -> check.first
        }
    }

    @DomainLayerRestricted
    fun addAllergenPerson(slot: MealSlot, coverType: AllergenMealCover, person: AllergenPerson) {
        val list = when (coverType) {
            AllergenMealCover.COVERED -> _checks[slot]?.third
            AllergenMealCover.UNKNOWN -> _checks[slot]?.second
            AllergenMealCover.NOT_COVERED -> _checks[slot]?.first
        }
        list?.add(person)
    }

    @DomainLayerRestricted
    fun addEmptySlot(slot: MealSlot) {
        _checks[slot] = Triple(mutableListOf(), mutableListOf(), mutableListOf())
    }
}