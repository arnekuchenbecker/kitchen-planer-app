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

package com.scouts.kitchenplaner.ui.view.projectdetails

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.scouts.kitchenplaner.ui.state.ProjectDialogValues
import com.scouts.kitchenplaner.ui.view.SideDrawer
import com.scouts.kitchenplaner.ui.view.SideDrawerItem

@Composable
fun ProjectSettingsSideDrawer(
    modifier: Modifier = Modifier,
    displayDialog: (ProjectDialogValues) -> Unit,
    showSideBar: Boolean
) {
    SideDrawer(
        expand = showSideBar,
        modifier = modifier
    ) {
        Text(
            text = "Einstellungen",
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 8.em,
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.CenterHorizontally)
        )
        HorizontalDivider()
        SideDrawerItem(
            content = {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit project name",
                    modifier = Modifier.padding(end = 15.dp)
                )
                Text(text = "Namen ändern")
            },
            onClick = {
                displayDialog(ProjectDialogValues.NAME_CHANGE)
            }
        )
        SideDrawerItem(
            content = {
                Icon(
                    imageVector = Icons.Filled.ImageSearch,
                    contentDescription = "Edit project image",
                    modifier = Modifier.padding(end = 15.dp)
                )
                Text(text = "Projekt-Bild ändern")
            },
            onClick = {
                displayDialog(ProjectDialogValues.IMAGE_CHANGE)
            }
        )
        SideDrawerItem(
            content = {
                Icon(
                    imageVector = Icons.Filled.EditCalendar,
                    contentDescription = "Edit start / end date",
                    modifier = Modifier.padding(end = 15.dp)
                )
                Text(text = "Start-/Enddatum")
            },
            onClick = {
                displayDialog(ProjectDialogValues.DATE_CHANGE)
            }
        )
        SideDrawerItem(
            content = {
                Icon(
                    imageVector = Icons.Filled.SwapHoriz,
                    contentDescription = "Edit arrivals and departures",
                    modifier = Modifier.padding(end = 15.dp)
                )
                Text(text = "Ankunft / Abfahrt")
            },
            onClick = {
                displayDialog(ProjectDialogValues.NUMBER_CHANGE)
            }
        )
        SideDrawerItem(
            content = {
                Icon(
                    imageVector = Icons.Filled.MedicalInformation,
                    contentDescription = "Edit Allergens",
                    modifier = Modifier.padding(end = 15.dp)
                )
                Text(text = "Allergene")
            },
            onClick = {
                displayDialog(ProjectDialogValues.ALLERGENS)
            }
        )
        SideDrawerItem(
            content = {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Invite other people",
                    modifier = Modifier.padding(end = 15.dp)
                )
                Text(text = "Teilen")
            },
            onClick = {
                displayDialog(ProjectDialogValues.INVITE)
            }
        )
    }
}