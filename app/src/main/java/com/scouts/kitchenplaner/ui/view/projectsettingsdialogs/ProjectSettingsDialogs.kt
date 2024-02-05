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

package com.scouts.kitchenplaner.ui.view

import androidx.compose.runtime.Composable
import com.scouts.kitchenplaner.ui.state.ProjectDialogValues
import com.scouts.kitchenplaner.ui.state.ProjectDialogsState
import com.scouts.kitchenplaner.ui.view.projectsettingsdialogs.NameChangeDialog

@Composable
fun ProjectSettingsDialogs(
    state: ProjectDialogsState
) {
    when (state.displayDialog) {
        ProjectDialogValues.NAME_CHANGE -> NameChangeDialog(
            onDismissRequest = { state.displayDialog = ProjectDialogValues.NONE },
            onNameChange = state.onNameChange
        )
        ProjectDialogValues.IMAGE_CHANGE -> println(/*TODO*/)
        ProjectDialogValues.DATE_CHANGE -> println(/*TODO*/)
        ProjectDialogValues.NUMBER_CHANGE -> println(/*TODO*/)
        ProjectDialogValues.INVITE -> println(/*TODO*/)
        ProjectDialogValues.NONE -> Unit // Nothing to display here
    }
}