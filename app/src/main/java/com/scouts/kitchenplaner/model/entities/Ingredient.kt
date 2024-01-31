/*
 * KitchenPlanerApp is the android app frontend for the KitchenPlaner, a tool
 * to cooperatively plan a meal plan for a campout.
 * Copyright (C) 2023-2024 Arne Kuchenbecker, Antonia Heiming
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

package com.scouts.kitchenplaner.model.entities

class Ingredient(
    val name: String,
    private var _amount: Float,
    val unit: String
) {
    val amount: Float
        get() = _amount

    fun setAmount(newAmount: Float) {
        _amount = newAmount
    }

    override fun equals(other: Any?): Boolean = (other is Ingredient)
            && name == other.name
            && _amount == other._amount
            && unit == other.unit

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + _amount.hashCode()
        result = 31 * result + unit.hashCode()
        return result
    }
}