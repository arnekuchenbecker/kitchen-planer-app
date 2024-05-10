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

import com.scouts.kitchenplaner.datalayer.entities.DynamicShoppingListEntryEntity
import com.scouts.kitchenplaner.datalayer.entities.StaticShoppingListEntryEntity
import com.scouts.kitchenplaner.model.DomainLayerRestricted
import com.scouts.kitchenplaner.model.entities.MealPlan

/**
 * A static entry of a shopping list
 *
 * @param name The name of the ingredient
 * @param unit The unit of measure
 * @param _amount The amount that should be purchased
 */
class StaticShoppingListEntry @DomainLayerRestricted constructor(
    override val name: String,
    override val unit: String,
    private val _amount: Double
) : ShoppingListEntry {
    override fun getAmount(mealPlan: MealPlan): Double {
        return _amount
    }

    override fun toDynamicEntity(listID: Long, projectID: Long): DynamicShoppingListEntryEntity? {
        return null
    }

    override fun toStaticEntity(listID: Long, projectID: Long): StaticShoppingListEntryEntity {
        return StaticShoppingListEntryEntity(
            listID,
            projectID,
            name,
            _amount,
            unit
        )
    }
}