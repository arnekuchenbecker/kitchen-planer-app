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

package com.scouts.kitchenplaner.ui.view.createproject

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scouts.kitchenplaner.ui.state.CreateProjectInputState
import com.scouts.kitchenplaner.ui.view.DockedDatePicker
import com.scouts.kitchenplaner.ui.view.PicturePicker
import com.scouts.kitchenplaner.ui.viewmodel.CreateProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProject(createProjectViewModel: CreateProjectViewModel = viewModel()) {
    Scaffold (topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(it)) {
            CreateProjectInput(state = createProjectViewModel.inputState.value, modifier = Modifier.align(Alignment.TopCenter))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectInput(state: CreateProjectInputState, modifier: Modifier = Modifier) {
    Column(modifier = modifier
        .padding(10.dp)
        .fillMaxWidth()
        .verticalScroll(state = rememberScrollState())) {
        val columnItemModifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)

        Row(horizontalArrangement = Arrangement.Center, modifier = columnItemModifier) {
            val context = LocalContext.current
            PicturePicker(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1.0f),
                onPathSelected = {
                    context.contentResolver.takePersistableUriPermission(it!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    state.image = it
                },
                path = state.image
            )
        }

        TextField(
            value = state.name,
            onValueChange = { state.name = it },
            modifier = columnItemModifier
                .height(70.dp),
            label = { Text("Project Name") }
        )

        DockedDatePicker(
            modifier = columnItemModifier
                .height(70.dp),
            dateState = state.startDate,
            label = "Start-Datum:",
            displayText = state.startDateString
        )

        DockedDatePicker(
            modifier = columnItemModifier
                .height(70.dp),
            dateState = state.endDate,
            label = "End-Datum:",
            displayText = state.endDateString
        )

        MealPicker(
            modifier = columnItemModifier,
            onAdd = state::addMeal,
            onRemove = state::removeMeal,
            meals = state.meals
        )

        AllergenPicker(
            modifier = columnItemModifier,
            onAdd = state::addIntolerantPerson,
            onRemove = state::removeIntolerantPerson,
            onRemoveItem = state::removeIntolerancy,
            allergens = state.allergens
        )

        //To allow scrolling stuff from behind the FAB
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewCreateProject() {
    Surface(modifier = Modifier.fillMaxSize()) {
        CreateProject()
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewMealPicker() {
    Surface(modifier = Modifier.height(500.dp)) {
        EditMealsDialog(onDismissRequest = {}, onAdd = {}, onRemove = {}, meals = listOf("Pizza", "Flammkuchen"))
    }
}

@Composable
@Preview(showBackground = true)
fun Test() {
    Text("Test")
}
