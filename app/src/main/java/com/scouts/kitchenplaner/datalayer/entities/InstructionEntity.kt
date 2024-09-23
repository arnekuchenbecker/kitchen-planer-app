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

/**
 * Data base entity for an instruction step. An instruction step is uniquely identified by the recipe it belongs to and its order.
 * Because the order can change often, it has another key.
 * To refer to this entity the DTO should be used.
 *
 * @param id The unique identifier of the instruction step
 * @param order The order of the instruction step
 * @param recipe The recipe the instruction step belongs to
 * @param instruction The content of the instruction step
 */
@Entity(
    primaryKeys = ["order", "recipe"],
    foreignKeys = [ForeignKey(
        entity = RecipeEntity::class,
        parentColumns = ["id"],
        childColumns = ["recipe"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("recipe")]
)
data class InstructionEntity(
    val order: Int,
    var recipe: Long,
    val instruction: String
)
