/*
 * KitchenPlanerApp is the android app frontend for the KitchenPlaner, a tool
 * to cooperatively plan a meal plan for a campout.
 * Copyright (C) 2023-2024 Arne Kuchenbecker, Antonia Heiming
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
        ProjectDialogValues.NONE -> Unit // Nothing to display here
    }
}