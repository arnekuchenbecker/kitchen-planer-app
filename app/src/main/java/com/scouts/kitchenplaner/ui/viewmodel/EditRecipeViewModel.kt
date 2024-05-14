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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scouts.kitchenplaner.model.entities.DietarySpeciality
import com.scouts.kitchenplaner.model.entities.Ingredient
import com.scouts.kitchenplaner.model.entities.IngredientGroup
import com.scouts.kitchenplaner.model.entities.Recipe
import com.scouts.kitchenplaner.model.usecases.EditRecipe
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

    /**
     * Gets the recipe from the data base and saves it, such that the [recipeFlow] can be used
     * @param recipeID id of the recipe
     */
    suspend fun getRecipe(recipeID: Long) {
        recipeFlow = editRecipe.getRecipe(recipeID).stateIn(viewModelScope)
    }

    /**
     * Updates a recipe's name
     *
     * @param recipe The recipe to change
     * @param name The new name
     */
    fun setRecipeName(recipe: Recipe, name: String) {
        viewModelScope.launch {
            editRecipe.setRecipeName(recipe, name)
        }
    }

    /**
     * Updates the recipe's description
     *
     * @param recipe The recipe to change
     * @param description The new description
     */
    fun setDescription(recipe: Recipe, description: String) {
        viewModelScope.launch {
            editRecipe.setRecipeDescription(recipe, description)
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
     * Updates the number of people the recipe is designed for
     *
     * @param recipe The recipe to change
     * @param numberOfPeople New number of people
     */
    fun setNumberOfPeople(recipe: Recipe, numberOfPeople: Int) {
        viewModelScope.launch {
            editRecipe.setNumberOfPeople(recipe, numberOfPeople)
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

        /**
         * Deletes an ingredient group with all its ingredients in it or,
         * if the ingredient is specified only the ingredient from the group.
         *
         * @param recipe The recipe to change
         * @param group The group to delete or from which the ingredient gets deleted
         * @param ingredient The ingredient to be deleted ( or null if the ingredient group should be deleted)
         */
        fun deleteIngredient(
            recipe: Recipe, group: IngredientGroup, ingredient: Ingredient? = null
        ) {
            viewModelScope.launch {
                if (ingredient == null) {
                    editRecipe.deleteIngredientGroup(recipe, group)
                } else {
                    editRecipe.deleteIngredient(recipe, group, ingredient)
                }
            }
        }

        /**
         * Updates an ingredient with the given values. If a value is not specified the old value stays.
         *
         * @param recipe The recipe containing the ingredient
         * @param group The ingredient group the ingredient belongs to
         * @param ingredient The ingredient to change
         * @param newName The new name for the ingredient (or null if the old one stays)
         * @param newAmount The new amount of the ingredient (or null if the old one stays)
         * @param newUnit The new unit if the ingredient (or null if the old one stays)
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
         * @param index The index of the step in front of the step should be added
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
                if (index >= 0) {
                    editRecipe.updateInstructionStep(recipe, index, instruction)
                }
            }
        }
    }
}