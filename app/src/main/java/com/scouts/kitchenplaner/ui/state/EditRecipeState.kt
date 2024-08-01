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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import com.scouts.kitchenplaner.model.entities.Ingredient
import com.scouts.kitchenplaner.model.entities.IngredientGroup
import com.scouts.kitchenplaner.model.entities.Recipe

class EditRecipeState {

    var name by mutableStateOf("name ")

    var amount by mutableIntStateOf(0)

    var description by mutableStateOf("meine Beschreibung")


    private val _freeOf: SnapshotStateList<String> = mutableStateListOf<String>()

    private val _allergen: SnapshotStateList<String> = mutableStateListOf<String>()

    private val _traces: SnapshotStateList<String> = mutableStateListOf<String>()

    private val _originalIngredientGroup: SnapshotStateList<String> = mutableStateListOf<String>()

    private val _originalIngredients: SnapshotStateMap<String, MutableList<Ingredient>> =
        mutableStateMapOf()

    private val _addedIngredients: SnapshotStateMap<String, MutableList<Ingredient>> =
        mutableStateMapOf()
    private val _deletedIngredients: SnapshotStateMap<String, MutableList<Ingredient>> =
        mutableStateMapOf()
    private val _addedIngredientGroup: SnapshotStateList<String> = mutableStateListOf()
    private val _deletedIngredientGroup: SnapshotStateList<String> = mutableStateListOf()

    private val _instruction: SnapshotStateList<String> = mutableStateListOf<String>()

    private val _instructionChanges: SnapshotStateList<Triple<Int, String, Boolean>> = mutableStateListOf()

    val allergen: List<String>
        get() = _allergen
    val trace: List<String>
        get() = _traces
    val freeOf: List<String>
        get() = _freeOf
    val ingredients: List<IngredientGroup>
        get() = _addedIngredients.mapValues { (key, value) ->
            if (_originalIngredients.contains(key)) {
                return@mapValues value + _originalIngredients[key]!!
            } else {
                return@mapValues value
            }
        }.map { (group, ingredients) -> IngredientGroup(group, ingredients) }

    val instruction: List<String>
        get() = _instruction

    val instructionChanges: List<Triple<Int,String,Boolean>>
        get() = _instructionChanges

    fun addInstructionStep(index: Int, step: String){
        _instruction.add(index, step)
        _instructionChanges.add(Triple(index, step, true))
    }

    fun deleteInstructionStep(index: Int, step: String){
        _instruction.add(index, step)
        _instructionChanges.add(Triple(index, step,false))

    }

    fun getAddedIngredients():  Map<String, MutableList<Ingredient>> {
        return  _addedIngredients
    }
    fun getDeletedIngredientsAndGroups(): Pair<List<String>, Map<String, MutableList<Ingredient>>> {
        return Pair(_deletedIngredientGroup, _deletedIngredients)
    }

    fun initState(recipe: Recipe) {
        name = recipe.name
        amount = recipe.numberOfPeople
        description = recipe.description
        _freeOf.addAll(recipe.freeOfAllergen)
        _allergen.addAll(recipe.allergens)
        _traces.addAll(recipe.traces)
        _originalIngredientGroup.addAll(recipe.ingredientGroups.map { it.name })

        recipe.ingredientGroups.forEach { group ->
            _originalIngredients[group.name] = group.ingredients.toMutableStateList()
            _addedIngredients[group.name] = mutableStateListOf()
        }
        _instruction.addAll(recipe.instructions)
    }

    fun addIngredientGroup(group: String) {
        if (!_originalIngredientGroup.contains(group)) {
            _addedIngredientGroup.add(group)
        }
        _addedIngredients[group] = mutableStateListOf()
    }

    fun addIngredient(group: String, ingredient: Ingredient) {
        if (_originalIngredientGroup.contains(group) || _addedIngredientGroup.contains(group)) {
            _addedIngredients[group]?.add(ingredient)
        }

    }

    fun deleteIngredient(group: String, ingredient: Ingredient) {
        if (_addedIngredients[group]?.contains(ingredient) == true) {
            _addedIngredients[group]?.remove(ingredient)
        } else if (_originalIngredientGroup.contains(group)) {
            _originalIngredients[group]?.remove(ingredient)
            _deletedIngredients[group]?.add(ingredient)
        }
    }

    fun deleteGroup(group: String) {
        if (_originalIngredientGroup.contains(group)) {
            _originalIngredientGroup.remove(group)
            _deletedIngredientGroup.add(group)
            _originalIngredients.remove(group)

        } else {
            _addedIngredientGroup.remove(group)
        }
        _addedIngredients.remove(group)
        _deletedIngredients.remove(group)
    }

}