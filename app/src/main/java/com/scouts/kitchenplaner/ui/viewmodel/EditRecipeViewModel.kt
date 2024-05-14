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

@HiltViewModel
class EditRecipeViewModel @Inject constructor(private val editRecipe: EditRecipe) : ViewModel() {
    lateinit var recipeFlow: StateFlow<Recipe>
    suspend fun getRecipe(recipeID: Long) {
        recipeFlow = editRecipe.getRecipe(recipeID).stateIn(viewModelScope)
    }

    fun setRecipeName(recipe: Recipe, name: String) {
        viewModelScope.launch {
            editRecipe.setRecipeName(recipe, name)
        }
    }

    fun setDescription(recipe: Recipe, description: String) {
        viewModelScope.launch {
            editRecipe.setRecipeDescription(recipe, description)
        }
    }

    fun setRecipePicture(recipe: Recipe, uri: Uri) {
        viewModelScope.launch {
            editRecipe.setRecipePicture(recipe, uri)
        }
    }

    fun setNumberOfPeople(recipe: Recipe, numberOfPeople: Int) {
        viewModelScope.launch {
            editRecipe.setNumberOfPeople(recipe, numberOfPeople)
        }
    }

    fun addDietarySpeciality(recipe: Recipe, speciality: DietarySpeciality) {
        viewModelScope.launch {
            editRecipe.addDietarySpeciality(recipe, speciality)
        }
    }

    fun deleteDietarySpeciality(recipe: Recipe, speciality: DietarySpeciality) {
        viewModelScope.launch { editRecipe.deleteDietarySpeciality(recipe, speciality) }
    }

    fun addIngredient(recipe: Recipe, group: IngredientGroup, ingredient: Ingredient? = null) {
        viewModelScope.launch {
            if (ingredient == null) {
                editRecipe.addIngredientGroup(recipe, group)

            } else {
                editRecipe.addIngredient(recipe, group, ingredient)
            }
        }
    }

    fun deleteIngredient(recipe: Recipe, group: IngredientGroup, ingredient: Ingredient? = null) {
        viewModelScope.launch {
            if (ingredient == null) {
                editRecipe.deleteIngredientGroup(recipe, group)
            } else {
                editRecipe.deleteIngredient(recipe, group, ingredient)
            }
        }
    }

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
                recipe,
                group,
                ingredient,
                newName,
                newAmount,
                newUnit
            )
        }
    }

    fun addInstructionStep(recipe: Recipe, step: String, index: Int) {
        viewModelScope.launch {
            editRecipe.addInstructionStep(recipe, step, index)
        }
    }

    fun removeInstructionStep(recipe: Recipe, index: Int) {
        viewModelScope.launch {
            editRecipe.deleteInstructionStep(recipe, index)
        }
    }

    fun updateInstructionStep(recipe: Recipe, index: Int, instruction: String) {
        viewModelScope.launch {
            if (index >= 0) {
                editRecipe.updateInstructionStep(recipe, index, instruction)
            }
        }
    }


}