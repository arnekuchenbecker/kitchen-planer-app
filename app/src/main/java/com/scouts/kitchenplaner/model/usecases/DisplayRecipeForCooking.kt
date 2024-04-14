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

package com.scouts.kitchenplaner.model.usecases

import com.scouts.kitchenplaner.datalayer.repositories.AllergenRepository
import com.scouts.kitchenplaner.datalayer.repositories.RecipeRepository
import com.scouts.kitchenplaner.model.entities.DietarySpeciality
import com.scouts.kitchenplaner.model.entities.DietaryTypes
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.RecipeAlternative
import com.scouts.kitchenplaner.model.entities.RecipeForCooking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class DisplayRecipeForCooking(
    private val recipeRepository: RecipeRepository,
    private val allergenRepository: AllergenRepository
) {
    fun showRecipeForCooking(
        project: Project,
        mealSlot: MealSlot,
        recipeID: Long
    ): Flow<RecipeForCooking> {
        val allergenPersonsFlow = allergenRepository.getAllergenPersonsByProjectID(project.id)
        val allergensFlow = recipeRepository.getAllergensForRecipe(recipeID)
        val alternativeRecipes =
            project.mealPlan[mealSlot].first?.second?.map { recipeRepository.getRecipeById(it.id) }
                ?: listOf()

        val combinedAlternatives = combine(alternativeRecipes) { it.toList() }

        val allergensForAlternatives = combine(
            combinedAlternatives,
            allergensFlow,
            allergenPersonsFlow
        ) { alternatives, allergens, allergenPersons ->
            val allergenMap = mutableMapOf<Long, List<DietarySpeciality>>()
            alternatives.forEach {
                val relevantTraces = it.traces
                    .filter { trace ->
                        allergens.any { speciality ->
                            speciality.allergen == trace && speciality.type == DietaryTypes.ALLERGEN
                        }
                    }
                    .map { name -> Pair(name, DietaryTypes.TRACE) }

                val relevantFrees = it.freeOfAllergen
                    .filter { free ->
                        allergens.any { speciality ->
                            speciality.allergen == free && speciality.type != DietaryTypes.FREE_OF
                        }
                    }
                    .map { name -> Pair(name, DietaryTypes.FREE_OF) }
                val relevantAllergens = (relevantTraces + relevantFrees).filter { (name, type) ->
                    allergenPersons.any { person ->
                        person.allergens.any { allergen ->
                            val weakerType = if (type == DietaryTypes.FREE_OF) {
                                true
                            } else {
                                type == DietaryTypes.TRACE && !allergen.traces
                            }
                            allergen.allergen == name && weakerType
                        }
                    }
                }.map { (name, type) -> DietarySpeciality(name, type) }

                allergenMap[it.id] = relevantAllergens
            }

            allergenMap
        }

        return recipeRepository.getRecipeById(recipeID)
            .combine(allergensForAlternatives) { recipe, allergens ->
                val alternatives = project.mealPlan[mealSlot].first?.second?.map {
                    RecipeAlternative(it.id, it.name, allergens[it.id] ?: listOf())
                } ?: listOf()
                RecipeForCooking(recipe, project.mealPlan[mealSlot].second, alternatives)
            }
    }
}