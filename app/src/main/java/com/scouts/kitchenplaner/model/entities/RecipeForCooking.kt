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

package com.scouts.kitchenplaner.model.entities

/**
 * A recipe with amounts calculated for the correct number of people
 *
 * @param recipe The recipe this calculation is based on
 * @param people The number of people the amounts are calculated for
 * @param alternatives The recipes that are planned as an alternative for this recipe
 */
class RecipeForCooking(
    private val recipe: Recipe,
    val people: Int,
    val alternatives: List<RecipeAlternative>
) {
    /**
     * The name of the recipe
     */
    val name = recipe.name

    /**
     * The ingredient groups of the recipe. Ingredients in the groups are calculated to contain the
     * correct amounts to cook for [people] people
     */
    val ingredientGroups = recipe.ingredientGroups.map {
        IngredientGroup(
            it.name,
            it.ingredients.map { ingredient ->
                Ingredient(
                    ingredient.name,
                    (ingredient.amount * people / recipe.numberOfPeople.toDouble()).toFloat(),
                    ingredient.unit
                )
            }
        )
    }

    /**
     * The instructions for cooking this recipe
     */
    val instructions = recipe.instructions
}