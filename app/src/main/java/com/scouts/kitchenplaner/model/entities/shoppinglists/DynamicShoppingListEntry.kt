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
import com.scouts.kitchenplaner.model.entities.MealPlan
import com.scouts.kitchenplaner.model.entities.MealSlot

class DynamicShoppingListEntry(
    override val name: String,
    override val unit: String,
    private val baseAmount: Int,
    private val peopleBase: Int,
    private val mealSlot: MealSlot
) : ShoppingListEntry {
    override fun getAmount(mealPlan: MealPlan) : Int {
        return (baseAmount * ((mealPlan[mealSlot].second) / (peopleBase.toDouble()))).toInt()
    }

    override fun toDynamicEntity(listID: Long, projectID: Long): DynamicShoppingListEntryEntity {
        return DynamicShoppingListEntryEntity(
            listID,
            projectID,
            mealSlot.date,
            mealSlot.meal,
            name
        )
    }

    override fun toStaticEntity(listID: Long, projectID: Long): StaticShoppingListEntryEntity? {
        return null
    }
}