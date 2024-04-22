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

package com.scouts.kitchenplaner.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.toMutableStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scouts.kitchenplaner.model.entities.Ingredient
import com.scouts.kitchenplaner.model.entities.IngredientGroup
import com.scouts.kitchenplaner.model.entities.Recipe
import com.scouts.kitchenplaner.model.usecases.CreateRecipe
import com.scouts.kitchenplaner.model.usecases.ImportRecipe
import com.scouts.kitchenplaner.ui.state.RecipeAllergenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View model that contains all information which should be stored when creating a new recipe and creates a new recipe.
 * It also handles the import from chefkoch recipes.
 * @param createRecipe use case to create a new recipe
 * @param importRecipe use case to import a chefkoch recipe
 */
@HiltViewModel
class CreateRecipeViewModel @Inject constructor(
    private val createRecipe: CreateRecipe, private val importRecipe: ImportRecipe
) : ViewModel() {
    private val idRegex = Regex("^[0-9]+$")
    var recipeName by mutableStateOf("")
    var uri by mutableStateOf<Uri?>(null)
    var description by mutableStateOf("")
    var calculatedFor by mutableStateOf("1")
    var allergenState by mutableStateOf(RecipeAllergenState())

    private var _instructions = mutableStateListOf<String>()
    val instructions: MutableList<String>
        get() = _instructions

    private var _ingredients = mutableStateMapOf<String, SnapshotStateList<Ingredient>>()
    val ingredients: Map<String, List<Ingredient>>
        get() = _ingredients

    val navigateFlow = MutableStateFlow<Long?>(null)

    /**
     * Creates a recipe and stores it. It also does some sanity checks.
     */
    fun createRecipe() {
        if (recipeName.isBlank() || ingredients.isEmpty() || ingredients.any { (_, ingredients) -> ingredients.isEmpty() }) {
            return
        }
        viewModelScope.launch {
            val recipe = Recipe(name = recipeName,
                imageURI = uri ?: Uri.EMPTY,
                description = description,
                numberOfPeople = calculatedFor.toIntOrNull() ?: 1,
                traces = allergenState.traces,
                allergens = allergenState.allergens,
                freeOfAllergen = allergenState.freeOf,
                instructions = instructions,
                ingredientGroups = ingredients.map { (name, ingredients) ->
                    IngredientGroup(name, ingredients)
                })
            val recipeId = createRecipe.createRecipe(recipe)
            navigateFlow.emit(recipeId)
        }
    }

    /**
     * Adds a new ingredient to a already existing ingredient group.
     * @param group The name of the ingredient group where the ingredient should added
     * @param ingredient The ingredient which should be added
     */
    fun addIngredient(group: String, ingredient: Ingredient) {
        if (ingredient.name.isNotBlank() && ingredient.amount != 0f && ingredient.unit.isNotBlank()) {
            _ingredients[group]?.add(ingredient)
        }
    }

    /**
     * Adds a new empty ingredient group
     * @param group the name of the ingredient group that should be created.
     */
    fun addIngredientGroup(group: String) {
        if (!_ingredients.containsKey(group)) {
            _ingredients[group] = mutableStateListOf()
        }
    }

    /**
     * deletes all occurrences of the ingredient in a ingredient group
     *
     * @param group the name of the ingredient group from where the ingredient should be deleted
     * @param ingredient the ingredient that should be deleted.
     */
    fun deleteIngredient(group: String, ingredient: Ingredient) {
        _ingredients[group]?.removeAll { it.name == ingredient.name }
    }

    /**
     * Deletes an ingredient group
     * @param group the name of the ingredient group that should be deleted
     */
    fun deleteGroup(group: String) {
        _ingredients.remove(group)
    }

    /**
     * Imports a chefkoch recipe from the source
     * @param source Source can either be the full chefkoch URL or the recipe ID only
     */
    fun importRecipe(source: String) {
        if (idRegex.matches(source)) {
            importRecipeFromID(source.toLong())
        } else {
            val urlParts = source.split("/")
            val id = urlParts[urlParts.size - 2]
            if (idRegex.matches(id)) {
                importRecipeFromID(id.toLong())
            } else {
                println("Could not parse source $source")
            }
        }
    }

    private fun importRecipeFromID(id: Long) {
        viewModelScope.launch {
            importRecipe.import(id = id, onFailure = { code, message ->
                println("Request failed with code $code: $message")
            }, onSuccess = { setValues(it) })
        }
    }

    private fun setValues(recipe: Recipe) {
        recipeName = recipe.name
        uri = recipe.imageURI
        description = recipe.description
        calculatedFor = recipe.numberOfPeople.toString()
        allergenState = RecipeAllergenState()
        _instructions = mutableStateListOf<String>().apply { addAll(recipe.instructions) }
        _ingredients = recipe.ingredientGroups.map { group ->
                Pair(group.name, group.ingredients.toMutableStateList())
            }.toMutableStateMap()
    }
}