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

package com.scouts.kitchenplaner.datalayer

import com.scouts.kitchenplaner.datalayer.entities.AllergenEntity
import com.scouts.kitchenplaner.datalayer.entities.AllergenPersonEntity
import com.scouts.kitchenplaner.datalayer.entities.ProjectEntity
import com.scouts.kitchenplaner.datalayer.entities.ShoppingListEntity
import com.scouts.kitchenplaner.datalayer.entities.ShoppingListEntryEntity
import com.scouts.kitchenplaner.model.entities.AllergenPerson
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.ShoppingList

fun Project.toDataLayerEntity() : ProjectEntity {
    return ProjectEntity(
        id = (id ?: 0),
        name = name,
        startDate = startDate,
        endDate = endDate,
        imageUri = projectImage.toString()
    )
}

fun AllergenPerson.toDataLayerEntity(projectId: Long?) : Pair<AllergenPersonEntity, List<AllergenEntity>> {
    return Pair(AllergenPersonEntity(
        name = name,
        projectId = projectId ?: 0,
        arrivalDate = arrivalDate,
        arrivalMeal = arrivalMeal,
        departureDate = departureDate,
        departureMeal = departureMeal
    ), allergens.map {
        AllergenEntity(projectId ?: 0, name, it.allergen, it.traces)
    })
}

fun ShoppingList.toDataLayerEntity(projectId: Long) : Pair<ShoppingListEntity, List<ShoppingListEntryEntity>> {
    return Pair(
        ShoppingListEntity(
            id = id ?: 0,
            name = name,
            projectId = projectId
        ),
        items.map {
            ShoppingListEntryEntity(id ?: 0, projectId, it.name, it.amount, it.unit)
        }
    )
}