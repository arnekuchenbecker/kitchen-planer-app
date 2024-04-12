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

package com.scouts.kitchenplaner.datalayer.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.Date

/**
 * Database Entity for entries of shopping lists. Shopping lists update dynamically if referenced
 * recipes are updated. To facilitate this, no fixed values are stored, but instead the MealSlot for
 * which this entry includes an ingredient.
 *
 * @param listId The shopping list this entry belongs to
 * @param projectId The project the shopping list belongs to
 * @param mealDate The date the meal is cooked on for which this ingredient is required
 * @param meal The meal for which this ingredient is required
 * @param ingredientName The name of the ingredient
 */
@Entity(
    tableName = "shoppingListEntries",
    primaryKeys = ["listId", "mealDate", "meal", "ingredientName"],
    foreignKeys = [
        ForeignKey(
            entity = ShoppingListEntity::class,
            parentColumns = ["id", "projectId"],
            childColumns = ["listId", "projectId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["projectId", "name"],
            childColumns = ["projectId", "meal"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("listId", "projectId"),
        Index("projectId", "meal")
    ]
)
data class ShoppingListEntryEntity(
    var listId: Long,
    val projectId: Long,
    val mealDate: Date,
    val meal: String,
    val ingredientName: String
)
