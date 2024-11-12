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
import androidx.room.PrimaryKey

/**
 * Data base entity for a recipe. IT contains all relevant meta data.
 *
 * @param id The id of the recipe
 * @param title The title of the recipe
 * @param imageURI The URI of the picture for the recipe
 * @param description The description for the recipe
 * @param numberOfPeople For how many people the recipe is written
 */
@Entity(tableName = "recipeEntity")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val title: String,
    val imageURI: String,
    val description: String,
    val numberOfPeople: Int

)