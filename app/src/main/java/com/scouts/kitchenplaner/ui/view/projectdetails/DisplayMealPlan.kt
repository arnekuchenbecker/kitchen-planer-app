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

package com.scouts.kitchenplaner.ui.view.projectdetails

import androidx.compose.runtime.Composable
import com.scouts.kitchenplaner.ui.view.LazyColumnWrapper
import java.util.Date

data class MealSlot(val meal: String, val date: Date)
data class RecipeStub(val id: Long, val name: String)

@Composable
fun DisplayMealPlan (mealSlots: List<MealSlot>, mealPlan: Map<MealSlot, Pair<Pair<RecipeStub, List<RecipeStub>>?, Int>>) {
    val test =
    LazyColumnWrapper(
        content = mealSlots.mapNotNull { mealPlan[it] },
        DisplayContent = { it, index ->

        }) {

    }
}