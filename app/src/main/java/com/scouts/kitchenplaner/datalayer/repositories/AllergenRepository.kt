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

package com.scouts.kitchenplaner.datalayer.repositories

import com.scouts.kitchenplaner.datalayer.daos.AllergenDAO
import com.scouts.kitchenplaner.datalayer.dtos.AllergenIdentifierDTO
import com.scouts.kitchenplaner.datalayer.dtos.AllergenPersonIdentifierDTO
import com.scouts.kitchenplaner.datalayer.toDataLayerEntity
import com.scouts.kitchenplaner.model.entities.Allergen
import com.scouts.kitchenplaner.model.entities.AllergenPerson
import javax.inject.Inject

class AllergenRepository @Inject constructor(
    private val allergenDAO: AllergenDAO
){
    suspend fun deleteAllergen(projectId: Long, name: String, allergen: String) {
        allergenDAO.deleteAllergen(AllergenIdentifierDTO(projectId, name, allergen))

        if (allergenDAO.getAllergenCountByNameAndProjectId(name, projectId) == 0) {
            allergenDAO.deleteAllergenPerson(AllergenPersonIdentifierDTO(projectId, name))
        }
    }

    suspend fun addAllergenPerson(person: AllergenPerson, projectId: Long) {
        val datalayerEntities = person.toDataLayerEntity(projectId)
        allergenDAO.insertAllergenPersonWithAllergens(
            datalayerEntities.first,
            datalayerEntities.second
        )
    }

    suspend fun addAllergen(name: String, projectId: Long, allergen: Allergen) {
        allergenDAO.insertAllergen(allergen.toDataLayerEntity(projectId, name))
    }
}