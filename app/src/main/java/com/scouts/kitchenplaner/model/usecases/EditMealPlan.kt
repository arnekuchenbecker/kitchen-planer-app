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

package com.scouts.kitchenplaner.model.usecases

import com.scouts.kitchenplaner.model.DomainLayerRestricted
import com.scouts.kitchenplaner.model.entities.Project
import javax.inject.Inject

class EditMealPlan @Inject constructor(
    //TODO recipeManagementRepo - missing DataLayer support
) {
    //TODO selectRecipeForMeal(projectId, date, meal)
    //TODO removeRecipeFromMeal(projectId, date, meal)
    //TODO swapMeals(projectId, firstMealSlot, secondMealSlot)

    @OptIn(DomainLayerRestricted::class)
    fun addMeal(project: Project, meal: String, index: Int = -1) {

    }

    //TODO removeMeal
}