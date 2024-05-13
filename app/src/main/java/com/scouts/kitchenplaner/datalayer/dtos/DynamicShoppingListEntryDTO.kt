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

package com.scouts.kitchenplaner.datalayer.dtos

import java.util.Date

/**
 * DTO for transferring dynamic shopping lists out of the database
 *
 * @param ingredient The name of the referenced Ingredient
 * @param amount The amount specified in the recipe
 * @param unit The unit of measure
 * @param peopleBase The amount of people the recipe was written for
 * @param date The date of the MealSlot the entry is relevant for
 * @param meal The meal of the MealSlot the entry is relevant for
 */
data class DynamicShoppingListEntryDTO(
    val ingredient: String,
    val amount: Double,
    val unit: String,
    val peopleBase: Int,
    val date: Date,
    val meal: String
)
