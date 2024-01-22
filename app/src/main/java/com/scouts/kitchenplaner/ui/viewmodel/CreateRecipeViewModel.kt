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

package com.scouts.kitchenplaner.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scouts.kitchenplaner.model.entities.Ingredient
import com.scouts.kitchenplaner.model.entities.IngredientGroup
import com.scouts.kitchenplaner.model.entities.Recipe
import com.scouts.kitchenplaner.model.usecases.CreateRecipe
import com.scouts.kitchenplaner.ui.state.RecipeAllergenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateRecipeViewModel @Inject constructor(
    private val createRecipe: CreateRecipe
) : ViewModel() {
    var recipeName by mutableStateOf("")
    var uri by mutableStateOf<Uri?>(null)
    var description by mutableStateOf("")
    var calculatedFor by mutableStateOf(1)
    var allergenState by mutableStateOf(RecipeAllergenState())
    val instructions = mutableStateListOf<String>()
    private val _ingredients = mutableStateMapOf<String, SnapshotStateList<Ingredient>>()
    val ingredients: Map<String, List<Ingredient>>
        get() = _ingredients

    val navigateFlow = MutableStateFlow<Long?>(null)

    fun createRecipe() {
        if (recipeName.isBlank()
            || ingredients.isEmpty()
            || ingredients.all { (_, ingredients) -> ingredients.isEmpty() }) {
            return
        }
        viewModelScope.launch {
            val recipe = Recipe(
                name = recipeName,
                imageURI = uri ?: Uri.EMPTY,
                description = description,
                numberOfPeople = calculatedFor,
                traces = allergenState.traces,
                allergen = allergenState.allergens,
                freeOfAllergen = allergenState.freeOf,
                instructions = instructions,
                ingredientGroups = ingredients.map { (name, ingredients) ->
                    IngredientGroup(name, ingredients)
                }
            )
            val recipeId = createRecipe.createRecipe(recipe)
            navigateFlow.emit(recipeId)
        }
    }
}