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

package com.scouts.kitchenplaner.datalayer.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import java.util.Date

@Entity(foreignKeys = [
    ForeignKey(
        entity = ProjectEntity::class,
        parentColumns = ["id"],
        childColumns = ["projectId"],
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = MealEntity::class,
        parentColumns = ["name", "projectId"],
        childColumns = ["arrivalMeal", "projectId"],
    ),
    ForeignKey(
        entity = MealEntity::class,
        parentColumns = ["name", "projectId"],
        childColumns = ["departureMeal", "projectId"]
    )
],
    primaryKeys = ["name", "projectId"])
data class AllergenPersonEntity(
    val name: String,
    var projectId: Long,
    val arrivalDate: Date,
    val arrivalMeal: String,
    val departureDate: Date,
    val departureMeal: String
)
