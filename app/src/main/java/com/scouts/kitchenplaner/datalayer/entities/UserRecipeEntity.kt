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
 * Data base representation of mapping the recipe to a user for saving which recipe is the recipe last shown to the user.
 *
 * @param recipe The recipe the user has seen
 * @param user The username of the user who has seen the recipe
 * @param lastShown The recent point in time when the user has seen the recipe
 */
@Entity(
    tableName = "userRecipe", foreignKeys = [ForeignKey(
        entity = UserEntity::class, parentColumns = ["username"], childColumns = ["user"]
    ), ForeignKey(
        entity = RecipeEntity::class, parentColumns = ["id"], childColumns = ["recipe"]
    )], primaryKeys = ["recipe", "user"], indices = [Index("user")]
)
data class UserRecipeEntity(
    val recipe: Long, val user: String, val lastShown: Date
)
