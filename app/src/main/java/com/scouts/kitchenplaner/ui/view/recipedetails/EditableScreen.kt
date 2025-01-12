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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.scouts.kitchenplaner.model.entities.IngredientGroup
import com.scouts.kitchenplaner.ui.state.EditRecipeState
import com.scouts.kitchenplaner.ui.view.NumberPicker
import com.scouts.kitchenplaner.ui.view.PicturePicker

/**
 * Part of the strategy pattern.
 *
 * This class provides methods for a editing a recipe.
 * @param state State to save the current changes made to the recipe
 * @param setRecipePicture Callback function to change the name of the recipe
 * @param updateAmountOfPeople Callback function to change the number for how many servings the recipe is designed for
 * @param setRecipePicture Callback function to update the current recipe picture
 * @param setRecipeDescription Callback function to update the description of the recipe
 */
class EditableScreen(
    val state: EditRecipeState,
    val setRecipeName: (String) -> Unit,
    val updateAmountOfPeople: (Int) -> Unit,
    val setRecipePicture: (Uri) -> Unit,
    val setRecipeDescription: (String) -> Unit
) : EditRecipeStrategy {
    @Composable
    override fun DisplayHeaderField() {
        TextField(value = state.name, onValueChange = { setRecipeName(it) }, singleLine = true)
    }

    @Composable
    override fun DisplayHeaderImage() {
        Icon(
            imageVector = Icons.Filled.Close, contentDescription = "dismiss changes"
        )
    }

    @Composable
    override fun NumberOfPersons(modifier: Modifier) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Für ")
            NumberPicker(
                modifier = Modifier.fillMaxWidth(),
                value = if (state.amount == 0) {
                    ""
                } else {
                    state.amount.toString()
                },

                onValueChange = {
                    if (it.isEmpty()) {
                        updateAmountOfPeople(0)
                    } else {
                        updateAmountOfPeople(it.toInt())
                    }
                },
                label = { Text("") },
            )
            if (state.amount > 1) {
                Text(" Personen")

            } else {
                Text(" Person")
            }
        }
    }

    @Composable
    override fun RecipePicture() {
        PicturePicker(onPathSelected = { uri ->
            if (uri != null) {
                setRecipePicture(uri)
            }
        }, path = state.imageURI)
    }

    @Composable
    override fun Description(modifier: Modifier) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.description,
            onValueChange = { setRecipeDescription(it) },
        )
    }

    override fun getFreeOfList(): List<String> {
        return state.freeOf
    }

    override fun getTracesList(): List<String> {
        return state.traces
    }

    override fun getAllergenList(): List<String> {
        return state.allergens
    }

    override fun getInstructionSteps(): List<String> {
        return state.instructions
    }

    override fun getIngredientGroups(): List<IngredientGroup> {
        return state.ingredients
    }
}