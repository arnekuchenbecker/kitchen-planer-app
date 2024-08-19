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

class EditRecipeState {

    var name by mutableStateOf("name")
    var amount by mutableIntStateOf(0)
    var description by mutableStateOf("meine Beschreibung")
    var imageURI by mutableStateOf(Uri.EMPTY)

    private val _freeOf: SnapshotStateList<String> = mutableStateListOf()
    private val _allergens: SnapshotStateList<String> = mutableStateListOf()
    private val _traces: SnapshotStateList<String> = mutableStateListOf()


    private val _ingredients: SnapshotStateMap<String, SnapshotStateList<Ingredient>> =
        mutableStateMapOf()

    private val _instructions: SnapshotStateList<String> = mutableStateListOf()

    val freeOf: List<String>
        get() = _freeOf
    val allergens: List<String>
        get() = _allergens
    val traces: List<String>
        get() = _traces

    val ingredients: List<IngredientGroup>
        get() = _ingredients.map { (group, ingredients) -> IngredientGroup(group, ingredients) }

    val instructions: List<String>
        get() = _instructions


    fun init(recipe: Recipe) {
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

    fun addDietarySpeciality(speciality: String, type: DietaryTypes) {
        when (type) {
            DietaryTypes.TRACE -> _traces.add(speciality)
            DietaryTypes.ALLERGEN -> _allergens.add(speciality)
            DietaryTypes.FREE_OF -> _freeOf.add(speciality)
        }
    }

    fun deleteDietarySpeciality(speciality: String, type: DietaryTypes) {
        when (type) {
            DietaryTypes.TRACE -> _traces.remove(speciality)
            DietaryTypes.ALLERGEN -> _allergens.remove(speciality)
            DietaryTypes.FREE_OF -> _freeOf.remove(speciality)
        }
    }


    fun addInstructionStep(index: Int, instruction: String) {
        _instructions.add(index, instruction)
    }

    fun alterInstructionStep(index: Int, newInstruction: String) {
        _instructions[index] = newInstruction
    }

    fun deleteInstructionStep(index: Int) {
        _instructions.removeAt(index)
    }

    fun addIngredientGroup(name: String) {
        if (!_ingredients.containsKey(name)) {
            _ingredients[name] = mutableStateListOf()
        }
    }

    fun deleteIngredientGroup(name: String) {
        _ingredients.remove(name)
    }

    fun addIngredient(group: String, ingredient: Ingredient) {
        if (_ingredients.containsKey(group)) {
            _ingredients[group]!!.add(ingredient)
        }
    }

    fun deleteIngredient(group: String, ingredient: Ingredient) {
        if (_ingredients.containsKey(group)) {
            _ingredients[group]!!.remove(ingredient)
        }
    }

    fun alterIngredient(group: String, ingredient: Ingredient, newValues: Ingredient) {
        if (_ingredients.containsKey(group)) {
            val index = _ingredients[group]!!.indexOf(ingredient)
            _ingredients[group]!!.set(index = index, newValues)
        }
    }
}
