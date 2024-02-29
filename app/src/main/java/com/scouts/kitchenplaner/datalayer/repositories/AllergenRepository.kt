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

package com.scouts.kitchenplaner.datalayer.repositories

import com.scouts.kitchenplaner.datalayer.daos.AllergenDAO
import com.scouts.kitchenplaner.datalayer.dtos.AllergenIdentifierDTO
import com.scouts.kitchenplaner.datalayer.dtos.AllergenPersonIdentifierDTO
import com.scouts.kitchenplaner.datalayer.entities.AllergenPersonEntity
import com.scouts.kitchenplaner.datalayer.toDataLayerEntity
import com.scouts.kitchenplaner.datalayer.toModelEntity
import com.scouts.kitchenplaner.model.entities.Allergen
import com.scouts.kitchenplaner.model.entities.AllergenPerson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Date
import javax.inject.Inject

class AllergenRepository @Inject constructor(
    private val allergenDAO: AllergenDAO
){
    fun getAllergenPersonsByProjectID(id: Long) : Flow<List<AllergenPerson>> {
        return allergenDAO.getAllergenPersonsByProjectID(id).combine(allergenDAO.getAllergensByProjectID(id)) { persons, allergens ->
            persons.map {
                it.toModelEntity(allergens.filter { entity -> it.name == entity.name })
            }
        }
    }

    suspend fun deleteAllergen(projectId: Long, name: String, allergen: String) {
        allergenDAO.deleteAllergen(AllergenIdentifierDTO(projectId, name, allergen))

        if (allergenDAO.getAllergenCountByNameAndProjectId(name, projectId) == 0) {
            allergenDAO.deleteAllergenPerson(AllergenPersonIdentifierDTO(projectId, name))
        }
    }

    suspend fun deleteAllergenPerson(person: AllergenPerson, projectId: Long) {
        allergenDAO.deleteAllergenPerson(AllergenPersonIdentifierDTO(projectId, person.name))
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

    suspend fun updateAllergenPersonArrival(projectId: Long, person: AllergenPerson, newDate: Date, newMeal: String) {
        allergenDAO.updateAllergenPerson(
            AllergenPersonEntity(
                person.name,
                projectId,
                newDate,
                newMeal,
                person.departureDate,
                person.departureMeal
            )
        )
    }

    suspend fun updateAllergenPersonDeparture(projectId: Long, person: AllergenPerson, newDate: Date, newMeal: String) {
        allergenDAO.updateAllergenPerson(
            AllergenPersonEntity(
                person.name,
                projectId,
                person.arrivalDate,
                person.arrivalMeal,
                newDate,
                newMeal
            )
        )
    }
}