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

package com.scouts.kitchenplaner.ui.state

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.scouts.kitchenplaner.model.entities.DietaryTypes
import com.scouts.kitchenplaner.model.entities.Ingredient
import com.scouts.kitchenplaner.model.entities.IngredientGroup
import com.scouts.kitchenplaner.model.entities.Recipe

/**
 * State object for editing a recipe and storing the current changes
 */
class EditRecipeState {

    /**
     * This constructor provides a default implementation of the state
     * It should only be used to clear the state, if it used later
     */
    constructor()

    /**
     * This constructor creates a state with respect to the given recipe by copying all editable information from it
     * @param recipe The recipe that provides the data
     */
    constructor(recipe: Recipe) {
        name = recipe.name
        amount = recipe.numberOfPeople
        description = recipe.description
        imageURI = recipe.imageURI

        _freeOf.addAll(recipe.freeOfAllergen)
        _traces.addAll(recipe.traces)
        _allergens.addAll(recipe.allergens)

        for (group in recipe.ingredientGroups) {
            val ingredients = mutableStateListOf<Ingredient>()
            ingredients.addAll(group.ingredients)
            _ingredients[group.name] = ingredients
        }

        _instructions.addAll(recipe.instructions)
    }


    /**
     * Current name of the recipe
     */
    var name by mutableStateOf("name")

    /**
     * Current amount of people
     */
    var amount by mutableIntStateOf(0)

    /**
     * Current description of the recipe
     */
    var description by mutableStateOf("meine Beschreibung")

    /**
     * Image URI if an image should be displayed (otherwise it is empty)
     */
    var imageURI by mutableStateOf(Uri.EMPTY)

    /**
     * List of all dietary specialities that the recipe is free of
     */
    val freeOf: List<String>
        get() = _freeOf

    /**
     * List of all dietary specialities that are in the recipe
     */
    val allergens: List<String>
        get() = _allergens

    /**
     * List of all dietary specialities from which only traces are on the recipe
     */
    val traces: List<String>
        get() = _traces

    /**
     * List of all ingredients for the recipe grouped by their ingredient groups
     */
    val ingredients: List<IngredientGroup>
        get() = _ingredients.map { (group, ingredients) -> IngredientGroup(group, ingredients) }

    /**
     * Sorted list of all instructions for the recipe
     */
    val instructions: List<String>
        get() = _instructions

    private val _freeOf: SnapshotStateList<String> = SnapshotStateList()
    private val _allergens: SnapshotStateList<String> = SnapshotStateList()
    private val _traces: SnapshotStateList<String> = SnapshotStateList()


    private val _ingredients: SnapshotStateMap<String, SnapshotStateList<Ingredient>> =
        SnapshotStateMap()

    private val _instructions: SnapshotStateList<String> = SnapshotStateList()


    /**
     * Adds a new dietary speciality to the correct list
     * Note that the speciality cannot be present in any speciality list yet
     *
     * @param speciality The new speciality
     * @param type The type of the speciality
     */
    fun addDietarySpeciality(speciality: String, type: DietaryTypes) {
        when (type) {
            DietaryTypes.TRACE -> _traces.add(speciality)
            DietaryTypes.ALLERGEN -> _allergens.add(speciality)
            DietaryTypes.FREE_OF -> _freeOf.add(speciality)
        }
    }

    /**
     * Deletes a speciality from the given list
     * Note that the speciality is identified by it name
     *
     * @param speciality The speciality which should be deleted
     * @param type The list from which the speciality is going to removed
     */
    fun deleteDietarySpeciality(speciality: String, type: DietaryTypes) {
        when (type) {
            DietaryTypes.TRACE -> _traces.remove(speciality)
            DietaryTypes.ALLERGEN -> _allergens.remove(speciality)
            DietaryTypes.FREE_OF -> _freeOf.remove(speciality)
        }
    }


    /**
     * Adds a new instruction step at the given index
     * or as first instruction if the index is not defined
     *
     * @param index The index where the instruction should be added
     * @param instruction The content of the new instruction
     */
    fun addInstructionStep(index: Int, instruction: String) {
        _instructions.add(index, instruction)
    }

    /**
     * Alters the content of the given instruction step
     *
     * @param index The index of the instruction to be changed
     * @param newInstruction The new content for the instruction
     */
    fun alterInstructionStep(index: Int, newInstruction: String) {
        _instructions[index] = newInstruction
        TODO("alter instruction step")
    }

    /**
     * Deletes the instruction step at the given index
     *
     * @param index The index to be deleted
     */
    fun deleteInstructionStep(index: Int) {
        _instructions.removeAt(index)
    }

    /**
     * Adds a new ingredient group if it is not already there
     *
     * @param name The name of the new group
     */
    fun addIngredientGroup(name: String) {
        if (!_ingredients.containsKey(name)) {
            _ingredients[name] = mutableStateListOf()
        }
    }

    /**
     * Deletes an ingredient  group  and all the ingredients in it
     *
     * @param name The name of the ingredient group to be deleted
     */
    fun deleteIngredientGroup(name: String) {
        _ingredients.remove(name)
    }

    /**
     * Adds an ingredient to an ingredient group
     *
     * @param group The group where the ingredient should be added to
     * @param ingredient The new ingredient
     */
    fun addIngredient(group: String, ingredient: Ingredient) {
        if (_ingredients.containsKey(group)) {
            _ingredients[group]!!.add(ingredient)
        }
    }

    /**
     * Deletes an ingredient from an ingredient group
     *
     * @param group The group from which the ingredient should be deleted
     * @param ingredient The ingredient that should be deleted
     */
    fun deleteIngredient(group: String, ingredient: Ingredient) {
        if (_ingredients.containsKey(group)) {
            _ingredients[group]!!.remove(ingredient)
        }
    }

    /**
     * Alter the content of the given ingredient
     * Note that if only some values of the ingredient should be changed,
     * the new ingredient [newValues] has to contain the same values
     * as the [ingredient] for them which are not to change
     *
     * @param group The group in which the ingredient to edit is
     * @param ingredient The ingredient to be changed
     * @param newValues The new values for the ingredient to be changed
     */
    fun alterIngredient(group: String, ingredient: Ingredient, newValues: Ingredient) {
        if (_ingredients.containsKey(group)) {
            val index = _ingredients[group]!!.indexOf(ingredient)
            _ingredients[group]!!.set(index = index, newValues)
        }
    }
}
