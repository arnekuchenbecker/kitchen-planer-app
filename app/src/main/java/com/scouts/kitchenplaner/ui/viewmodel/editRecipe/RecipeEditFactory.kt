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
import com.scouts.kitchenplaner.model.entities.DietarySpeciality
import com.scouts.kitchenplaner.model.entities.Ingredient
import com.scouts.kitchenplaner.model.entities.Recipe
import com.scouts.kitchenplaner.model.usecases.EditRecipe
import com.scouts.kitchenplaner.ui.state.EditRecipeState

class RecipeEditFactory(
    private val recipe: Recipe,
    private val useCase: EditRecipe,
    private val state: EditRecipeState
) {

    fun createUpdateNameCommand(name: String): UpdateNameCommand =
        UpdateNameCommand(name, recipe, state, useCase)


    fun createAddDietarySpecialityCommand(speciality: DietarySpeciality): AddDietarySpecialityCommand =
        AddDietarySpecialityCommand(speciality, recipe, state, useCase)


    fun createAddIngredientCommand(group: String, ingredient: Ingredient): AddIngredientCommand =
        AddIngredientCommand(group, ingredient, recipe, state, useCase)

    fun createAddIngredientGroupCommand(group: String): AddIngredientGroupCommand =
        AddIngredientGroupCommand(group, recipe, state, useCase)

    fun createAddInstructionStepCommand(step: String, index: Int): AddInstructionStepCommand =
        AddInstructionStepCommand(index, step, recipe, state, useCase)

    fun createDeleteIngredientCommand(
        group: String,
        ingredient: Ingredient
    ): DeleteIngredientCommand = DeleteIngredientCommand(group, ingredient, recipe, state, useCase)

    fun createDeleteIngredientGroupCommand(group: String): DeleteIngredientGroupCommand =
        DeleteIngredientGroupCommand(group, recipe, state, useCase)

    fun createDeleteInstructionStepCommand(index: Int): DeleteInstructionStepCommand =
        DeleteInstructionStepCommand(index, recipe, state, useCase)

    fun createDeleteSpecialityCommand(speciality: DietarySpeciality): DeleteSpecialityCommand =
        DeleteSpecialityCommand(speciality, recipe, state, useCase)

    fun createEditIngredientCommand(
        group: String, ingredient: Ingredient,
        newName: String?, newAmount: Double?, newUnit: String?
    ): EditIngredientCommand = EditIngredientCommand(
        group, ingredient, newName, newAmount, newUnit,
        recipe, state, useCase
    )

    fun createEditInstructionStepCommand(index: Int, newText: String): EditInstructionStepCommand =
        EditInstructionStepCommand(index, newText, recipe, state, useCase)

    fun createUpdateAmountOfPeopleCommand(newAmount: Int): UpdateAmountOfPeopleCommand =
        UpdateAmountOfPeopleCommand(newAmount, recipe, state, useCase)

    fun createUpdateDescriptionCommand(newDescription: String): UpdateDescriptionCommand =
        UpdateDescriptionCommand(newDescription, recipe, state, useCase)

    fun createUpdateImageURICommand(imageURI: Uri): UpdateImageURICommand =
        UpdateImageURICommand(imageURI, recipe, state, useCase)
}