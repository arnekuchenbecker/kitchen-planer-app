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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scouts.kitchenplaner.ui.view.InvitationElements

@Composable
fun InvitationDialog(
    onDismissRequest: () -> Unit,
    projectPublished: Boolean,
    projectId: Long
) {
    var published by remember { mutableStateOf(projectPublished) }
    SettingDialog(
        onDismissRequest = onDismissRequest,
        title = "Weiter Personen einladen",
        onConfirm = { /*TODO Check project's published status*/ }
    ) {
        Row (verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = published, onCheckedChange = { published = it })
            Text(text = "Projekt teilen", modifier = Modifier.padding(start = 15.dp))
        }

        if (published) {
            InvitationElements(projectId = projectId)
        }
    }
}