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
import com.scouts.kitchenplaner.datalayer.repositories.RecipeRepository
import com.scouts.kitchenplaner.model.entities.Allergen
import com.scouts.kitchenplaner.model.entities.AllergenMealCover
import com.scouts.kitchenplaner.model.entities.AllergenPerson
import com.scouts.kitchenplaner.model.entities.DietarySpeciality
import com.scouts.kitchenplaner.model.entities.DietaryTypes
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.RecipeStub
import com.scouts.kitchenplaner.model.usecases.CheckAllergens
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.util.Date

class AllergenCheckTest {
    private val recipeRepository = mockk<RecipeRepository>(relaxed = true)
    private val project = mockk<Project>(relaxed = true)
    private val uri = mockk<Uri>(relaxed = true)
    private val checkAllergens = CheckAllergens(recipeRepository)
    private val mealSlots = listOf(
        MealSlot(Date(0), "Frühstück"),
        MealSlot(Date(0), "Mittagessen"),
        MealSlot(Date(24*60*60*1000), "Frühstück")
    )
    private val allergenPersons = listOf(
        AllergenPerson(
            "Alice",
            listOf(Allergen("Sellerie", false)),
            Date(0), "Frühstück",
            Date(48*60*60*1000), "Mittagessen"
        ),
        AllergenPerson(
            "Bob",
            listOf(Allergen("Gluten", true)),
            Date(0), "Mittagessen",
            Date(48*60*60*1000), "Mittagessen"
        )
    )

    private val breakfastStub = RecipeStub(1, "Brot", uri)

    private val breakfastDietaries = listOf(DietarySpeciality("Gluten", DietaryTypes.ALLERGEN))

    private val lunchStub = RecipeStub(
        2,
        "Kartoffelsuppe",
        uri
    )

    private val lunchDietaries = listOf(
        DietarySpeciality("Sellerie", DietaryTypes.TRACE),
        DietarySpeciality("Gluten", DietaryTypes.FREE_OF)
    )

    /**
     * Creates an allergen check for the above meals and allergic persons:
     *      - Alice can't eat celery, but is fine with traces
     *      - Bob can't tolerate even traces of gluten, but arrives only for lunch
     *      - Breakfasts contain gluten, but the recipe is lacking information about celery
     *      - Lunch contains no gluten, but contains traces of celery
     */
    @Test
    fun checkAllergenCheckCreation() : Unit = runTest {
        coEvery { project.meals } returns listOf("Frühstück", "Mittagessen")
        coEvery { project.mealSlots } returns mealSlots
        coEvery { project.allergenPersons } returns allergenPersons
        coEvery { project.mealPlan[mealSlots[0]] } returns Pair(Pair(breakfastStub, listOf()), 5)
        coEvery { project.mealPlan[mealSlots[1]] } returns Pair(Pair(lunchStub, listOf()), 5)
        coEvery { project.mealPlan[mealSlots[2]] } returns Pair(Pair(breakfastStub, listOf()), 5)
        coEvery { recipeRepository.getAllergensForRecipe(1) } returns flowOf(breakfastDietaries)
        coEvery { recipeRepository.getAllergensForRecipe(2) } returns flowOf(lunchDietaries)

        val projectFlow = flowOf(project)

        val results = mealSlots.associateWith {
            checkAllergens.getAllergenCheck(projectFlow, it)
        }

        //projectFlow.emit(project)

        results[mealSlots[0]]?.test { // first breakfast
            val check = awaitItem()
            Assertions.assertEquals(0, check.coveredPersons.size,
                "Did not find the correct number of covered persons for the first breakfast")
            Assertions.assertEquals(0, check.notCoveredPersons.size,
                "Did not find the correct number of not covered persons for the first breakfast")
            Assertions.assertEquals(1, check.unknownPersons.size,
                "Did not find the correct number of unknown persons for the first breakfast")
            Assertions.assertTrue(check.unknownPersons.any { it.name == "Alice" },
                "Did not find Alice as not covered person for the first breakfast")
            Assertions.assertEquals(check.mealCover, AllergenMealCover.UNKNOWN,
                "Did not find the correct meal cover for the first breakfast")
            awaitComplete()
        } ?: fail("MealSlot was not found in check")

        results[mealSlots[1]]?.test { // lunch
            val check = awaitItem()
            Assertions.assertEquals(2, check.coveredPersons.size,
                "Did not find the correct number of covered persons for the lunch")
            Assertions.assertEquals(0, check.notCoveredPersons.size,
                "Did not find the correct number of not covered persons for lunch")
            Assertions.assertEquals(0, check.unknownPersons.size,
                "Did not find the correct number of unknown persons for lunch")
            Assertions.assertTrue(check.coveredPersons.any { it.name == "Alice" },
                "Did not find Alice as covered person for the lunch")
            Assertions.assertTrue(check.coveredPersons.any { it.name == "Bob" },
                "Did not find Bob as covered person for the lunch")
            Assertions.assertEquals(check.mealCover, AllergenMealCover.COVERED,
                "Did not find the correct meal cover for the lunch")
            awaitComplete()
        } ?: fail("MealSlot was not found in check")

        results[mealSlots[2]]?.test { // second breakfast
            val check = awaitItem()
            Assertions.assertEquals(0, check.coveredPersons.size,
                "Did not find the correct number of covered persons for the second breakfast")
            Assertions.assertEquals(1, check.notCoveredPersons.size,
                "Did not find the correct number of not covered persons for the second breakfast")
            Assertions.assertEquals(1, check.unknownPersons.size,
                "Did not find the correct number of unknown persons for the second breakfast")
            Assertions.assertTrue(check.notCoveredPersons.any { it.name == "Bob" },
                "Did not find Bob as not covered person for the second breakfast")
            Assertions.assertTrue(check.unknownPersons.any { it.name == "Alice" },
                "Did not find Alice as unknown person for the second breakfast")
            Assertions.assertEquals(check.mealCover, AllergenMealCover.NOT_COVERED,
                "Did not find the correct meal cover for the second breakfast")
            awaitComplete()
        } ?: fail("MealSlot was not found in check")
    }
}