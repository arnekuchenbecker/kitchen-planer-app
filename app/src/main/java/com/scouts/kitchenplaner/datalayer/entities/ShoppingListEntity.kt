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
import androidx.room.PrimaryKey

/**
 * Database Entity for a shopping list
 *
 * @param id Primary Key
 * @param name Name of the shopping list. Does not have to be unique as it is not used as primary
 *             key.
 * @param projectId Foreign Key pointing to the project this shopping list was created for
 */
@Entity(
    tableName = "shoppingLists",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"]
        )
    ],
    indices = [
        Index("projectId"),
        Index(
            value = ["id", "projectId"],
            unique = true
        )
    ]
)
data class ShoppingListEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val projectId: Long
)
