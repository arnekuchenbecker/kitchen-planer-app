/*
 * KitchenPlanerApp is the android app frontend for the KitchenPlaner, a tool
 * to cooperatively plan a meal plan for a campout.
 * Copyright (C) 2023-2024 Arne Kuchenbecker, Antonia Heiming
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

package com.scouts.kitchenplaner.model.entities

import android.net.Uri

class Recipe(
    val id: Long = 0,
    val name: String = "",
    val imageURI: Uri = Uri.EMPTY,
    val description: String = "",
    val numberOfPeople: Int = -1,
    val traces: List<String> = listOf(),
    val allergen: List<String> = listOf(),
    val freeOfAllergen: List<String> = listOf(),
    val instructions: List<String> = listOf(),
    val ingredientGroups: List<IngredientGroup> = listOf()
) {
}