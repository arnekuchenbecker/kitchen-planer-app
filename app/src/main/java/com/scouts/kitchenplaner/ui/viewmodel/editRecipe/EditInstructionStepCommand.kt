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

import com.scouts.kitchenplaner.model.entities.Recipe
import com.scouts.kitchenplaner.model.usecases.EditRecipe
import com.scouts.kitchenplaner.ui.state.EditRecipeState

/**
 * Command to edit the content of an instruction step
 *
 * @param index The index of the instruction to change
 * @param instruction The new content of the instruction
 */
class EditInstructionStepCommand(
    private val index: Int,
    private val instruction: String,
    recipe: Recipe
) :
    ChangeCommand(recipe = recipe) {
    override fun applyOnState(state: EditRecipeState) {
        state.alterInstructionStep(index, instruction)
    }

    override suspend fun applyOnRecipe(editRecipe: EditRecipe) {
        editRecipe.updateInstructionStep(recipe, index, instruction)
    }
}