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

package com.scouts.kitchenplaner.networklayer.kitchenplaner.dtos.projects

import java.util.Date

/**
 * DTO for communication with the server. Represents a recipe mapping (i.e. maps a recipe to a meal
 * slot).
 *
 * @param date The date of the meal slot
 * @param meal The meal of the meal slot
 * @param recipeID The onlineID of the recipe
 * @param mainRecipe Whether this recipe should be selected as the main recipe for the meal slot
 */
data class ServerRecipeMappingDTO(
    val date: Date,
    val meal: String,
    val recipeID: Long,
    val mainRecipe: Boolean
)