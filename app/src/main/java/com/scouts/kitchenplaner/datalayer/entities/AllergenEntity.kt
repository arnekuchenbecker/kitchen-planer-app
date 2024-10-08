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

/**
 * Data base entity of an allergen of a participant of a project.
 * The allergen is identified by its name, the project and the participant
 *
 * @param projectId The project's id to which the participant belongs
 * @param name The name of the participant who has the allergen
 * @param allergen The content of the allergen
 * @param traces Whether traces are relevant
 */
@Entity(
    tableName = "allergens",
    foreignKeys = [
        ForeignKey(
            entity = AllergenPersonEntity::class,
            parentColumns = ["name", "projectId"],
            childColumns = ["name", "projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["name", "projectId", "allergen"]
)
data class AllergenEntity(
    var projectId: Long,
    val name: String,
    val allergen: String,
    val traces: Boolean
)
