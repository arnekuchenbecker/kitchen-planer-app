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

@Composable
fun ProjectField(
    project: ProjectStub,
    selected: Boolean,
    onNavigateToDetailedProject: (Long) -> Unit,
    toggleSelection: (Long, Boolean) -> Unit,
    archive: Boolean
) {
    OverviewField(
        onClick = {
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
        additional =
        {
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
        }
    )


    /*
        Box(modifier = Modifier
            .clickable {
                if (!archive) {
                    onNavigateToDetailedProject(
                        project.id
                    )
                } else {
                    toggleSelection(project.id, !selected)
                }
            }
            .fillMaxWidth()
            .padding(5.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(6.dp)
            )
            .height(75.dp)

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 5.dp, end = 15.dp)
            ) {
                if (project.imageUri == Uri.EMPTY) {
                    Icon(
                        modifier = Modifier
                            .fillMaxHeight(0.9f)
                            .aspectRatio(1.0f)
                            .padding(start = 5.dp),
                        imageVector = Icons.Filled.HideImage,
                        contentDescription = "Projektplatzhalter"
                    )
                } else {
                    AsyncImage(
                        model = project.imageUri,
                        contentDescription = "Project Image",
                        modifier = Modifier
                            .fillMaxHeight(0.85f)
                            .aspectRatio(1.0f)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                }
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = project.name,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                if (archive && selected) {
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

        } */
}