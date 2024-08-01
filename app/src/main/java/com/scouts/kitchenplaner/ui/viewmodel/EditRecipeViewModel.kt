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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scouts.kitchenplaner.model.entities.DietarySpeciality
import com.scouts.kitchenplaner.model.entities.Ingredient
import com.scouts.kitchenplaner.model.entities.IngredientGroup
import com.scouts.kitchenplaner.model.entities.Recipe
import com.scouts.kitchenplaner.model.usecases.EditRecipe
import com.scouts.kitchenplaner.ui.state.EditRecipeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View model for displaying and editing a recipe
 */
@HiltViewModel
class EditRecipeViewModel @Inject constructor(private val editRecipe: EditRecipe) : ViewModel() {
    lateinit var recipeFlow: StateFlow<Recipe>
    private var editMode by mutableStateOf(false)
    var expandedAllergen by mutableStateOf(false)
    var expandedFreeOf by mutableStateOf(false)
    var expandedTraces by mutableStateOf(false)
    var changeState by mutableStateOf(EditRecipeState())


    /**
     * Gets the recipe from the data base and saves it, such that the [recipeFlow] can be used
     * @param recipeID id of the recipe
     */
    suspend fun getRecipe(recipeID: Long) {
        recipeFlow = editRecipe.getRecipe(recipeID).stateIn(viewModelScope)
    }

    fun isEditable(): Boolean {
        return editMode;
    }

    fun toggleEditMode(recipe: Recipe) {
        if (!editMode) {
            changeState.initState(recipe = recipe);
        } else {
            updateRecipe(recipe)
        }
        editMode = !editMode
    }

    private fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            editRecipe.setRecipeName(recipe, changeState.name)
            editRecipe.setRecipeDescription(recipe, changeState.description)
            editRecipe.setNumberOfPeople(recipe, changeState.amount)


            changeState.getAddedIngredients().forEach { (group, ingredients) ->
                editRecipe.addIngredientGroup(
                    recipe,
                    IngredientGroup(group, ingredients)
                )
            }

            val deleted = changeState.getDeletedIngredientsAndGroups()

            deleted.first.forEach { group ->
                editRecipe.deleteIngredientGroup(recipe, IngredientGroup(group))
            }
            deleted.second.forEach { (group, ingredientList) ->
                ingredientList.forEach { ingredient ->
                    editRecipe.deleteIngredient(
                        recipe,
                        IngredientGroup(group), ingredient
                    )
                }

            }

            changeState.instructionChanges.forEach { (index, step, added) ->
                if (added) {
                    editRecipe.addInstructionStep(recipe, step, index)
                } else {
                    editRecipe.deleteInstructionStep(recipe, index)
                }
            }
            changeState = EditRecipeState()
        }
    }

    /**
     * Updates the recipe's picture
     *
     * @param recipe The recipe to change
     * @param uri The new URI for the new picture
     */
    fun setRecipePicture(recipe: Recipe, uri: Uri) {
        viewModelScope.launch {
            editRecipe.setRecipePicture(recipe, uri)
        }
    }


    /**
     * Adds a dietary speciality to the recipe
     *
     * @param recipe The recipe to change
     * @param speciality The speciality to add
     */
    fun addDietarySpeciality(recipe: Recipe, speciality: DietarySpeciality) {
        viewModelScope.launch {
            editRecipe.addDietarySpeciality(recipe, speciality)
        }
    }

    /**
     * Deletes a speciality from the recipe
     *
     * @param recipe The recipe to change
     * @param speciality The speciality to delete
     */
    fun deleteDietarySpeciality(recipe: Recipe, speciality: DietarySpeciality) {
        viewModelScope.launch { editRecipe.deleteDietarySpeciality(recipe, speciality) }
    }

    /**
     * Adds an ingredient to the given ingredient group, or the whole ingredient group if no ingredient is provided
     *
     * @param recipe The recipe to change
     * @param group The ingredient which should be added, or to which the ingredient is added to
     * @param ingredient The ingredient to be added ( or null if the ingredient group should be added)
     */
    fun addIngredient(recipe: Recipe, group: IngredientGroup, ingredient: Ingredient? = null) {
        viewModelScope.launch {
            if (ingredient == null) {
                editRecipe.addIngredientGroup(recipe, group)
            } else {
                editRecipe.addIngredient(recipe, group, ingredient)
            }
        }
    }

    /**
     * Updates an ingredient with the given values. If a value is not specified the value will not be changed.
     *
     * @param recipe The recipe containing the ingredient
     * @param group The ingredient group the ingredient belongs to
     * @param ingredient The ingredient to change
     * @param newName The new name for the ingredient (or null if the name should not be changed)
     * @param newAmount The new amount of the ingredient (or null if the name should not be changed)
     * @param newUnit The new unit if the ingredient (or null if the name should not be changed)
     */
    fun editIngredient(
        recipe: Recipe,
        group: IngredientGroup,
        ingredient: Ingredient,
        newName: String? = null,
        newAmount: Float? = null,
        newUnit: String? = null
    ) {
        viewModelScope.launch {
            editRecipe.editIngredient(
                recipe, group, ingredient, newName, newAmount, newUnit
            )
        }
    }

    /**
     * Adds an instruction step before the step with the given index
     *
     * @param recipe The recipe to which to add the instruction step
     * @param step The step to be added
     * @param index The index of the step in front of which the step should be added
     */
    fun addInstructionStep(recipe: Recipe, step: String, index: Int) {
        viewModelScope.launch {
            editRecipe.addInstructionStep(recipe, step, index)
        }
    }

    /**
     * Removes the instruction step on the given index.
     *
     * @param recipe The recipe where the step should be removed
     * @param index Index of the step that should be removed
     */
    fun removeInstructionStep(recipe: Recipe, index: Int) {
        viewModelScope.launch {
            editRecipe.deleteInstructionStep(recipe, index)
        }
    }

    /**
     * Updates the content of a instruction step
     *
     * @param recipe The recipe to which the step belongs to
     * @param index The index of the instruction step to change
     * @param instruction The new content of the instruction step
     */
    fun updateInstructionStep(recipe: Recipe, index: Int, instruction: String) {
        viewModelScope.launch {
            editRecipe.updateInstructionStep(recipe, index, instruction)
        }
    }
}