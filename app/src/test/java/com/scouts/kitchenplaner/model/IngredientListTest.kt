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
import com.scouts.kitchenplaner.repositories.RecipeRepository
import com.scouts.kitchenplaner.model.entities.Ingredient
import com.scouts.kitchenplaner.model.entities.IngredientGroup
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.Recipe
import com.scouts.kitchenplaner.model.entities.RecipeStub
import com.scouts.kitchenplaner.model.usecases.DisplayIngredientList
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.Date

class IngredientListTest {
    private val project = mockk<Project>(relaxed = true)

    private val uri = mockk<Uri>(relaxed = true)

    private val breakfastStub = RecipeStub(1, "Frühstück", uri)
    private val breakfast = mockk<Recipe>(relaxed = true)
    private val breakfastIngredients = IngredientGroup(
        "Frühstück",
        listOf(
            Ingredient("Brot", 3.0, "kg"),
            Ingredient("Nutella", 0.75, "kg"),
            Ingredient("Nougat Bits", 600.0, "g")
        )
    )

    private val lunchStub = RecipeStub(2, "Mittagessen", uri)
    private val lunch = mockk<Recipe>(relaxed = true)
    private val lunchIngredients1 = IngredientGroup(
        "Mittagessen1",
        listOf(
            Ingredient("Nudeln", 1.0, "kg"),
            Ingredient("Pesto", 0.5, "kg"),
            Ingredient("Tomatensauce", 450.0, "g"),
            Ingredient("Paprika", 2.0, "Stk")
        )
    )
    private val lunchIngredients2 = IngredientGroup(
        "Mittagessen2",
        listOf(
            Ingredient("Nudeln", 1000.0, "g")
        )
    )

    private val dinnerStub = RecipeStub(3, "Abendessen", uri)
    private val dinner = mockk<Recipe>(relaxed = true)
    private val dinnerIngredients = IngredientGroup(
        "Abendessen",
        listOf(
            Ingredient("Spätzle", 2.0, "kg"),
            Ingredient("Käse", 1.0, "kg"),
            Ingredient("Zwiebeln", 600.0, "g")
        )
    )

    private val mealSlots = listOf(
        MealSlot(Date(0), "Frühstück"),
        MealSlot(Date(0), "Mittagessen"),
        MealSlot(Date(0), "Abendessen")
    )

    private val recipeRepository = mockk<RecipeRepository>(relaxed = true)

    @Test
    fun testIngredientListCreationMainRecipe() = runTest {
        coEvery { project.mealSlots } returns mealSlots
        coEvery { project.mealPlan[mealSlots[0]] } returns Pair(Pair(breakfastStub, listOf()), 5)
        coEvery { project.mealPlan[mealSlots[1]] } returns Pair(Pair(lunchStub, listOf()), 60)
        coEvery { project.mealPlan[mealSlots[2]] } returns Pair(Pair(dinnerStub, listOf()), 60)

        coEvery { recipeRepository.getRecipeById(1) } returns flowOf(breakfast)
        coEvery { recipeRepository.getRecipeById(2) } returns flowOf(lunch)
        coEvery { recipeRepository.getRecipeById(3) } returns flowOf(dinner)

        coEvery { breakfast.numberOfPeople } returns 10
        coEvery { breakfast.ingredientGroups } returns listOf(breakfastIngredients)

        coEvery { lunch.numberOfPeople } returns 10
        coEvery { lunch.ingredientGroups } returns listOf(lunchIngredients1, lunchIngredients2)

        coEvery { dinner.numberOfPeople } returns 10
        coEvery { dinner.ingredientGroups } returns listOf(dinnerIngredients)

        val displayIngredientList = DisplayIngredientList(recipeRepository)
        val result = displayIngredientList.getIngredientList(project).toList(mealSlots.map { it.meal })

        Assertions.assertEquals(3, result.size)
        Assertions.assertEquals(3, result[0].second.size)
        Assertions.assertTrue(result[0].second.any { it.name == "Brot" && it.amount == 1.5&& it.unit == "kg" }, "Brot")
        Assertions.assertTrue(result[0].second.any { it.name == "Nutella" && it.amount == 0.75 / 2 && it.unit == "kg" }, "Nutella")
        Assertions.assertTrue(result[0].second.any { it.name == "Nougat Bits" && it.amount == 300.0 && it.unit == "g" }, "Nougat Bits")
        Assertions.assertEquals(5, result[1].second.size)
        Assertions.assertTrue(result[1].second.any { it.name == "Nudeln" && it.amount == 6.0 && it.unit == "kg" }, "Nudeln, kg")
        Assertions.assertTrue(result[1].second.any { it.name == "Nudeln" && it.amount == 6000.0 && it.unit == "g" }, "Nudeln, g")
        Assertions.assertTrue(result[1].second.any { it.name == "Pesto" && it.amount == 6 * 0.5 && it.unit == "kg" }, "Pesto")
        Assertions.assertTrue(result[1].second.any { it.name == "Tomatensauce" && it.amount == 6 * 450.0 && it.unit == "g" }, "Tomatensauce")
        Assertions.assertTrue(result[1].second.any { it.name == "Paprika" && it.amount == 6 * 2.0 && it.unit == "Stk" }, "Paprika")
        Assertions.assertEquals(3, result[2].second.size)
        Assertions.assertTrue(result[2].second.any { it.name == "Spätzle" && it.amount == 12.0 && it.unit == "kg" }, "Spätzle")
        Assertions.assertTrue(result[2].second.any { it.name == "Käse" && it.amount == 6.0 && it.unit == "kg" }, "Käse")
        Assertions.assertTrue(result[2].second.any { it.name == "Zwiebeln" && it.amount == 600.0 * 6 && it.unit == "g" }, "Zwiebeln")
    }
}