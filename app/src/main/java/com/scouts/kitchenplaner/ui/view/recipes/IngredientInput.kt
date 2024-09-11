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

package com.scouts.kitchenplaner.ui.view.recipes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.scouts.kitchenplaner.model.entities.Ingredient
import com.scouts.kitchenplaner.model.entities.IngredientGroup
import com.scouts.kitchenplaner.ui.view.ContentBox
import com.scouts.kitchenplaner.ui.view.NumberFieldType
import com.scouts.kitchenplaner.ui.view.OutlinedNumberField

/**
 * Display UI elements for editing the ingredients while creating a new recipe
 *
 * @param modifier Any composable modifiers that should be applied to this composable
 * @param ingredientGroups IngredientGroups created so far
 * @param onGroupAdd Callback function for creating a new IngredientGroup
 * @param onIngredientAdd Callback function for adding a new Ingredient (second argument) to the
 *                        IngredientGroup with the specified name (first argument)
 * @param onIngredientDelete Callback function for deleting an Ingredient (second argument) from the
 *                           IngredientGroup with the specified name (first argument)
 * @param onAlterIngredient Callback function to alter an Ingredient (second argument) in
 * an ingredient group (first argument) to set new values: name (third argument), amount (fourth argument) and unit (fifth argument)
 * @param onDeleteIngredientGroup Callback function for deleting an entire IngredientGroup
 * @param editable Whether the ingredients are editable
 */
@Composable
fun IngredientsInput(
    modifier: Modifier = Modifier,
    ingredientGroups: List<IngredientGroup>,
    onGroupAdd: (String) -> Unit = {},
    onIngredientAdd: (String, Ingredient) -> Unit = { _, _ -> },
    onIngredientDelete: (String, Ingredient) -> Unit = { _, _ -> },
    onAlterIngredient: (String, Ingredient, String?, Double?, String?) -> Unit = { _, _, _, _, _ -> },
    onDeleteIngredientGroup: (String) -> Unit = {},
    editable: Boolean = true
) {
    var addIngredientToGroup by remember { mutableStateOf("") }
    var newGroupName by remember { mutableStateOf("") }
    var showIngredientChangeIndex by remember { mutableIntStateOf(-1) }
    var showIngredientChangeGroup by remember { mutableStateOf("") }


    ContentBox(
        title = "Zutaten", modifier = modifier
    ) {
        ingredientGroups.forEach { (name, ingredients) ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = name, fontWeight = FontWeight.Black)
                if (editable) {
                    Spacer(modifier = Modifier.weight(1.0f))
                    IconButton(onClick = {
                        addIngredientToGroup = name
                    }) {
                        Icon(Icons.Filled.Add, "Add ingredient to group")
                    }
                    IconButton(onClick = {
                        onDeleteIngredientGroup(name)
                    }) {
                        Icon(Icons.Filled.Delete, "Delete ingredient group")
                    }
                }
            }
            ingredients.forEachIndexed { index, ingredient ->
                DisplayIngredient(onDeleteClick = { onIngredientDelete(name, ingredient) },
                    ingredient = ingredient,
                    editable = editable,
                    onChangeIngredient = {

                        showIngredientChangeIndex = index
                        showIngredientChangeGroup = name
                    })
                if (index == showIngredientChangeIndex && showIngredientChangeGroup == name) {
                    DisplayIngredientChangeDialog(ingredient = ingredient, onDismissRequest = {
                        showIngredientChangeIndex = -1
                        showIngredientChangeGroup = ""
                    }, onSaveChanges = { newName, newAmount, newUnit ->
                        onAlterIngredient(
                            name, ingredient, newName, newAmount, newUnit
                        )
                    })
                }
            }
            HorizontalDivider(modifier = Modifier.padding(10.dp))
        }

        if (editable) {
            OutlinedTextField(modifier = Modifier.align(Alignment.CenterHorizontally),
                value = newGroupName,
                onValueChange = { newGroupName = it },
                trailingIcon = {
                    IconButton(onClick = {
                        onGroupAdd(newGroupName)
                        newGroupName = ""
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Create new ingredient group"
                        )
                    }
                },
                label = { Text("Gruppe hinzufügen") })
        }
    }

    if (addIngredientToGroup.isNotEmpty()) {
        IngredientAdderDialog(onDismissRequest = { addIngredientToGroup = "" },
            onIngredientAdd = { onIngredientAdd(addIngredientToGroup, it) })
    }
}

/**
 * UI elements for displaying a single ingredient inside an IngredientGroup while creating a new
 * recipe
 *
 * @param onDeleteClick Callback function for deleting the ingredient
 * @param ingredient The ingredient to be displayed
 * @param editable Whether the ingredient is editable
 * @param onChangeIngredient Callback function to signal that the ingredient should be edited
 */
@Composable
fun DisplayIngredient(
    onDeleteClick: () -> Unit = {},
    ingredient: Ingredient,
    editable: Boolean,
    onChangeIngredient: () -> Unit = {}
) {
    Row(
        modifier = Modifier.clickable(enabled = editable) { onChangeIngredient() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = ingredient.name, modifier = Modifier.fillMaxWidth(0.5f))
        Spacer(modifier = Modifier.weight(1.0f))
        Text(
            text = "${ingredient.amount} ${ingredient.unit}",
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth(0.85f)
        )
        if (editable) {
            IconButton(
                onClick = onDeleteClick
            ) {
                Icon(
                    imageVector = Icons.Filled.Close, contentDescription = "Delete ingredient"
                )
            }
        }
    }
}


/**
 * Dialog for adding a new Ingredient
 *
 * @param onDismissRequest Callback function to close the dialog when requested by the user
 * @param onIngredientAdd Callback function for creating the ingredient
 */
@Composable
fun IngredientAdderDialog(
    onDismissRequest: () -> Unit, onIngredientAdd: (Ingredient) -> Unit
) {
    DisplayIngredientChangeDialog(
        ingredient = Ingredient("", 0.0, ""),
        onDismissRequest = onDismissRequest,
        onSaveChanges = { ingredientName, amount, unit ->
            if (ingredientName == null || amount == null || unit == null) return@DisplayIngredientChangeDialog else onIngredientAdd(
                Ingredient(
                    ingredientName, amount, unit
                )
            )
        },
        text = "Zutat hinzufügen"
    )
}

/**
 * Dialog to change values of the  ingredient
 *
 * @param ingredient The ingredient to be changed
 * @param onDismissRequest  Callback function to close the dialog when requested by the user
 * @param onSaveChanges Callback function to save the new values. Return null if the value should not be changed.
 *  the arguments are the following: name, amount, unit
 * @param text The text displayed on the save button
 */
@Composable
fun DisplayIngredientChangeDialog(
    ingredient: Ingredient,
    onDismissRequest: () -> Unit,
    onSaveChanges: (String?, Double?, String?) -> Unit,
    text: String = "Änderung speichern"
) {
    var name by remember { mutableStateOf(ingredient.name) }
    var nameChange by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf(ingredient.amount.toString()) }
    var amountChange by remember { mutableStateOf(false) }
    var unit by remember { mutableStateOf(ingredient.unit) }
    var unitChange by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = RoundedCornerShape(15.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(value = name,
                    onValueChange = {
                        name = it
                        nameChange = true
                    },
                    label = { Text(text = "Zutat") },
                    modifier = Modifier.padding(vertical = 5.dp)
                )

                OutlinedNumberField(value = amount,
                    onValueChange = {
                        amount = it
                        amountChange = true
                    },
                    label = { Text(text = "Menge") },
                    type = NumberFieldType.FLOAT,
                    modifier = Modifier.padding(vertical = 5.dp)
                )

                OutlinedTextField(value = unit,
                    onValueChange = {
                        unit = it
                        unitChange = true
                    },
                    label = { Text(text = "Einheit") },
                    modifier = Modifier.padding(vertical = 5.dp)
                )

                OutlinedButton(modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 5.dp), onClick = {
                    onSaveChanges(
                        if (nameChange) {
                            name
                        } else {
                            null
                        }, if (amountChange) {
                            amount.toDoubleOrNull()
                        } else {
                            null
                        }, if (unitChange) {
                            unit
                        } else {
                            null
                        }
                    )
                    name = ""
                    amount = ""
                    unit = ""
                    onDismissRequest()
                }) {
                    Text(text = text)
                }
            }
        }
    }

}