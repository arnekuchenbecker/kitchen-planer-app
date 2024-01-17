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

import com.scouts.kitchenplaner.datalayer.repositories.RecipeRepository
import com.scouts.kitchenplaner.model.DomainLayerRestricted
import com.scouts.kitchenplaner.model.entities.AllergenCheck
import com.scouts.kitchenplaner.model.entities.AllergenMealCover
import com.scouts.kitchenplaner.model.entities.AllergenPerson
import com.scouts.kitchenplaner.model.entities.DietaryTypes
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.RecipeStub
import com.scouts.kitchenplaner.model.entities.before

class CheckAllergens (
    private val recipeRepository: RecipeRepository
) {
    @OptIn(DomainLayerRestricted::class)
    suspend fun getAllergenCheck(project: Project) : AllergenCheck {
        val check = AllergenCheck()
        project.mealSlots.forEach { slot ->
            check.addEmptySlot(slot)
            project.allergenPersons.filter {
                MealSlot(it.arrivalDate, it.arrivalMeal).before(slot, project.meals)
                        && slot.before(MealSlot(it.departureDate, it.departureMeal), project.meals)
            }.forEach { person ->
                var coverType = AllergenMealCover.NOT_COVERED
                val recipes = project.mealPlan[slot].first
                if (recipes != null) {
                    val mainCover = checkRecipe(recipes.first, person)
                    if (mainCover != AllergenMealCover.COVERED) {
                        if (mainCover == AllergenMealCover.UNKNOWN) {
                            coverType = AllergenMealCover.UNKNOWN
                        }
                        recipes.second.forEach {
                            val alternativeCheck = checkRecipe(it, person)
                            if (coverType == AllergenMealCover.NOT_COVERED) {
                                coverType = alternativeCheck
                            } else if (coverType == AllergenMealCover.UNKNOWN && alternativeCheck == AllergenMealCover.COVERED) {
                                coverType = alternativeCheck
                            }
                        }
                    } else {
                        coverType = AllergenMealCover.COVERED
                    }
                }
                check.addAllergenPerson(slot, coverType, person)
            }
        }
        return check
    }

    private suspend fun checkRecipe(stub: RecipeStub, person: AllergenPerson) : AllergenMealCover {
        val dietarySpecialities = recipeRepository.getAllergensForRecipe(stub.id ?: return AllergenMealCover.UNKNOWN)
        val foundConflict = person.allergens.any { allergen ->
            dietarySpecialities.filter { it.allergen == allergen.allergen }.any { speciality ->
                if (allergen.traces) {
                    speciality.type != DietaryTypes.FREE_OF
                } else {
                    speciality.type == DietaryTypes.ALLERGEN
                }
            }
        }
        if (foundConflict) {
            return AllergenMealCover.NOT_COVERED
        }

        val allCovered = person.allergens.all { allergen ->
            val foundAllergen = dietarySpecialities.filter { it.allergen == allergen.allergen }
            foundAllergen.isNotEmpty() && foundAllergen.all { speciality ->
                if (allergen.traces) {
                    speciality.type == DietaryTypes.FREE_OF
                } else {
                    speciality.type !=DietaryTypes.ALLERGEN
                }
            }
        }
        return if (allCovered) {
            AllergenMealCover.COVERED
        } else {
            AllergenMealCover.UNKNOWN
        }
    }
}
