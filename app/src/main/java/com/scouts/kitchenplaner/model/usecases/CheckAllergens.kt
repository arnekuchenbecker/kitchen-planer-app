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
import com.scouts.kitchenplaner.datalayer.repositories.RecipeManagementRepository
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
import javax.inject.Inject

class CheckAllergens @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val recipeManagementRepository: RecipeManagementRepository,
    private val allergenRepository: AllergenRepository
) {
    @OptIn(DomainLayerRestricted::class, ExperimentalCoroutinesApi::class)
    fun getAllergenCheck(project: Project, mealSlot: MealSlot): Flow<AllergenCheck> {
        val recipesFlow = recipeManagementRepository.getRecipesForMealSlot(project.id, mealSlot)
            .flatMapLatest { ids ->
                val filteredIds = ids.filter { it != 0L }
                if (filteredIds.isNotEmpty()) {
                    combine(
                        filteredIds.map {
                            recipeRepository.getRecipeStubById(it)
                        }
                    ) {
                        stubs -> stubs.toList()
                    }
                } else {
                    flowOf(listOf())
                }
            }
        val personsFlow = allergenRepository.getAllergenPersonsByProjectID(project.id)

        return personsFlow
            .map { persons ->
                persons.filter { person ->
                    person.arrivalMealSlot.before(mealSlot, project.meals)
                            && mealSlot.before(person.departureMealSlot, project.meals)
                }
            }
            .combine(recipesFlow) { persons, recipes ->
                Pair(persons, recipes)
            }
            .flatMapLatest { (persons, recipes) ->
                val recipeCoverFlows = persons.map { person ->
                    val coverFlows = recipes.map { recipe ->
                        checkRecipe(recipe, person)
                    }
                    if (coverFlows.isEmpty()) {
                        return@flatMapLatest flowOf(AllergenCheck())
                    }
                    val covers = combine(coverFlows) { covers ->
                        covers.fold(AllergenMealCover.NOT_COVERED) { acc, newValue ->
                            if (newValue == AllergenMealCover.COVERED) {
                                AllergenMealCover.COVERED
                            } else if (newValue == AllergenMealCover.UNKNOWN
                                && acc == AllergenMealCover.NOT_COVERED
                            ) {
                                AllergenMealCover.UNKNOWN
                            } else {
                                acc
                            }
                        }
                    }
                    covers.map { Pair(person, it) }
                }
                combine(recipeCoverFlows) { covers ->
                    AllergenCheck().apply {
                        covers.forEach { (person, cover) ->
                            this.addAllergenPerson(cover, person)
                        }
                    }
                }
            }
    }

    private fun checkRecipe(stub: RecipeStub, person: AllergenPerson): Flow<AllergenMealCover> {
        val dietarySpecialities = recipeRepository.getAllergensForRecipe(
            stub.id ?: return flowOf(
                AllergenMealCover.UNKNOWN
            )
        )

        return dietarySpecialities.map { specialities ->
            return@map person.allergens.fold(AllergenMealCover.COVERED) { acc, newValue ->
                return@fold if (acc == AllergenMealCover.COVERED && specialities.none { it.allergen == newValue.allergen }) {
                    AllergenMealCover.UNKNOWN
                } else if (acc == AllergenMealCover.NOT_COVERED) {
                    AllergenMealCover.NOT_COVERED
                } else {
                    val filteredSpecialities =
                        specialities.filter { it.allergen == newValue.allergen }
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
