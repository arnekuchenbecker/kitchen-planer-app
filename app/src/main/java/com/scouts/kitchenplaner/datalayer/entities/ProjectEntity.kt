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
import java.util.Date

/**
 * Data base entity for a project. It contains all meta data of a project.
 *
 * @param id The  offline id of the project
 * @param name The name of the project
 * @param startDate The date when the project starts
 * @param endDate The date when the project ends
 * @param imageUri The URI leading to the picture for the project
 * @param isArchived Whether the project is currently archived (and thus only meta data is currently available
 */
@Entity(tableName = "projects")
data class ProjectEntity (
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val startDate: Date,
    val endDate: Date,
    val imageUri: String,
    val isArchived: Boolean
)