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

import com.scouts.kitchenplaner.between
import com.scouts.kitchenplaner.model.DomainLayerRestricted
import java.util.Date

class MealPlan (
    private var _startDate: Date,
    private var _endDate: Date,
    initialMeals: List<String> = listOf(),
    initialPlan: Map<MealSlot, Pair<RecipeStub, List<RecipeStub>>> = mutableMapOf(),
    initialNumberChanges: Map<MealSlot, MealNumberChange> = mutableMapOf()
) {
    private var _plan: Map<MealSlot, Pair<RecipeStub, List<RecipeStub>>> = initialPlan.filter { (slot, _) ->
        initialMeals.contains(slot.meal) && slot.date.between(_startDate, _endDate)
    }.toMutableMap()
    private var _meals: List<String> = initialMeals.toMutableList()
    private var _numberChanges: Map<MealSlot, MealNumberChange> = initialNumberChanges.filter { (slot, _) ->
        initialMeals.contains(slot.meal) && slot.date.between(_startDate, _endDate)
    }.toMutableMap()

    val meals: List<String>
        get() = _meals

    val startDate: Date
        get() = _startDate

    val endDate: Date
        get() = _endDate

    operator fun get(mealSlot: MealSlot) : Pair<Pair<RecipeStub, List<RecipeStub>>?, Int> {
        assert(meals.contains(mealSlot.meal))
        var people = 0
        _numberChanges.filter { (slot, _) ->
            slot.date.before(mealSlot.date)
                    || (slot.date == mealSlot.date
                    && meals.indexOf(slot.meal) < meals.indexOf(mealSlot.meal))
        }.forEach { (_, numberChange) ->
            people += numberChange.before + numberChange.after
        }

        people += _numberChanges[mealSlot]?.before ?: 0
        return Pair(_plan[mealSlot], people)
    }

    @DomainLayerRestricted
    fun setStartDate(startDate: Date) {
        _startDate = startDate
    }

    @DomainLayerRestricted
    fun setEndDate(endDate: Date) {
        _endDate = endDate
    }

    @DomainLayerRestricted
    fun setNumberChanges(numberChanges: Map<MealSlot, MealNumberChange>) {
        _numberChanges = numberChanges.filter { (slot, _) ->
            meals.contains(slot.meal) && slot.date.between(startDate, endDate)
        }
    }

    @DomainLayerRestricted
    fun setMeals(meals: List<String>) {
        _meals = meals
        _numberChanges = _numberChanges.filter { (slot, _) ->
            meals.contains(slot.meal)
        }.toMutableMap()
        _plan = _plan.filter { (slot, _) ->
            meals.contains(slot.meal)
        }.toMutableMap()
    }

    @DomainLayerRestricted
    fun setPlan(plan: Map<MealSlot, Pair<RecipeStub, List<RecipeStub>>>) {
        _plan = plan.filter { (slot, _) ->
            meals.contains(slot.meal) && slot.date.between(startDate, endDate)
        }
    }
}