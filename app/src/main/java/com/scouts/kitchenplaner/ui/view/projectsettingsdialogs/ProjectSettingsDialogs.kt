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

package com.scouts.kitchenplaner.ui.view.projectsettingsdialogs

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.toMutableStateMap
import com.scouts.kitchenplaner.model.entities.Allergen
import com.scouts.kitchenplaner.model.entities.AllergenPerson
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.ui.state.ProjectDialogValues
import java.util.Date

/**
 * Wrapper for the different project settings dialogs. Displays different dialogs depending on the
 * [displayDialog] parameter.
 *
 * @param displayDialog Which dialog to display (or ProjectSettingsDialogs.NONE to not display
 *                      any)
 * @param onDismissRequest Callback function for closing the currently displayed dialog
 * @param onNameChange Callback function for changing a project's name
 * @param onPictureChange Callback function for change a project's image
 * @param onDateChange Callback function for changing a project's start and end date
 * @param onNumbersChange Callback function for changing arrivals and departures
 * @param onRemovePerson Callback function for removing an allergen person from the project
 * @param onRemoveAllergen Callback function for removing an allergen from a person
 * @param onAddAllergenPerson Callback function for adding a new allergen person to a project
 * @param onMealAdd Callback function for adding a meal at the specified index to a project
 * @param onMealRemove Callback function for removing a meal from a project
 * @param project The project the settings dialogs are displayed for
 */
@Composable
fun ProjectSettingsDialogs(
    displayDialog: ProjectDialogValues,
    onDismissRequest: () -> Unit,
    onNameChange: (String) -> Unit,
    onPictureChange: (Uri) -> Unit,
    onDateChange: (Date, Date) -> Unit,
    onNumbersChange: (Map<MealSlot, Int>) -> Unit,
    onRemovePerson: (AllergenPerson) -> Unit,
    onRemoveAllergen: (AllergenPerson, Allergen) -> Unit,
    onAddAllergenPerson: (AllergenPerson) -> Unit,
    onMealAdd: (String, Int) -> Unit,
    onMealRemove: (String) -> Unit,
    project: Project
) {
    when (displayDialog) {
        ProjectDialogValues.NAME_CHANGE -> NameChangeDialog(
            onDismissRequest = onDismissRequest,
            onNameChange = onNameChange
        )
        ProjectDialogValues.IMAGE_CHANGE -> ImageChangeDialog(
            onDismissRequest = onDismissRequest,
            onImageChange = onPictureChange,
            currentImage = project.projectImage
        )
        ProjectDialogValues.DATE_CHANGE -> DateChangeDialog(
            onDismissRequest = onDismissRequest,
            onDateChange = onDateChange,
            startDate = project.startDate,
            endDate = project.endDate
        )
        ProjectDialogValues.NUMBER_CHANGE -> NumberChangeDialog(
            onDismissRequest = onDismissRequest,
            onConfirm = onNumbersChange,
            presentPersons = project.mealSlots.map { Pair(it, project.mealPlan[it].second) }.toMutableStateMap(),
            mealSlots = project.mealSlots
        )
        ProjectDialogValues.INVITE -> InvitationDialog(
            onDismissRequest = onDismissRequest,
            projectPublished = false, // TODO - replace with actual value
            projectId = project.id
        )
        ProjectDialogValues.ALLERGENS -> EditAllergenPersonsDialog (
            onDismissRequest = onDismissRequest,
            onRemovePerson = onRemovePerson,
            onRemoveAllergen = onRemoveAllergen,
            allergenPersons = project.allergenPersons,
            onAddAllergenPerson = onAddAllergenPerson
        )
        ProjectDialogValues.MEALS -> MealChangeDialog(
            onDismissRequest = onDismissRequest,
            meals = project.meals,
            onMealAdd = onMealAdd,
            onMealRemove = onMealRemove
        )
        ProjectDialogValues.NONE -> Unit // Nothing to display here
    }
}