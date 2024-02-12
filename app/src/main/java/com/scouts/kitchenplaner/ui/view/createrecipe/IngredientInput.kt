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

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.scouts.kitchenplaner.model.entities.Ingredient
import com.scouts.kitchenplaner.ui.Headline
import com.scouts.kitchenplaner.ui.view.NumberFieldType
import com.scouts.kitchenplaner.ui.view.OutlinedNumberField

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
        Headline(text = "Zutaten")
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
        IngredientAdderDialog(
            onDismissRequest = { addIngredientToGroup = "" },
            onIngredientAdd = { onIngredientAdd(addIngredientToGroup, it) }
        )
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

@Composable
fun IngredientAdderDialog(
    onDismissRequest: () -> Unit,
    onIngredientAdd: (Ingredient) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismissRequest) {
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
                            Ingredient(name, amount.toFloatOrNull() ?: 0f, unit)
                        )
                        name = ""
                        amount = ""
                        unit = ""
                        onDismissRequest()
                    }
                ) {
                    Text(text = "Zutat hinzufügen")
                }
            }
        }
    }
}