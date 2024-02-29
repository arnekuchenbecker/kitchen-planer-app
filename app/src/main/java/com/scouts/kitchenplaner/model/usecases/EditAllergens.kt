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

package com.scouts.kitchenplaner.model.usecases

import com.scouts.kitchenplaner.datalayer.repositories.AllergenRepository
import com.scouts.kitchenplaner.model.entities.Allergen
import com.scouts.kitchenplaner.model.entities.AllergenPerson
import com.scouts.kitchenplaner.model.entities.Project
import java.util.Date
import javax.inject.Inject

class EditAllergens @Inject constructor(
    private val allergenRepository: AllergenRepository
) {
    suspend fun addAllergenToPerson(project: Project, person: AllergenPerson, allergen: Allergen) {
        allergenRepository.addAllergen(person.name, project.id, allergen)
    }

    suspend fun removeAllergenFromPerson(project: Project, person: AllergenPerson, allergen: Allergen) {
        allergenRepository.deleteAllergen(project.id, person.name, allergen.allergen)
    }

    suspend fun addAllergenPerson(project: Project, person: AllergenPerson) {
        allergenRepository.addAllergenPerson(person, project.id)
    }

    suspend fun removeAllergenPerson(project: Project, person: AllergenPerson) {
        allergenRepository.deleteAllergenPerson(person, project.id)
    }

    suspend fun updateArrival(project: Project, person: AllergenPerson, newDate: Date, newMeal: String) {
        allergenRepository.updateAllergenPersonArrival(project.id, person, newDate, newMeal)
    }

    suspend fun updateDeparture(project: Project, person: AllergenPerson, newDate: Date, newMeal: String) {
        allergenRepository.updateAllergenPersonDeparture(project.id, person, newDate, newMeal)
    }
}