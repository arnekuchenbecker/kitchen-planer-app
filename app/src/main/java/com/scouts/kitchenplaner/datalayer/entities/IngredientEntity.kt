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

/**
 * Data base entity for an ingredient of a recipe. The ingredient group is saved implicitly in the ingredient entity and not in an own entity.
 *
 * @param recipe The recipe this ingredient belongs to
 * @param ingredientGroup The ingredient group the ingredient belongs to
 * @param name The name of the ingredient
 * @param amount The amount of the ingredient needed in the recipe
 * @param unit The unit af the amount
 */
@Entity(
    primaryKeys = ["ingredientGroup", "recipe", "name"]
)
data class IngredientEntity(
    var recipe: Long,
    val ingredientGroup: String,
    val name: String,
    val amount: Double,
    val unit: String //TODO maybe change for calculation
)