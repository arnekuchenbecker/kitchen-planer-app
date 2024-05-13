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

/**
 * A shopping list entry. Shopping list entries can either be dynamic (i.e. they get scaled by the
 * number of people that are present at the meal slot they are relevant for) or static (i.e. they
 * are independent of the number of people at any given meal slot)
 */
interface ShoppingListEntry {
    /**
     * The name of the ingredient that should be purchased
     */
    val name: String

    /**
     * The unit of measure
     */
    val unit: String

    /**
     * Calculates how much of the ingredient should be purchased
     *
     * @param mealPlan The meal plan of the project this entry belongs to
     *
     * @return The amount that should be purchased
     */
    fun getAmount(mealPlan: MealPlan) : Double

    /**
     * Converts this entry to a data layer entity representing a dynamic shopping list entry if
     * possible
     *
     * @return A DynamicShoppingListEntryEntity representing this shopping list entry or null if
     *         such an entity can not be constructed (e.g. because this is a static entry)
     */
    fun toDynamicEntity(listID: Long, projectID: Long) : DynamicShoppingListEntryEntity?

    /**
     * Converts this entry to a data layer entity representing a static shopping list entry if
     * possible
     *
     * @return A StaticShoppingListEntryEntity representing this shopping list entry of null if such
     *         an entity can not be constructed (e.g. because this is a dynamic entry)
     */
    fun toStaticEntity(listID: Long, projectID: Long) : StaticShoppingListEntryEntity?
}