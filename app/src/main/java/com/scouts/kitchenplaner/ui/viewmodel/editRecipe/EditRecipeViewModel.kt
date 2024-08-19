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

package com.scouts.kitchenplaner.ui.viewmodel.editRecipe

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
    var state by mutableStateOf(EditRecipeState())
    private var commandList: MutableList<ChangeCommand> = mutableListOf()


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

    fun activateEditMode(recipe: Recipe) {
        editMode = true
        state.init(recipe)
    }

    fun deactivateEditMode() {
        editMode = false
        state = EditRecipeState()
    }

    fun saveChangesAndDeactivateEditMode(recipe: Recipe) {
        updateRecipe(recipe)
        deactivateEditMode()
    }


    private fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            commandList.forEach { it.applyOnRecipe(editRecipe, recipe) }
        }
    }

    fun setRecipeName(name: String) {
        val nameCommand = UpdateNameCommand(name)
        commandList.add(nameCommand)
        nameCommand.applyOnState(state)
    }

    fun setRecipeDescription(description: String) {
        val command = UpdateDescriptionCommand(description)
        commandList.add(command)
        command.applyOnState(state)
    }

    fun setAmountOfPeople(amount: Int) {
        val command = UpdateAmountOfPeopleCommand(amount)
        commandList.add(command)
        command.applyOnState(state)
    }

    /**
     * Updates the recipe's picture
     *
     * @param uri The new URI for the new picture
     */
    fun setRecipePicture(uri: Uri) {
        val command = UpdateImageURICommand(uri)
        commandList.add(command)
        command.applyOnState(state)
    }


    /**
     * Adds a dietary speciality to the recipe
     *
     * @param speciality The speciality to add
     */
    fun addDietarySpeciality(speciality: DietarySpeciality) {
        val command = AddDietarySpecialityCommand(speciality)
        command.applyOnState(state)
        commandList.add(command)
    }

    /**
     * Deletes a speciality from the recipe
     *
     * @param speciality The speciality to delete
     */
    fun deleteDietarySpeciality(speciality: DietarySpeciality) {
        val command = DeleteSpecialityCommand(speciality)
        command.applyOnState(state)
        commandList.add(command)
    }

    /**
     * Adds an ingredient to the given ingredient group, or the whole ingredient group if no ingredient is provided
     *
     * @param recipe The recipe to change
     * @param group The ingredient group which should be added, or to which the ingredient is added to
     * @param ingredient The ingredient to be added ( or null if the ingredient group should be added)
     */
    fun addIngredient(recipe: Recipe, group: String, ingredient: Ingredient? = null) {
        val command = if (ingredient == null) {
            AddIngredientGroupCommand(group)
        } else {
            AddIngredientCommand(group, ingredient)
        }
        command.applyOnState(state)
        commandList.add(command)
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
        group: IngredientGroup, //TODO
        ingredient: Ingredient,
        newName: String? = null,
        newAmount: Float? = null,
        newUnit: String? = null
    ) {
        val command = EditIngredientCommand(
            group = group.name,
            ingredient,
            newName = newName,
            newAmount = newAmount?.toDouble(),
            newUnit = newUnit
        )
        command.applyOnState(state)
        commandList.add(command)
    }

    fun deleteIngredient(group: String, ingredient: Ingredient? = null) {
        val command = if (ingredient == null) {
            DeleteIngredientGroupCommand(group)
        } else {
            DeleteIngredientCommand(group, ingredient)
        }
        command.applyOnState(state)
        commandList.add(command)
    }


    /**
     * Adds an instruction step before the step with the given index
     *
     * @param step The step to be added
     * @param index The index of the step in front of which the step should be added
     */
    fun addInstructionStep(step: String, index: Int) {
        val command = AddInstructionStepCommand(index, step)
        command.applyOnState(state)
        commandList.add(command)
    }

    /**
     * Removes the instruction step on the given index.
     *
     * @param index Index of the step that should be removed
     */
    fun removeInstructionStep(index: Int) {
        val command = DeleteInstructionStepCommand(index)
        command.applyOnState(state)
        commandList.add(command)

    }

    /**
     * Updates the content of a instruction step
     *
     * @param index The index of the instruction step to change
     * @param instruction The new content of the instruction step
     */
    fun updateInstructionStep(index: Int, instruction: String) {
        val command = EditInstructionStepCommand(index, instruction)
        command.applyOnState(state)
        commandList.add(command)
    }
}