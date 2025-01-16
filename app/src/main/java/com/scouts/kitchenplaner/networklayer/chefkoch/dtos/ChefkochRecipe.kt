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

package com.scouts.kitchenplaner.networklayer.chefkoch.dtos

/**
 * A recipe as used in requests to chefkoch's API
 *
 * @param title The name of the recipe
 * @param subtitle Further description of the recipe
 * @param previewImageUrlTemplate A string describing where the preview image of this recipe can be
 *                                found. Contains placeholders where the resolution needs to be
 *                                specified
 * @param instructions The instructions for cooking this recipe
 * @param ingredientGroups The ingredient groups of this recipe
 * @param servings For how many people the recipe is calculated for
 */
data class ChefkochRecipe(
    val title: String,
    val subtitle: String,
    val previewImageUrlTemplate: String,
    val instructions: String,
    val ingredientGroups: List<ChefkochIngredientGroup>,
    val servings: Int
)
