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

package com.scouts.kitchenplaner.model.entities

/**
 * Represents an alternative to a specific recipe, including which DietarySpecialities of the other
 * recipe are covered in the alternative
 *
 * @param id The ID of the alternative recipe
 * @param name The name of the alternative recipe
 * @param coveredAllergens All allergens of the original recipe that are covered by the alternative
 *                         recipe
 */
data class RecipeAlternative(
    val id: Long,
    val name: String,
    val coveredAllergens: List<DietarySpeciality>
)