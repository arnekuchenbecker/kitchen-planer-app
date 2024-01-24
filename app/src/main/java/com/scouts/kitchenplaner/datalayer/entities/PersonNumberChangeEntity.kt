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
import androidx.room.Index
import java.util.Date

/**
 * Models the Arrival or departure of persons
 */
@Entity(
    tableName = "personNumberChanges",
    primaryKeys = ["projectId", "date", "meal"],
    foreignKeys = [
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["projectId", "name"],
            childColumns = ["projectId", "meal"]
        )
    ],
    indices = [Index("projectId", "meal")]
)
data class PersonNumberChangeEntity(
    val projectId: Long,
    val date: Date,
    val meal: String,
    /**
     * differences are applied before the specified meal slot
     */
    val differenceBefore: Int
)
