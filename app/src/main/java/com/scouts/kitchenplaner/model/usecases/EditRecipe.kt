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

package com.scouts.kitchenplaner.model.usecases

import android.net.Uri
import com.scouts.kitchenplaner.datalayer.KitchenAppDataStore
import com.scouts.kitchenplaner.datalayer.repositories.RecipeRepository
import com.scouts.kitchenplaner.model.entities.DietarySpeciality
import com.scouts.kitchenplaner.model.entities.Ingredient
import com.scouts.kitchenplaner.model.entities.IngredientGroup
import com.scouts.kitchenplaner.model.entities.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import java.util.Date
import javax.inject.Inject

/**
 * Usecase for editing a recipe
 *
 * @param recipeRepository The repository to be used to access the data layer
 * @param userDataStore The data store to be used to query the current user
 */
class EditRecipe @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val userDataStore: KitchenAppDataStore
) {
    /**
     * Get a recipe by its ID
     *
     * @param id The ID of the requested recipe
     * @return A flow containing the requested recipe
     */
    fun getRecipe(id: Long): Flow<Recipe> {
        return recipeRepository.getRecipeById(id).onStart {
            recipeRepository.updateLastShownRecipeForUser(
                userDataStore.getCurrentUser(),
                id,
                Date()
            )
        }
    }

    /**
     * Set a recipe's name to a new value
     *
     * @param recipe The recipe to change
     * @param newName The new name
     */
    suspend fun setRecipeName(recipe: Recipe, newName: String) {
        recipeRepository.setRecipeName(recipe.id, newName)
    }

    /**
     * Set a recipe's picture to a new one
     *
     * @param recipe The recipe to change
     * @param newURI The URI of the new picture
     */
    suspend fun setRecipePicture(recipe: Recipe, newURI: Uri) {
        recipeRepository.setRecipeImage(recipe.id, newURI)
    }

    /**
     * Set a recipe's description to a new one
     *
     * @param recipe The recipe to change
     * @param newDescription The new description
     */
    suspend fun setRecipeDescription(recipe: Recipe, newDescription: String) {
        recipeRepository.setRecipeDescription(recipe.id, newDescription)
    }

    /**
     * Set a recipe's number of people it is calculated for to a new value
     *
     * @param recipe The recipe to change
     * @param newNumberOfPeople The new number of people the recipe is calculated for
     */
    suspend fun setNumberOfPeople(recipe: Recipe, newNumberOfPeople: Int) {
        recipeRepository.setNumberOfPeople(recipe.id, newNumberOfPeople)
    }

    /**
     * Add a DietarySpeciality to a recipe
     *
     * @param recipe The recipe to which the speciality should be added
     * @param speciality The speciality that should be added
     */
    suspend fun addDietarySpeciality(recipe: Recipe, speciality: DietarySpeciality) {
        recipeRepository.insertDietarySpeciality(recipe.id, speciality.allergen, speciality.type)
    }

    /**
     * Remove a DietarySpeciality from a recipe
     *
     * @param recipe The recipe from which to remove the speciality
     * @param speciality The speciality that should be removed
     */
    suspend fun deleteDietarySpeciality(recipe: Recipe, speciality: DietarySpeciality) {
        recipeRepository.deleteDietarySpeciality(recipe.id, speciality.allergen)
    }

    /**
     * Add a new IngredientGroup to a recipe (Note that ingredient groups must not be empty)
     *
     * @param recipe The recipe to which to add the ingredient group
     * @param group The IngredientGroup that should be added
     */
    suspend fun addIngredientGroup(recipe: Recipe, group: IngredientGroup) {
        group.ingredients.forEach {
            recipeRepository.insertIngredient(recipe.id, group.name, it)
        }
    }

    /**
     * Remove an IngredientGroup and all ingredients it contains from a recipe
     *
     * @param recipe The recipe from which to remove the ingredient group
     * @param group The ingredient group that should be removed
     */
    suspend fun deleteIngredientGroup(recipe: Recipe, group: IngredientGroup) {
        recipeRepository.deleteIngredientGroup(recipe.id, group.name)
    }

    /**
     * Add an ingredient to a recipe
     *
     * @param recipe The recipe to which to add the ingredient
     * @param group The ingredient group to which to add the ingredient
     * @param ingredient The ingredient that should be added
     */
    suspend fun addIngredient(recipe: Recipe, group: IngredientGroup, ingredient: Ingredient) {
        recipeRepository.insertIngredient(recipe.id, group.name, ingredient)
    }

    /**
     * Remove an ingredient from a recipe (Note that if this was the last ingredient in its group,
     * the ingredient group will also be deleted)
     *
     * @param recipe The recipe from which to remove the ingredient
     * @param group The ingredient group from which to remove the ingredient
     * @param ingredient The ingredient that should be removed
     */
    suspend fun deleteIngredient(recipe: Recipe, group: IngredientGroup, ingredient: Ingredient) {
        recipeRepository.deleteIngredient(recipe.id, group.name, ingredient.name)
    }

    /**
     * Update a recipe's ingredient, setting all non-null parameters to the given values
     *
     * @param recipe The recipe the ingredient belongs to
     * @param group The ingredient group the ingredient is associated with
     * @param ingredient The ingredient whose name should be changed
     * @param newName The new name that should be set for the ingredient (or null if it shouldn't be
     *                changed
     * @param newAmount The new amount that should be set for the ingredient (or null if shouldn't
     *                  be changed
     * @param newUnit The new unit that should be set for the ingredient (or null if it shouldn't be
     *                changed
     */
    suspend fun editIngredient(
        recipe: Recipe,
        group: IngredientGroup,
        ingredient: Ingredient,
        newName: String? = null,
        newAmount: Float? = null,
        newUnit: String? = null
    ) {
        if (newName != null) {
            recipeRepository.updateIngredientName(recipe.id, group.name, ingredient, newName)
        }
        if (newAmount != null) {
            recipeRepository.updateIngredientAmount(recipe.id, group.name, ingredient, newAmount)
        }
        if (newUnit != null) {
            recipeRepository.updateIngredientUnit(recipe.id, group.name, ingredient, newUnit)
        }
    }

    /**
     * Add an instruction step to a recipe
     *
     * @param recipe The recipe to which to add the instruction step
     * @param step The instruction step that should be added
     * @param index The index of the instruction step in front of which the new one should be added
     */
    suspend fun addInstructionStep(recipe: Recipe, step: String, index: Int) {
        recipeRepository.insertInstructionStep(recipe.id, step, index)
    }

    /**
     * Remove an instruction step from a recipe
     *
     * @param recipe The recipe from which to remove the instruction step
     * @param index The index of the instruction step that should be removed
     */
    suspend fun deleteInstructionStep(recipe: Recipe, index: Int) {
        recipeRepository.deleteInstructionStep(recipe.id, index)
    }

    /**
     * Update an instruction step's text
     *
     * @param recipe The recipe the instruction that should be edited belongs to
     * @param index The index of the instruction that should be edited
     * @param newInstruction The new text of the instruction
     */
    suspend fun updateInstructionStep(recipe:Recipe, index: Int, newInstruction: String) {
        recipeRepository.updateInstructionStep(recipe.id, index, newInstruction)
    }
}