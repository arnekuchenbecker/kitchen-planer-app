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

package com.scouts.kitchenplaner.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.scouts.kitchenplaner.model.entities.shoppinglists.ShoppingListEntry

/**
 * Wrapper class for shopping list entries, allowing them to be enabled or disabled during shopping
 * list creation.
 *
 * @param item The wrapped shopping list entry
 * @param _enabled Whether the entry should initially be
 */
class ShoppingListEntryState (
    val item: ShoppingListEntry,
    _enabled: Boolean = true
) {
    /**
     * Whether the entry is enabled
     */
    var enabled by mutableStateOf(_enabled)
}