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

package com.scouts.kitchenplaner.ui.view.projectoverview

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.scouts.kitchenplaner.model.entities.ProjectStub
import com.scouts.kitchenplaner.ui.view.OverviewField

/**
 * If clicking a project should select it to be archived or trigger navigation to the detailed project screen
 *
 * @param project A stub of the project to be displayed
 * @param selected Whether the project is selected
 * @param onNavigateToDetailedProject action which leads to the detailed screen of the project
 * @param toggleSelection action that toggles the selection of the project
 * @param archive If the selection of a project means to archive it or go to the detailed screen
 */
@Composable
fun ProjectField(
    project: ProjectStub,
    selected: Boolean,
    onNavigateToDetailedProject: (Long) -> Unit,
    toggleSelection: (Long, Boolean) -> Unit,
    archive: Boolean
) {
    OverviewField(onClick = {
        if (!archive) {
            onNavigateToDetailedProject(
                project.id
            )
        } else {
            toggleSelection(project.id, !selected)
        }
    },
        text = project.name,
        imageUri = project.imageUri,
        imageDescription = "Project Image",
        additionalContent = {
            if (archive && selected) {
                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "zu löschendes projekt",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxHeight(0.7f)
                            .aspectRatio(1.0f)

                    )
                }

            }
        })
}