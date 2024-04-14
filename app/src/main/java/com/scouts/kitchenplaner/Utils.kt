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

package com.scouts.kitchenplaner

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Formats this Long to represent a date in dd.MM.yyyy format
 */
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

/**
 * Constructs a list with all dates between this and the given end date
 *
 * @param endInclusive The (inclusive) end date
 *
 * @return A list containing all dates between this Date and endInclusive (inclusive)
 */
fun Date.listDatesUntil(endInclusive: Date) : List<Date> {
    var date = this
    val result = mutableListOf<Date>()
    while (date.before(endInclusive)) {
        result.add(Date(date.time))
        date = incrementDate(date)
    }
    result.add(endInclusive)
    return result
}

/**
 * Adds one day to the given date
 *
 * @param date The date to add a day to
 *
 * @return A date object representing the point in Time exactly one day after date
 */
private fun incrementDate(date: Date) : Date {
    val cal = Calendar.getInstance()
    cal.time = date
    cal.add(Calendar.DATE, 1)
    return Date(cal.time.time)
}