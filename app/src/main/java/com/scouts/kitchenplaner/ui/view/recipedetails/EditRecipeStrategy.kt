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

package com.scouts.kitchenplaner.ui.view.recipedetails

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.scouts.kitchenplaner.model.entities.IngredientGroup

/**
 * Part of a strategy pattern. Interface that provides methods to use the edit recipe screen.
 */
interface EditRecipeStrategy {

    /**
     * Composable to display the header field
     */
    @Composable
    fun DisplayHeaderField()

    /**
     * Composable that provides the button image for the header field
     */
    @Composable
    fun DisplayHeaderImage()

    /**
     * Composable that provides possibility to display the number of servings
     */
    @Composable
    fun NumberOfPersons(modifier: Modifier)

    /**
     * Composable that provides the picture for the recipe if available
     */
    @Composable
    fun RecipePicture()

    /**
     * Composable that provides the description of the recipe
     * @param modifier Modifier that may align the field within an outer structure
     */
    @Composable
    fun Description(modifier: Modifier)

    /**
     * Provides a list of allergens the recipe does not contain
     */
    fun getFreeOfList(): List<String>

    /**
     * Provides a list of traces which are in the recipe
     */
    fun getTracesList(): List<String>

    /**
     * Provides a list of allergens within the recipe
     */
    fun getAllergenList(): List<String>

    /**
     * Provides all instruction steps for the recipe
     */
    fun getInstructionSteps(): List<String>

    /**
     * Provides all ingredients with their groups of the recipe
     */
    fun getIngredientGroups(): List<IngredientGroup>
}