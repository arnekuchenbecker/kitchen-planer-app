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

import com.scouts.kitchenplaner.repositories.AllergenRepository
import com.scouts.kitchenplaner.repositories.RecipeRepository
import com.scouts.kitchenplaner.model.entities.DietarySpeciality
import com.scouts.kitchenplaner.model.entities.DietaryTypes
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.RecipeAlternative
import com.scouts.kitchenplaner.model.entities.RecipeForCooking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Usecase for displaying a recipe for cooking
 *
 * @param recipeRepository Repository class to retrieve information about recipes
 * @param allergenRepository Repository class to retrieve information about allergen persons
 */
class DisplayRecipeForCooking @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val allergenRepository: AllergenRepository
) {
    /**
     * Retrieve information about a recipe for cooking
     *
     * @param project The project in which the recipe is cooked
     * @param mealSlot The meal slot for which the recipe is cooked
     * @param recipeID The ID of the recipe being cooked
     *
     * @return A flow containing information on how to cook the recipe
     */
    fun showRecipeForCooking(
        project: Project,
        mealSlot: MealSlot,
        recipeID: Long
    ): Flow<RecipeForCooking> {
        val allergenPersonsFlow = allergenRepository.getAllergenPersonsByProjectID(project.id)
        val allergensFlow = recipeRepository.getAllergensForRecipe(recipeID)
        val alternativeRecipes =
            project.mealPlan[mealSlot].first?.second
                ?.filter { it.id != recipeID }
                ?.map { recipeRepository.getRecipeById(it.id) }
                ?: listOf()

        val combinedAlternatives =
            if (alternativeRecipes.isNotEmpty()) {
                combine(alternativeRecipes) { it.toList() }
            } else {
                flowOf(listOf())
            }

        // Calculate the dietary specialities each alternative recipe actually covers
        val allergensForAlternatives = combine(
            combinedAlternatives,
            allergensFlow,
            allergenPersonsFlow
        ) { alternatives, allergens, allergenPersons ->
            val allergenMap = mutableMapOf<Long, List<DietarySpeciality>>()
            alternatives.forEach {
                // A trace allergen is relevant iff the recipe that is currently being cooked con-
                // tains it in large amounts
                val relevantTraces = it.traces
                    .filter { trace ->
                        allergens.any { speciality ->
                            speciality.allergen == trace && speciality.type == DietaryTypes.ALLERGEN
                        }
                    }
                    .map { name -> Pair(name, DietaryTypes.TRACE) }

                // A free allergen is relevant iff the recipe that is currently being cooked con-
                // tains it at all
                val relevantFrees = it.freeOfAllergen
                    .filter { free ->
                        allergens.any { speciality ->
                            speciality.allergen == free && speciality.type != DietaryTypes.FREE_OF
                        }
                    }
                    .map { name -> Pair(name, DietaryTypes.FREE_OF) }

                // An allergen is relevant iff it is either a relevant trace allergen or a relevant
                // free allergen and some person is allergic to it
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