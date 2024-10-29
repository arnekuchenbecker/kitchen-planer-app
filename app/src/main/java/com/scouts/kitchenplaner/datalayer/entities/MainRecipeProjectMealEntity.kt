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
 * Data base representation for mapping a main recipe to a meal slot.
 *
 * @param projectId The project the mapping belongs to
 * @param meal The meal that identifies the meal slot the recipe is the main recipe
 * @param date The date on which the recipe is the main recipe
 * @param recipeId The id of the recipe that is the main recipe
 */
@Entity(
    tableName = "recipeProjectMeal",
    primaryKeys = ["projectId", "meal", "date"],
    foreignKeys = [
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["projectId", "name"],
            childColumns = ["projectId", "meal"]
        ),
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"]
        )
    ],
    indices = [
        Index("recipeId"),
        Index("projectId", "meal")
    ]
)
data class MainRecipeProjectMealEntity(
    val projectId: Long,
    val meal: String,
    val date: Date,
    val recipeId: Long
)
