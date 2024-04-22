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

import androidx.lifecycle.ViewModel
import com.scouts.kitchenplaner.model.usecases.RecipeSelection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * This view model provides methods to get all recipe stubs to display them
 * @param recipeSelection use case that provides all recipe stubs
 */
@HiltViewModel
class RecipeSelectionViewModel @Inject constructor(private val recipeSelection: RecipeSelection) :
    ViewModel() {
    val recipes = recipeSelection.getAllRecipeStubs();

}