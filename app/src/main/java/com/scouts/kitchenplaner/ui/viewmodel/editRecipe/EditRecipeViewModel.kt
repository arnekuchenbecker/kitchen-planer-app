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

    /**
     * Whether the allergen card is expanded
     */
    var expandedAllergen by mutableStateOf(false)

    /**
     * Whether the free of card is expanded
     */
    var expandedFreeOf by mutableStateOf(false)

    /**
     * Whether the traces card is expanded
     */
    var expandedTraces by mutableStateOf(false)

    /**
     * The state for the currently made changes on the recipe
     */
    var state by mutableStateOf(EditRecipeState())
    var factory: RecipeEditFactory? = null
    private var commandList: MutableList<ChangeCommand> = mutableListOf()


    /**
     * Gets the recipe from the data base and saves it, such that the [recipeFlow] can be used
     * @param recipeID id of the recipe
     */
    suspend fun getRecipe(recipeID: Long) {
        recipeFlow = editRecipe.getRecipe(recipeID).stateIn(viewModelScope)
    }

    /**
     * Provides whether the recipe is currently editable
     */
    fun isEditable(): Boolean {
        return editMode;
    }

    /**
     * Activates the edit mode and initializes the state
     *
     * @param recipe The recipe that is editable
     */
    fun activateEditMode(recipe: Recipe) {
        editMode = true
        state = EditRecipeState(recipe)
        factory = RecipeEditFactory(recipe = recipe, useCase = editRecipe, state = state)
    }

    /**
     * Deactivates the edit mode, without saving any changes
     */
    fun deactivateEditMode() {
        editMode = false
    }

    /**
     * Deactivates the edit mode and saves all changes
     */
    fun saveChangesAndDeactivateEditMode() {
        updateRecipe()
        deactivateEditMode()
    }


    private fun updateRecipe() {
        viewModelScope.launch {
            commandList.forEach { it.applyOnRecipe() }
            commandList.clear()
        }
    }

    /**
     * Part of a command pattern:
     * Sets the name of the recipe to the new name
     *
     * @param name The new name of the recipe
     */
    fun setRecipeName(name: String) {
        val nameCommand = factory?.createUpdateNameCommand(name)!!
        commandList.add(nameCommand)
        nameCommand.applyOnState()
    }

    /**
     * Part of a command pattern:
     * Sets a new description
     *
     * @param description The new content of the description
     */
    fun setRecipeDescription(description: String) {
        val command = factory?.createUpdateDescriptionCommand(description)!!
        commandList.add(command)
        command.applyOnState()
    }

    /**
     * Part of a command pattern:
     * Updates the amount of people for whom the recipe is calculated
     *
     * @param amount New amount of people
     */
    fun setAmountOfPeople(amount: Int) {
        val command = factory?.createUpdateAmountOfPeopleCommand(amount)!!
        commandList.add(command)
        command.applyOnState()
    }

    /**
     * Part of a command pattern:
     * Updates the recipe's picture
     *
     * @param uri The new URI for the new picture
     */
    fun setRecipePicture(uri: Uri) {
        val command = factory?.createUpdateImageURICommand(uri)!!
        commandList.add(command)
        command.applyOnState()
    }


    /**
     * Part of a command pattern:
     * Adds a dietary speciality to the recipe
     *
     * @param speciality The speciality to add
     */
    fun addDietarySpeciality(speciality: DietarySpeciality) {
        val command = factory?.createAddDietarySpecialityCommand(speciality)!!
        commandList.add(command)
        command.applyOnState()
    }

    /**
     * Part of a command pattern:
     * Deletes a speciality from the recipe
     *
     * @param speciality The speciality to delete
     */
    fun deleteDietarySpeciality(speciality: DietarySpeciality) {
        val command = factory?.createDeleteSpecialityCommand(speciality)!!
        command.applyOnState()
        commandList.add(command)
    }

    /**
     * Part of a command pattern:
     * Adds an ingredient to the given ingredient group, or the whole ingredient group if no ingredient is provided
     *
     * @param group The ingredient group which should be added, or to which the ingredient is added to
     * @param ingredient The ingredient to be added (or null if the ingredient group should be added)
     */

    fun addIngredient(group: String, ingredient: Ingredient? = null) {
        val command = if (ingredient == null) {
            factory?.createAddIngredientGroupCommand(group)!!
        } else {
            factory?.createAddIngredientCommand(group, ingredient)!!
        }
        command.applyOnState()
        commandList.add(command)
    }

    /**
     * Part of a command pattern:
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
        group: IngredientGroup,
        ingredient: Ingredient,
        newName: String? = null,
        newAmount: Double? = null,
        newUnit: String? = null
    ) {
        val command = factory?.createEditIngredientCommand(
            group = group.name,
            ingredient,
            newName = newName,
            newAmount = newAmount,
            newUnit = newUnit
        )!!
        commandList.add(command)
        command.applyOnState()
    }

    /**
     * Part of a command pattern:
     * Deletes the given ingredient from the recipe. If the ingredient is null the whole ingredient group gets deleted
     *
     * @param group The ingredient group the ingredient belongs to or which should be deleted
     * @param ingredient The ingredient to be delete (or null if the ingredient group should be deleted)
     */
    fun deleteIngredient(group: String, ingredient: Ingredient? = null) {
        val command = if (ingredient == null) {
            factory?.createDeleteIngredientGroupCommand(group)!!
        } else {
            factory?.createDeleteIngredientCommand(group, ingredient)!!
        }
        command.applyOnState()
        commandList.add(command)
    }


    /**
     * Part of a command pattern:
     * Adds an instruction step at the step with the given index
     *
     * @param step The step to be added
     * @param index The index of the step at which the step should be added
     */
    fun addInstructionStep(step: String, index: Int) {
        val command = factory?.createAddInstructionStepCommand(index = index, step = step)!!
        command.applyOnState()
        commandList.add(command)
    }

    /**
     * Part of a command pattern:
     * Removes the instruction step on the given index.
     *
     * @param index Index of the step that should be removed
     */
    fun removeInstructionStep(index: Int) {
        val command = factory?.createDeleteInstructionStepCommand(index)!!
        command.applyOnState()
        commandList.add(command)

    }

    /**
     * Part of a command pattern:
     * Updates the content of a instruction step
     *
     * @param index The index of the instruction step to change
     * @param instruction The new content of the instruction step
     */
    fun updateInstructionStep(index: Int, instruction: String) {
        val command = factory?.createEditInstructionStepCommand(index, instruction)!!
        command.applyOnState()
        commandList.add(command)
    }
}