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

package com.scouts.kitchenplaner.ui.state

/**
 * Enum class for choosing a project settings dialog
 */
enum class ProjectDialogValues {
    /**
     * Display no dialog
     */
    NONE,

    /**
     * Display the name change dialog
     */
    NAME_CHANGE,

    /**
     * Display the image change dialog
     */
    IMAGE_CHANGE,

    /**
     * Display the date change dialog
     */
    DATE_CHANGE,

    /**
     * Display the number change dialog
     */
    NUMBER_CHANGE,

    /**
     * Display the dialog for inviting people to the project
     */
    INVITE,

    /**
     * Display the dialog for editing intolerant persons
     */
    ALLERGENS,

    /**
     * Display the dialog for editing meals
     */
    MEALS
}