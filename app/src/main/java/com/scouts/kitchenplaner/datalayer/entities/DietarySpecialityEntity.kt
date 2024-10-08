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
import com.scouts.kitchenplaner.model.entities.DietaryTypes

/**
 * Data base entity of a dietary speciality of a recipe.
 * It is uniquely defined by the speciality and the recipe they belong to.
 *
 * @param recipe The recipe the speciality belongs to (foreign key)
 * @param type The type of the speciality
 * @param speciality The content that describes the speciality
 */
@Entity(
    primaryKeys = ["recipe", "speciality"], foreignKeys = [ForeignKey(
        entity = RecipeEntity::class,
        parentColumns = ["id"],
        childColumns = ["recipe"],
        onDelete = ForeignKey.CASCADE
    )], indices = [Index("recipe")]
)
data class DietarySpecialityEntity(
    var recipe: Long, val type: DietaryTypes, val speciality: String
)
