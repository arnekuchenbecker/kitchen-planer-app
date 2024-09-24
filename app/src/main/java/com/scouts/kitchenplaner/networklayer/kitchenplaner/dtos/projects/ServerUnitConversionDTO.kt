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
 * DTO for communication with the server. Represents a unit conversion
 *
 * @param startUnit The unit from which to convert
 * @param endUnit The unit to which to convert
 * @param ingredient Regex identifying the ingredients this unit conversion can be applied to.
 * @param factor The factor by which to multiply the amount of an ingredient in order to convert
 *               from startUnit to endUnit
 */
data class ServerUnitConversionDTO(
    val startUnit: String,
    val endUnit: String,
    val ingredient: String,
    val factor: Double
)