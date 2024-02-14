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

import android.net.Uri
import com.scouts.kitchenplaner.model.entities.Ingredient
import com.scouts.kitchenplaner.model.entities.IngredientGroup
import com.scouts.kitchenplaner.model.entities.Recipe
import com.scouts.kitchenplaner.networklayer.ChefkochAPIService
import javax.inject.Inject

private const val FORMAT_STRING = "<format>"
private const val FORMAT_INPUT = "crop-224x148"

class ImportRecipe @Inject constructor(
    private val chefkochAPIService: ChefkochAPIService
) {
    suspend fun import(
        id: Long,
        onFailure: (Int, String) -> Unit,
        onSuccess: (Recipe) -> Unit
    ) {
        val response = chefkochAPIService.getRecipe(id)
        if (response.isSuccessful) {
            val chefkochRecipe = response.body()
            if (chefkochRecipe != null) {
                val uri = Uri.parse(chefkochRecipe.previewImageUrlTemplate.replace(FORMAT_STRING, FORMAT_INPUT))
                val recipeName = if (chefkochRecipe.subtitle.isNotBlank()) {
                    "${chefkochRecipe.title} - ${chefkochRecipe.subtitle}"
                } else {
                    chefkochRecipe.title
                }
                val recipe = Recipe(
                    name = recipeName,
                    imageURI = uri,
                    numberOfPeople = chefkochRecipe.servings,
                    instructions = chefkochRecipe.instructions.split("\n").filter { it.isNotBlank() },
                    ingredientGroups = chefkochRecipe.ingredientGroups.map { group ->
                        val ingredients = group.ingredients.map { Ingredient(it.name, it.amount, it.unit) }
                        IngredientGroup(group.header, ingredients)
                    }
                )
                onSuccess(recipe)
            } else {
                onFailure(response.code(), "Response body was empty")
            }
        } else {
            onFailure(response.code(), "HTTP request was not succesful")
        }
    }
}