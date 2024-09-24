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

/**
 * DTO for communication with the server. Represents the meta data of a project.
 *
 * @param id The onlineID of the project
 * @param name The name of the project
 * @param imageVersion The version number of the project's image
 * @param projectVersion The version number of the project's data
 */
data class ServerProjectStubDTO(
    val id: Long,
    val name: String,
    val imageVersion: Long,
    val projectVersion: Long
)