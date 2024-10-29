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
 * Data base representation of an instruction step.
 * To refer to an instruction use [com.scouts.kitchenplaner.datalayer.dtos.InstructionStepDTO].
 * The combination [order] and [recipe] should be unique in a consistent state of the data base.
 *
 * @param id The unique identifier of the instruction step 
 * @param order The relative order of the instruction step
 * @param recipe The recipe Id to which the instruction step belongs
 * @param instruction The content of the instruction
 */
@Entity(
    foreignKeys = [ForeignKey(
        entity = RecipeEntity::class,
        parentColumns = ["id"],
        childColumns = ["recipe"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("recipe")]
)

data class InstructionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val order: Int,
    var recipe: Long,
    val instruction: String
)
