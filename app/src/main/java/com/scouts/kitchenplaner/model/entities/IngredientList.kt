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
import java.util.SortedSet

class IngredientList {
    private val _ingredients = mutableMapOf<MealSlot, SortedSet<Ingredient>>()
    
    fun toList(meals: List<String>) : List<Pair<MealSlot, Set<Ingredient>>> {
        return _ingredients.toList().sortedWith(Comparator { first, second ->
            if (first.first.date.before(second.first.date)) {
                return@Comparator -1
            } else if (first.first.date.after(second.first.date)) {
                return@Comparator 1
            } else {
                return@Comparator compareValues(meals.indexOf(first.first.meal), meals.indexOf(second.first.meal))
            }
        })
    }

    @DomainLayerRestricted
    fun addIngredient(ingredient: Ingredient, mealSlot: MealSlot) {
        val set = _ingredients[mealSlot] ?: addNewSet(mealSlot)
        if (set.any { it.name == ingredient.name && it.unit == ingredient.unit }) {
            val found = set.first { it.name == ingredient.name }
            found.setAmount(found.amount + ingredient.amount)
        } else {
            set.add(ingredient)
        }
    }

    private fun addNewSet(slot: MealSlot) : SortedSet<Ingredient> {
        val newSet = sortedSetOf<Ingredient>(compareBy({ it.name }, { it.unit }))
        _ingredients[slot] = newSet
        return newSet
    }
}