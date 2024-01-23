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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class CheckAllergens (
    private val recipeRepository: RecipeRepository
) {
    @OptIn(DomainLayerRestricted::class, ExperimentalCoroutinesApi::class)
    fun getAllergenCheck(projectFlow: Flow<Project>, mealSlot: MealSlot) : Flow<AllergenCheck> {
        return projectFlow.flatMapLatest { project ->
            val recipePair = project.mealPlan[mealSlot].first
                ?: return@flatMapLatest flowOf(listOf())
            val recipes = listOf(listOf(recipePair.first), recipePair.second).flatten()

            val allergens = project.allergenPersons.filter { person ->
                MealSlot(person.arrivalDate, person.arrivalMeal).before(mealSlot, project.meals) &&
                        mealSlot.before(MealSlot(person.departureDate, person.departureMeal), project.meals)
            }.map { person ->
                recipes.map { recipe ->
                    val allergenFlow: Flow<List<Triple<RecipeStub, AllergenPerson, AllergenMealCover>>> = flowOf(listOf())
                    val recipeCover = checkRecipe(recipe, person)
                    allergenFlow.combine(recipeCover) { prev, cover ->
                        val next = prev + Triple(recipe, person, cover)
                        next
                    }
                }
            }.flatten()

            combine(allergens) {
                it.toList().flatten()
            }
        }.map {
            val check = AllergenCheck()
            val personCovers = it.groupBy { cover -> cover.second }.map { (person, covers) ->
                val cover = covers.map { cover ->
                    cover.third
                }.fold(AllergenMealCover.NOT_COVERED) { acc, newValue ->
                    if (acc == AllergenMealCover.NOT_COVERED) {
                        newValue
                    } else if (newValue == AllergenMealCover.COVERED) {
                        newValue
                    } else {
                        acc
                    }
                }
                Pair(person, cover)
            }
            personCovers.forEach { (person, cover) ->
                check.addAllergenPerson(cover, person)
            }
            check
        }
    }

    private fun checkRecipe(stub: RecipeStub, person: AllergenPerson) : Flow<AllergenMealCover> {
        val dietarySpecialities = recipeRepository.getAllergensForRecipe(stub.id ?: return flowOf(
            AllergenMealCover.UNKNOWN
        ))

        return dietarySpecialities.map { specialities ->
            return@map person.allergens.fold(AllergenMealCover.COVERED) { acc, newValue ->
                return@fold if (acc == AllergenMealCover.COVERED && specialities.none { it.allergen == newValue.allergen }) {
                    AllergenMealCover.UNKNOWN
                } else if (acc == AllergenMealCover.NOT_COVERED) {
                    AllergenMealCover.NOT_COVERED
                } else {
                    val filteredSpecialities = specialities.filter { it.allergen == newValue.allergen }
                    if (newValue.traces && filteredSpecialities.isNotEmpty() && filteredSpecialities.any { it.type != DietaryTypes.FREE_OF }) {
                        AllergenMealCover.NOT_COVERED
                    } else if (filteredSpecialities.isNotEmpty() && filteredSpecialities.any { it.type == DietaryTypes.ALLERGEN }) {
                        AllergenMealCover.NOT_COVERED
                    } else {
                        acc
                    }
                }
            }
        }
    }
}
