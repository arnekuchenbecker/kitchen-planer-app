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

package com.scouts.kitchenplaner.model

import android.net.Uri
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
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
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
     *      - Lunch contains no gluten,
     */
    @Test
    fun checkAllergenCheckCreation() : Unit = runTest {
        coEvery { project.meals } returns listOf("Frühstück", "Mittagessen")
        coEvery { project.mealSlots } returns mealSlots
        coEvery { project.allergenPersons } returns allergenPersons
        coEvery { project.mealPlan[mealSlots[0]] } returns Pair(Pair(breakfastStub, listOf()), 5)
        coEvery { project.mealPlan[mealSlots[1]] } returns Pair(Pair(lunchStub, listOf()), 5)
        coEvery { project.mealPlan[mealSlots[2]] } returns Pair(Pair(breakfastStub, listOf()), 5)
        coEvery { recipeRepository.getAllergensForRecipe(1) } returns breakfastDietaries
        coEvery { recipeRepository.getAllergensForRecipe(2) } returns lunchDietaries

        val result = checkAllergens.getAllergenCheck(project)
        Assertions.assertEquals(AllergenMealCover.UNKNOWN, result[mealSlots[0]])
        Assertions.assertEquals(AllergenMealCover.COVERED, result[mealSlots[1]])
        Assertions.assertEquals(AllergenMealCover.NOT_COVERED, result[mealSlots[2]])

        val coveredPersonsBreakfast1 = result[mealSlots[0], AllergenMealCover.COVERED]
        val unknownPersonsBreakfast1 = result[mealSlots[0], AllergenMealCover.UNKNOWN]
        val notCoveredPersonsBreakfast1 = result[mealSlots[0], AllergenMealCover.NOT_COVERED]
        Assertions.assertEquals(0, coveredPersonsBreakfast1.size)
        Assertions.assertEquals(1, unknownPersonsBreakfast1.size)
        Assertions.assertEquals("Alice", unknownPersonsBreakfast1[0].name)
        Assertions.assertEquals(0, notCoveredPersonsBreakfast1.size)

        val coveredPersonsLunch = result[mealSlots[1], AllergenMealCover.COVERED]
        val unknownPersonsLunch = result[mealSlots[1], AllergenMealCover.UNKNOWN]
        val notCoveredPersonsLunch = result[mealSlots[1], AllergenMealCover.NOT_COVERED]
        Assertions.assertEquals(2, coveredPersonsLunch.size)
        Assertions.assertTrue(coveredPersonsLunch.any { it.name == "Alice" })
        Assertions.assertTrue(coveredPersonsLunch.any { it.name == "Bob" })
        Assertions.assertEquals(0, unknownPersonsLunch.size)
        Assertions.assertEquals(0, notCoveredPersonsLunch.size)

        val coveredPersonsBreakfast2 = result[mealSlots[2], AllergenMealCover.COVERED]
        val unknownPersonsBreakfast2 = result[mealSlots[2], AllergenMealCover.UNKNOWN]
        val notCoveredPersonsBreakfast2 = result[mealSlots[2], AllergenMealCover.NOT_COVERED]
        Assertions.assertEquals(0, coveredPersonsBreakfast2.size)
        Assertions.assertEquals(1, unknownPersonsBreakfast2.size)
        Assertions.assertTrue(unknownPersonsBreakfast2.any {it.name == "Alice"})
        Assertions.assertEquals(1, notCoveredPersonsBreakfast2.size)
        Assertions.assertTrue(notCoveredPersonsBreakfast2.any {it.name == "Bob"})
    }
}