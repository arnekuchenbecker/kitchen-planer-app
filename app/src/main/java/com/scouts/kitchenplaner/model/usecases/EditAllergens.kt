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

/**
 * Use case for handling the allergens and allergen persons within a project.
 * It is possible to add and remove allergens of a person with allergens and edit them.
 *
 * @param allergenRepository The repository to persist those changes in the data base
 */
class EditAllergens @Inject constructor(
    private val allergenRepository: AllergenRepository
) {
    /**
     * Adds a new allergen to a person who already has allergens
     *
     * @param project The project in which the person is relevant
     * @param person The person who gets a new allergen
     * @param allergen The new allergen for the person
     */
    suspend fun addAllergenToPerson(project: Project, person: AllergenPerson, allergen: Allergen) {
        allergenRepository.addAllergen(person.name, project.id, allergen)
    }

    /**
     * Removes an allergen from a person. If the last allergen gets deleted, the allergen person also gets deleted.
     *
     * @param project The project in which the allergen person is relevant
     * @param person The person from who an allergen gets deleted
     * @param allergen The allergen that should be removed
     */
    suspend fun removeAllergenFromPerson(project: Project, person: AllergenPerson, allergen: Allergen) {
        allergenRepository.deleteAllergen(project.id, person.name, allergen.allergen)
    }

    /**
     * Persists a new allergen person
     *
     * @param project The project the allergen person is relevant for
     * @param person The new person to be added
     */
    suspend fun addAllergenPerson(project: Project, person: AllergenPerson) {
        allergenRepository.addAllergenPerson(person, project.id)
    }

    /**
     * Removes an allergen person from the project
     *
     * @param project The project from which the person is removed
     * @param person The person to remove
     */
    suspend fun removeAllergenPerson(project: Project, person: AllergenPerson) {
        allergenRepository.deleteAllergenPerson(person, project.id)
    }

    /**
     * Updates the arrival time of an already existing allergen person
     *
     * @param project The project the person is relevant for
     * @param person The person who's start time is updated
     * @param newDate The new date the person arrives
     * @param newMeal The new meal that is the first meal they have
     */
    suspend fun updateArrival(project: Project, person: AllergenPerson, newDate: Date, newMeal: String) {
        allergenRepository.updateAllergenPersonArrival(project.id, person, newDate, newMeal)
    }

    /**
     * Updates the departure time of an already existing allergen person
     *
     * @param project The project the person is relevant for
     * @param person The person who's departure time is updated
     * @param newDate The date on which the first meal is, the person is not present anymore
     * @param newMeal The first meal the person is not present anymore
     */
    suspend fun updateDeparture(project: Project, person: AllergenPerson, newDate: Date, newMeal: String) {
        allergenRepository.updateAllergenPersonDeparture(project.id, person, newDate, newMeal)
    }
}