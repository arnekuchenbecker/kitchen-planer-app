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

package com.scouts.kitchenplaner.networklayer.kitchenplaner.dtos.projects

import java.util.Date

/**
 * DTO for communication with the server. Represents an allergic person.
 *
 * @param name The name of the person
 * @param arrivalDate The date the person arrives
 * @param departureDate The date the person departs
 * @param arrivalMeal The first meal of the person
 * @param departureMeal The last meal of the person
 * @param allergen Everything the person is allergic to
 * @param traces Everything the person is allergic to traces
 */
data class ServerAllergenPeopleDTO(
    val name: String,
    val arrivalDate: Date,
    val departureDate: Date,
    val arrivalMeal: String,
    val departureMeal: String,
    val allergen: List<String>,
    val traces: List<String>
)
