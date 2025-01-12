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

package com.scouts.kitchenplaner.ui.view.recipedetails

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.scouts.kitchenplaner.model.entities.IngredientGroup
import com.scouts.kitchenplaner.model.entities.Recipe

/**
 * Part of the strategy pattern.
 * This class provides methods for a showing a recipe, without the possibility to edit it.
 * @param recipe The recipe that should be shown
 */
class StaticScreen(val recipe: Recipe) : EditRecipeStrategy {


    @Composable
    override fun DisplayHeaderField() {
        Text(recipe.name)
    }

    @Composable
    override fun DisplayHeaderImage() {
        Icon(
            imageVector = Icons.Filled.Edit, contentDescription = "Edit recipe"
        )
    }

    @Composable

    override fun NumberOfPersons(modifier: Modifier) {
        Text(
            "Für " + recipe.numberOfPeople + if (recipe.numberOfPeople > 1) {
                " Personen"
            } else {
                " Person"
            }, modifier = modifier
        )
    }

    @Composable
    override fun RecipePicture() {
        if (recipe.imageURI != Uri.EMPTY) {
            AsyncImage(
                model = recipe.imageURI,
                contentDescription = "Recipe picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }

    @Composable
    override fun Description(modifier: Modifier) {
        Text(recipe.description, modifier = modifier)
    }

    override fun getFreeOfList(): List<String> {
        return recipe.freeOfAllergen
    }

    override fun getTracesList(): List<String> {
        return recipe.traces
    }

    override fun getAllergenList(): List<String> {
        return recipe.allergens
    }

    override fun getInstructionSteps(): List<String> {
        return recipe.instructions
    }

    override fun getIngredientGroups(): List<IngredientGroup> {
       return recipe.ingredientGroups
    }
}