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
 * DTO for communication with the server. Represents a project.
 *
 * @param versionNumber The version number of the data of the project
 * @param imageVersionNumber The version number of the image of the project
 * @param name The name of the project
 * @param id The onlineID of the project
 * @param meals All meals of the project
 * @param startDate The start date of the project
 * @param endDate The end date of the project
 * @param allergenPeople All allergic people relevant to the project
 * @param recipes All (main and alternative) recipe mappings of the project
 * @param unitConversions All unit conversions active for the project
 * @param personNumberChange All person number changes of the project
 */
data class ServerProjectDTO(
    val versionNumber: Long,
    val imageVersionNumber: Long,
    val name: String,
    val id: Long,
    val meals: List<String>,
    val startDate: Date,
    val endDate: Date,
    val allergenPeople: List<ServerAllergenPeopleDTO>,
    val recipes: List<ServerRecipeMappingDTO>,
    val unitConversions: List<ServerUnitConversionDTO>,
    val personNumberChange: List<ServerPersonNumberChangeDTO>
)
