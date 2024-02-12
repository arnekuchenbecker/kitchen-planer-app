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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.scouts.kitchenplaner.model.entities.Ingredient
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

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (showImportDialog) {
        //TODO
    }
}

@Composable
fun InstructionInput(
    modifier: Modifier = Modifier,
    instructions: List<String>,
    onAddInstruction: (String, Int?) -> Unit
) {
    var showAddInstructionDialog by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
            .padding(15.dp)
    ) {
        Text(
            text = "Kochanweisungen",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Weiteren Schritt hinzufügen...", modifier = Modifier.padding(end = 15.dp))
            OutlinedIconButton(onClick = { showAddInstructionDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Open dialog to add a new instruction"
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
        instructions.forEachIndexed { index, instruction ->
            Row {
                Box(modifier = Modifier.width(30.dp)) {
                    Text("${index + 1}.")
                }
                Text(instruction)
            }
        }
    }

    if (showAddInstructionDialog) {
        Dialog(
            onDismissRequest = { showAddInstructionDialog = false }
        ) {
            var numberFieldText by remember { mutableStateOf("") }
            var instructionText by remember { mutableStateOf("") }
            Surface(shape = RoundedCornerShape(15.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedNumberField(
                        value = numberFieldText,
                        onValueChange = { numberFieldText = it },
                        label = { Text(text = "Einfügen vor Schritt...") },
                        type = NumberFieldType.POSITIVE,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )

                    OutlinedTextField(
                        value = instructionText,
                        onValueChange = { instructionText = it },
                        label = { Text(text = "Anweisungen") },
                        modifier = Modifier.padding(vertical = 5.dp)
                    )

                    OutlinedButton(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 5.dp),
                        onClick = {
                            onAddInstruction(
                                instructionText,
                                numberFieldText.toIntOrNull()?.minus(1)
                            )
                            showAddInstructionDialog = false
                        }
                    ) {
                        Text(text = "Anweisung hinzufügen")
                    }
                }
            }
        }
    }
}

@Composable
fun IngredientsInput(
    modifier: Modifier = Modifier,
    ingredientGroups: Map<String, List<Ingredient>>,
    onGroupAdd: (String) -> Unit,
    onIngredientAdd: (String, Ingredient) -> Unit,
    onIngredientDelete: (String, Ingredient) -> Unit,
    onDeleteIngredientGroup: (String) -> Unit
) {
    println("Displaying ingredients: ${ingredientGroups.size}")
    var addIngredientToGroup by remember { mutableStateOf("") }
    var newGroupName by remember { mutableStateOf("") }
    Column(
        modifier = modifier
            .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
            .padding(15.dp)
    ) {
        Text(
            text = "Zutaten",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp)
        )
        ingredientGroups.forEach { (name, ingredients) ->
            HorizontalDivider(modifier = Modifier.padding(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = name, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.weight(1.0f))
                IconButton(
                    onClick = {
                        addIngredientToGroup = name
                    }
                ) {
                    Icon(Icons.Filled.Add, "Add ingredient to group")
                }
                IconButton(
                    onClick = {
                        onDeleteIngredientGroup(name)
                    }
                ) {
                    Icon(Icons.Filled.Delete, "Delete ingredient group")
                }
            }
            ingredients.forEach {
                DisplayIngredient(onDeleteClick = { onIngredientDelete(name, it) }, ingredient = it)
            }
        }

        OutlinedTextField(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            value = newGroupName,
            onValueChange = { newGroupName = it },
            trailingIcon = {
                IconButton(
                    onClick = {
                        onGroupAdd(newGroupName)
                        newGroupName = ""
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Create new ingredient group"
                    )
                }
            },
            label = { Text("Gruppe hinzufügen") }
        )
    }

    if (addIngredientToGroup.isNotEmpty()) {
        var name by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf("") }
        var unit by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { addIngredientToGroup = "" }) {
            Surface(shape = RoundedCornerShape(15.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(text = "Zutat") },
                        modifier = Modifier.padding(vertical = 5.dp)
                    )

                    OutlinedNumberField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text(text = "Menge") },
                        type = NumberFieldType.FLOAT,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )

                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text(text = "Einheit") },
                        modifier = Modifier.padding(vertical = 5.dp)
                    )

                    OutlinedButton(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 5.dp),
                        onClick = {
                            onIngredientAdd(
                                addIngredientToGroup,
                                Ingredient(name, amount.toFloatOrNull() ?: 0f, unit)
                            )
                            name = ""
                            amount = ""
                            unit = ""
                            addIngredientToGroup = ""
                        }
                    ) {
                        Text(text = "Zutat hinzufügen")
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayIngredient(
    onDeleteClick: () -> Unit,
    ingredient: Ingredient
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = ingredient.name)
        Spacer(modifier = Modifier.weight(1.0f))
        Text(text = "${ingredient.amount} ${ingredient.unit}")
        IconButton(
            onClick = onDeleteClick
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Edit ingredient"
            )
        }
    }
}