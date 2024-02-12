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

package com.scouts.kitchenplaner.ui.view.createrecipe

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scouts.kitchenplaner.ui.view.NumberFieldType
import com.scouts.kitchenplaner.ui.view.OutlinedNumberField
import com.scouts.kitchenplaner.ui.view.PicturePicker
import com.scouts.kitchenplaner.ui.viewmodel.CreateRecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecipe(
    onNavigationToRecipeDetails: (Long) -> Unit,
    viewModel: CreateRecipeViewModel = hiltViewModel()
) {
    val navigateID by viewModel.navigateFlow.collectAsState()
    var showImportDialog by remember { mutableStateOf(false) }
    val pattern = remember { Regex("^\\d*\$") }

    navigateID?.let {
        onNavigationToRecipeDetails(it)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                title = {
                    Text("Create a New Recipe")
                },
                actions = {
                    IconButton(onClick = { showImportDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.ImportExport,
                            contentDescription = "Import Recipe from chefkoch.de"
                        )
                    }
                }
            )

        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.createRecipe() },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Create Recipe"
                    )
                },
                text = { Text("Fertig") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            val context = LocalContext.current
            val columnItemModifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
            PicturePicker(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1.0f)
                    .align(Alignment.CenterHorizontally),
                onPathSelected = {
                    context.contentResolver.takePersistableUriPermission(
                        it!!,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    viewModel.uri = it
                },
                path = viewModel.uri
            )

            TextField(
                value = viewModel.recipeName,
                onValueChange = { viewModel.recipeName = it },
                singleLine = true,
                label = { Text("Rezept Name") },
                modifier = columnItemModifier
            )

            OutlinedNumberField(
                value = viewModel.calculatedFor,
                onValueChange = {
                    if (it.isEmpty() || it.matches(pattern)) {
                        viewModel.calculatedFor = it
                    } else {
                        println("Didn't match regex :(")
                    }
                },
                label = { Text("Anzahl Personen") },
                modifier = columnItemModifier,
                type = NumberFieldType.POSITIVE
            )

            OutlinedTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                label = { Text("Beschreibung") },
                modifier = columnItemModifier
            )

            IngredientsInput(
                modifier = columnItemModifier,
                ingredientGroups = viewModel.ingredients,
                onGroupAdd = viewModel::addIngredientGroup,
                onIngredientAdd = viewModel::addIngredient,
                onIngredientDelete = viewModel::deleteIngredient,
                onDeleteIngredientGroup = viewModel::deleteGroup
            )

            InstructionInput(
                modifier = columnItemModifier,
                instructions = viewModel.instructions,
                onAddInstruction = { instruction, index ->
                    if (index == null) {
                        viewModel.instructions.add(instruction)
                    } else {
                        viewModel.instructions.add(index, instruction)
                    }
                }
            )

            AllergenInput(
                modifier = columnItemModifier,
                allergens = viewModel.allergenState
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (showImportDialog) {
        RecipeImportDialog(
            onDismissRequest = { showImportDialog = false },
            importRecipe = { viewModel.importRecipe(it) }
        )
    }
}
