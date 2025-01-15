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

package com.scouts.kitchenplaner.model

import android.net.Uri
import app.cash.turbine.test
import com.scouts.kitchenplaner.repositories.AllergenRepository
import com.scouts.kitchenplaner.repositories.RecipeRepository
import com.scouts.kitchenplaner.model.entities.Allergen
import com.scouts.kitchenplaner.model.entities.AllergenPerson
import com.scouts.kitchenplaner.model.entities.DietarySpeciality
import com.scouts.kitchenplaner.model.entities.DietaryTypes
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.Recipe
import com.scouts.kitchenplaner.model.entities.RecipeStub
import com.scouts.kitchenplaner.model.usecases.DisplayRecipeForCooking
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.Date

class RecipeForCookingTest {
    private val recipeRepository = mockk<RecipeRepository>(relaxed = true)
    private val allergenRepository = mockk<AllergenRepository>(relaxed = true)
    private val project = mockk<Project>(relaxed = true)
    private val mainRecipe = mockk<Recipe>(relaxed = true)
    private val alternativeRecipe1 = mockk<Recipe>(relaxed = true)
    private val alternativeRecipe2 = mockk<Recipe>(relaxed = true)
    private val uri = mockk<Uri>(relaxed = true)

    @Test
    fun testRecipeForCookingAllergens(): Unit = runTest {
        val model = DisplayRecipeForCooking(
            recipeRepository,
            allergenRepository
        )

        val recipeID = 1L
        val recipeName = "Main"
        val mainRecipeStub = RecipeStub(recipeID, recipeName, uri)
        val alternative1ID = 2L
        val alternative2ID = 3L
        val projectID = 1L
        val numPeople = 100
        val mealSlot = MealSlot(Date(0), "Frühstück")
        val mainAllergens = listOf(
            DietarySpeciality("Gluten", DietaryTypes.TRACE),
            DietarySpeciality("Schlechte Laune", DietaryTypes.ALLERGEN),
            DietarySpeciality("Unit Tests", DietaryTypes.TRACE),
            DietarySpeciality("Spinat", DietaryTypes.FREE_OF)
        )
        val allergenPersons = listOf(
            AllergenPerson(
                "Arne",
                listOf(
                    Allergen("Unit Tests", true)
                )
            ),
            AllergenPerson(
                "Antonia",
                listOf(
                    Allergen("Schlechte Laune", false),
                    Allergen("Spinat", true)
                )
            )
        )
        val alternativeRecipeStubs = listOf(
            RecipeStub(alternative1ID, "Alternative1", uri),
            RecipeStub(alternative2ID, "Alternative2", uri)
        )
        val instructions = listOf("A", "B", "C")

        coEvery { allergenRepository.getAllergenPersonsByProjectID(projectID) } returns
                flowOf(allergenPersons)
        coEvery { recipeRepository.getAllergensForRecipe(recipeID) } returns
                flowOf(mainAllergens)
        coEvery { project.mealPlan[mealSlot] } returns
                Pair(Pair(mainRecipeStub, alternativeRecipeStubs), numPeople)
        coEvery { recipeRepository.getRecipeById(alternative1ID) } returns
                flowOf(alternativeRecipe1)
        coEvery { recipeRepository.getRecipeById(alternative2ID) } returns
                flowOf(alternativeRecipe2)
        coEvery { recipeRepository.getRecipeById(recipeID) } returns
                flowOf(mainRecipe)

        coEvery { alternativeRecipe1.traces } returns listOf("Schlechte Laune")
        coEvery { alternativeRecipe1.freeOfAllergen } returns listOf("Spinat")
        coEvery { alternativeRecipe2.freeOfAllergen } returns listOf("Unit Tests")

        coEvery { mainRecipe.name } returns recipeName

        coEvery { mainRecipe.id } returns recipeID
        coEvery { alternativeRecipe1.id } returns alternative1ID
        coEvery { alternativeRecipe2.id } returns alternative2ID

        coEvery { project.id } returns projectID

        coEvery { mainRecipe.instructions } returns instructions

        model.showRecipeForCooking(project, mealSlot, recipeID).test {
            val recipe = awaitItem()

            Assertions.assertEquals(
                recipeName, recipe.name, "Returned an incorrect recipe " +
                        "name (expected $recipeName)."
            )
            Assertions.assertEquals(
                numPeople, recipe.people, "Returned an incorrect " +
                        "number of persons (expected $numPeople)."
            )
            Assertions.assertEquals(
                instructions, recipe.instructions, "Returned incorrect" +
                        " instructions."
            )
            Assertions.assertEquals(2, recipe.alternatives.size)

            // Assert relevant allergens for recipe 1 (Spinach is not relevant as the main recipe
            // does not contain it)
            Assertions.assertTrue(
                recipe.alternatives.any {
                    it.coveredAllergens.size == 1
                            && it.coveredAllergens[0].allergen == "Schlechte Laune"
                            && it.id == alternative1ID
                }
            )

            // Assert relevant allergens for recipe 2
            Assertions.assertTrue(
                recipe.alternatives.any {
                    it.coveredAllergens.size == 1
                            && it.coveredAllergens[0].allergen == "Unit Tests"
                            && it.id == alternative2ID
                }
            )

            awaitComplete()
        }
    }
}