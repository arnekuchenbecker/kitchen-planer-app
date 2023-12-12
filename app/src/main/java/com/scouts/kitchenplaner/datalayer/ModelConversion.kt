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

package com.scouts.kitchenplaner.datalayer

import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.scouts.kitchenplaner.datalayer.entities.AllergenPersonEntity
import com.scouts.kitchenplaner.datalayer.entities.ProjectEntity
import com.scouts.kitchenplaner.model.entities.AllergenPerson
import com.scouts.kitchenplaner.model.entities.Project

fun Project.toDataLayerEntity() : ProjectEntity {
    return ProjectEntity(
        id = (id ?: 0),
        name = name,
        startDate = startDate,
        endDate = endDate,
        imageUri = projectImage.path ?: ""
    )
}

fun AllergenPerson.toDataLayerEntity() : AllergenPersonEntity {
    return AllergenPersonEntity(
        name = name,
        projectId = project.id ?: 0,
        allergen = allergen,
        traces = traces,
        arrivalDate = arrivalDate,
        arrivalMeal = arrivalMeal,
        departureDate = departureDate,
        departureMeal = departureMeal
    )
}
