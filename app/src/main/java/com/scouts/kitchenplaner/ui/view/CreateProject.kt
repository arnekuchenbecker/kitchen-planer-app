/*
 * KitchenPlanerApp is the android app frontend for the KitchenPlaner, a tool
 * to cooperatively plan a meal plan for a campout.
 * Copyright (C) 2023  Arne Kuchenbecker, Antonia Heiming
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

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scouts.kitchenplaner.ui.state.CreateProjectInputState
import com.scouts.kitchenplaner.ui.viewmodel.CreateProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProject(createProjectViewModel: CreateProjectViewModel = viewModel()) {
    Scaffold (topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                Text("Create a New Project")
            }
        )

    }, floatingActionButton = {
        ExtendedFloatingActionButton(onClick = { createProjectViewModel.onProjectCreate() }, icon = {
            Icon(imageVector = Icons.Filled.Check, contentDescription = "Create project")
        }, text = { Text("Fertig") })
    }) {
        CreateProjectInput(state = createProjectViewModel.inputState.value, modifier = Modifier.padding(it))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectInput(state: CreateProjectInputState, modifier: Modifier = Modifier) {
    var displayStarterPicker by remember { mutableStateOf(false) }
    Column(modifier = modifier
        .padding(5.dp)
        .fillMaxWidth()) {
        TextField(value = state.name, onValueChange = {
            state.name = it
        }, modifier = Modifier.fillMaxWidth(0.8f),
            label = { Text("Project Name") }
        )
        Row (modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)) {
            Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.fillMaxHeight().padding(5.dp, 0.dp)) {
                Text("Start Datum:")
            }
            Text(state.startDateString, modifier = Modifier
                .clickable {
                    displayStarterPicker = true
                }
                .border(2.dp, MaterialTheme.colorScheme.primary))
        }

        if (displayStarterPicker) {
            DatePickerDialog(onDismissRequest = {displayStarterPicker = false}, confirmButton = {
                Button(onClick = {
                    println(state.startDate.selectedDateMillis)
                    displayStarterPicker = false
                }) {
                    Icon(Icons.Filled.Check, "Choose Date Range")
                }
            }) {
                DatePicker(state = state.startDate)
            }
        }
    }
}
