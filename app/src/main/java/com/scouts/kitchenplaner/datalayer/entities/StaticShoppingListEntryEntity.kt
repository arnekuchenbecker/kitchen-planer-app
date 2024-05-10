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

/**
 * Entity class for static shopping list entries
 *
 * @param listId The ID of the shopping list this entry belongs to
 * @param projectId The ID of the project the shopping list this entry belong to belongs to
 * @param ingredientName The name of the ingredient that should be purchased
 * @param amount The amount that should be purchased
 * @param unit The unit of measure
 */
@Entity(
    tableName = "staticShoppingListEntries",
    primaryKeys = ["listId", "ingredientName", "unit"],
    foreignKeys = [
        ForeignKey(
            entity = ShoppingListEntity::class,
            parentColumns = ["id", "projectId"],
            childColumns = ["listId", "projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("listId", "projectId")
    ]
)
data class StaticShoppingListEntryEntity(
    var listId: Long,
    val projectId: Long,
    val ingredientName: String,
    val amount: Int,
    val unit: String
)
