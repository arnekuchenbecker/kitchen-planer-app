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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.toMutableStateMap
import com.scouts.kitchenplaner.ui.state.ProjectDialogValues
import com.scouts.kitchenplaner.ui.state.ProjectDialogsState

@Composable
fun ProjectSettingsDialogs(
    state: ProjectDialogsState
) {
    when (state.displayDialog) {
        ProjectDialogValues.NAME_CHANGE -> NameChangeDialog(
            onDismissRequest = { state.displayDialog = ProjectDialogValues.NONE },
            onNameChange = state.onNameChange
        )
        ProjectDialogValues.IMAGE_CHANGE -> ImageChangeDialog(
            onDismissRequest = { state.displayDialog = ProjectDialogValues.NONE },
            onImageChange = state.onPictureChange,
            currentImage = state.currentImage
        )
        ProjectDialogValues.DATE_CHANGE -> DateChangeDialog(
            onDismissRequest = { state.displayDialog = ProjectDialogValues.NONE },
            onDateChange = state.onDateChange,
            startDate = state.startDate,
            endDate = state.endDate
        )
        ProjectDialogValues.NUMBER_CHANGE -> NumberChangeDialog(
            onDismissRequest = { state.displayDialog = ProjectDialogValues.NONE },
            onConfirm = state.onNumbersChange,
            presentPersons = state.mealSlots.map { Pair(it, state.mealPlan[it].second) }.toMutableStateMap(),
            mealSlots = state.mealSlots
        )
        ProjectDialogValues.INVITE -> InvitationDialog(
            onDismissRequest = { state.displayDialog = ProjectDialogValues.NONE },
            projectPublished = state.projectPublished,
            projectId = state.projectId
        )
        ProjectDialogValues.NONE -> Unit // Nothing to display here
    }
}