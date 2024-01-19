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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.scouts.kitchenplaner.model.entities.ProjectStub
import com.scouts.kitchenplaner.ui.viewmodel.ProjectSelectionViewModel

@Composable
fun archiveDialog(
    viewModel: ProjectSelectionViewModel,
    projects: List<Pair<ProjectStub, Boolean>>
) {

    fun closeDialog() {
        viewModel.showArchiveDialog = false
        viewModel.archive = false
        viewModel.clearSelection()
    }

    Dialog(onDismissRequest = { viewModel.showArchiveDialog = false }
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text("geteilte Projekte werden so vom Gerät entfernt. \nEs ist möglich diese wieder zu laden. \nNicht geteilte Projekte werden gelöscht und können nicht wieder hergestellt werden.")
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = { /*TODO delete selected projects*/
                        projects.forEach { (project, selected) ->
                            if (selected) {
                                println(project.name)
                            }
                        }
                        closeDialog()
                    }) { Text("OK") }
                    Button(onClick = { closeDialog() }
                    ) { Text("Abbrechen") }
                }
            }
        }
    }
}