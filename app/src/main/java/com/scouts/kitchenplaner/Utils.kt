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

package com.scouts.kitchenplaner

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toDateString() : String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY)
    return dateFormat.format(this)
}

/**
 * Checks if this Date is between start and end (both inclusive)
 */
@Throws(IllegalArgumentException::class)
fun Date.between(start: Date, end: Date) : Boolean {
    if (end.before(start)) {
        throw IllegalArgumentException("End date must not be before start date!")
    } else {
        return this == start || (this.after(start) && this.before(end)) || this == end
    }
}