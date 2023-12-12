/*
 * KitchenPlanerApp is the android app frontend for the KitchenPlaner, a tool
 * to cooperatively plan a meal plan for a campout.
 * Copyright (C) 2023  Arne Kuchenbecker, Antonia Heiming
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

import android.net.Uri
import java.util.Date

/**
 * NOT FINAL --> Adjust when stuff actually happens to projects
 */
class Project {
    val id: Long? = null
    val name: String = ""
    val startDate: Date = Date(0)
    val endDate: Date = Date(0)
    val meals: List<String> = listOf()
    val allergenPersons: List<AllergenPerson> = listOf()
    val projectImage: Uri = Uri.EMPTY
}