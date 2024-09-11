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

package com.scouts.kitchenplaner.datalayer.dtos

/**
 * This class represents an instruction step without its id.
 * Each the order of an instruction step should be unique within a recipe
 *
 * @param order The relative order of the instructions
 * @param recipe The id of the recipe, the instruction step belongs to
 * @param instruction The content of the instruction
 */
data class InstructionStepDTO(
    val order: Int,
    var recipe: Long,
    val instruction: String
)
