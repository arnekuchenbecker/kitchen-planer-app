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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.scouts.kitchenplaner.ui.view.ContentBox
import com.scouts.kitchenplaner.ui.view.NumberFieldType
import com.scouts.kitchenplaner.ui.view.OutlinedNumberField

/**
 * UI elements for adding instructions when creating a new recipe
 *
 * @param modifier Any composable modifiers that should be applied to this Composable
 * @param instructions The instructions added so far
 * @param onAddInstruction Callback function for adding a new instruction at the specified index (or
 *                         at the end if Null is passed)
 */
@Composable
fun InstructionInput(
    modifier: Modifier = Modifier,
    instructions: List<String>,
    onAddInstruction: (String, Int?) -> Unit = { _, _ -> },
    editable: Boolean = true
) {
    var showAddInstructionDialog by remember { mutableStateOf(false) }
    ContentBox(
        title = "Kochanweisungen",
        modifier = modifier
    ) {
        if (editable) {
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
        }
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
        InstructionAdderDialog(
            onDismissRequest = { showAddInstructionDialog = false },
            onAddInstruction = onAddInstruction
        )
    }
}

/**
 * Dialog for adding a new instruction
 *
 * @param onDismissRequest Callback function for closing the dialog when requested by the user
 * @param onAddInstruction Callback function for adding the new instruction at the specified index
 *                         (or at the end if no index is specified, by passing Null)
 */
@Composable
fun InstructionAdderDialog(
    onDismissRequest: () -> Unit,
    onAddInstruction: (String, Int?) -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest
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
                        onDismissRequest()
                    }
                ) {
                    Text(text = "Anweisung hinzufügen")
                }
            }
        }
    }
}
