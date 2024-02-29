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
    initialNumberChanges: Map<MealSlot, Int> = mutableMapOf()
) {
    private var _plan: Map<MealSlot, Pair<RecipeStub, List<RecipeStub>>> = initialPlan.filter { (slot, _) ->
        initialMeals.contains(slot.meal) && slot.date.between(_startDate, _endDate)
    }.toMutableMap()
    private var _meals: List<String> = initialMeals.toMutableList()
    private var _numberChanges: Map<MealSlot, Int> = initialNumberChanges.filter { (slot, _) ->
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
        val people = _numberChanges.filter { (slot, _) ->
            slot.before(mealSlot, meals)
        }.values.reduceOrNull { first, second -> first + second } ?: 0

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
    fun setNumberChanges(numberChanges: Map<MealSlot, Int>) {
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

    override fun equals(other: Any?): Boolean = (other is MealPlan)
            && _startDate == other._startDate
            && _endDate == other._endDate
            && _meals == other._meals
            && _plan == other._plan
            && _numberChanges == other._numberChanges

    override fun hashCode(): Int {
        var result = _startDate.hashCode()
        result = 31 * result + _endDate.hashCode()
        result = 31 * result + _plan.hashCode()
        result = 31 * result + _meals.hashCode()
        result = 31 * result + _numberChanges.hashCode()
        return result
    }
}