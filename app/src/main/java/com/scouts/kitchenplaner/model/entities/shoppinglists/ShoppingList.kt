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

package com.scouts.kitchenplaner.model.entities.shoppinglists

/**
 * A shopping list
 *
 * @param id A way to identify this shopping list. Can be null if it isn't known (e.g. if the
 *           shopping list hasn't been created in the database yet)
 * @param name The name of the shopping list. Doesn't have to be unique.
 * @param items The entries of the shopping list
 */
data class ShoppingList (
    val id: Long = 0,
    val name: String,
    val items: List<ShoppingListEntry>
)