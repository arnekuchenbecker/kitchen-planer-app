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

package com.scouts.kitchenplaner.ui.view.recipedetails

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.scouts.kitchenplaner.model.entities.DietarySpeciality
import com.scouts.kitchenplaner.model.entities.DietaryTypes
import com.scouts.kitchenplaner.model.entities.IngredientGroup
import com.scouts.kitchenplaner.ui.view.CardState
import com.scouts.kitchenplaner.ui.view.ContentBox
import com.scouts.kitchenplaner.ui.view.EditableHeader
import com.scouts.kitchenplaner.ui.view.ExpandableCard
import com.scouts.kitchenplaner.ui.view.LazyColumnWrapper
import com.scouts.kitchenplaner.ui.view.NumberFieldType
import com.scouts.kitchenplaner.ui.view.OutlinedNumberField
import com.scouts.kitchenplaner.ui.view.PicturePicker
import com.scouts.kitchenplaner.ui.view.recipes.IngredientsInput
import com.scouts.kitchenplaner.ui.view.recipes.InstructionInput
import com.scouts.kitchenplaner.ui.viewmodel.EditRecipeViewModel


@Composable
fun RecipeDetails(
    recipeID: Long, viewModel: EditRecipeViewModel = hiltViewModel()
) {
//TODO: add to database when not in focus
    var recipeInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = null) {
        viewModel.getRecipe(recipeID)
        recipeInitialized = true
    }
    if (recipeInitialized) {
        val recipe by viewModel.recipeFlow.collectAsState()

        Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) {
            EditableHeader(titleField = {
                if (viewModel.isEditable()) {
                    TextField(
                        value = recipe.name,
                        onValueChange = { viewModel.setRecipeName(recipe, it) })
                } else {
                    Text(recipe.name)
                }
            },
                { viewModel.toggleEditMode() },
                buttonImage = {
                    if (viewModel.isEditable()) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = "save changes")
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit recipe"
                        )
                    }
                })


            Row(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(), verticalAlignment = Alignment.Bottom
            ) {

                Column(Modifier.fillMaxWidth(0.5f)) {
                    if (viewModel.isEditable()) {
                        Row(
                            Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Für ")
                            OutlinedNumberField(
                                modifier = Modifier.fillMaxWidth(0.3f),
                                value = recipe.numberOfPeople.toString(),
                                onValueChange = {
                                    viewModel.setNumberOfPeople(recipe, it.toInt())
                                },
                                label = { },
                                type = NumberFieldType.POSITIVE
                            )
                            Text("Person(en)")
                        }
                    } else {
                        Text(
                            "Für " + recipe.numberOfPeople + " Person(en)",
                            modifier = Modifier
                                .align(alignment = Alignment.CenterHorizontally)
                        )
                    }
                    HorizontalDivider()
                    TextField(
                        value = recipe.description,
                        readOnly = !viewModel.isEditable(),
                        onValueChange = { new ->
                            viewModel.setDescription(
                                recipe,
                                description = new
                            )
                        },
                        maxLines = 4
                    )
                }
                if (viewModel.isEditable()) {
                    PicturePicker(onPathSelected = { uri ->
                        if (uri != null) {
                            viewModel.setRecipePicture(
                                recipe = recipe,
                                uri = uri
                            )
                        }
                    }, path = recipe.imageURI)
                } else {
                    AsyncImage(
                        model = recipe.imageURI,
                        contentDescription = "Recipe picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }


            }

            ContentBox(title = "Allergene", modifier = Modifier.padding(10.dp)) {
                Column {
                    ExpandableCard(
                        expanded = viewModel.expandedFreeOf,
                        onCardArrowClick = { viewModel.expandedFreeOf = !viewModel.expandedFreeOf },
                        onTitleClick = { /*TODO*/ },
                        cardState = CardState(
                            title = "Frei von: ",
                            onDelete = {},
                            toBeDeleted = false,
                            contentModifier = Modifier.heightIn(max = 500.dp)
                        ) {
                            DisplayAllergenLists(
                                editable = { viewModel.isEditable() },
                                allergenList = recipe.freeOfAllergen,
                                add = { allergen ->
                                    viewModel.addDietarySpeciality(
                                        recipe,
                                        DietarySpeciality(allergen = allergen, DietaryTypes.FREE_OF)
                                    )
                                },
                                        delete = { allergen ->
                                    viewModel.deleteDietarySpeciality(
                                        recipe,
                                        DietarySpeciality(
                                            allergen,
                                            DietaryTypes.FREE_OF
                                        )
                                    )
                                })
                        }
                    )
                    ExpandableCard(
                        expanded = viewModel.expandedAllergen,
                        onCardArrowClick = {
                            viewModel.expandedAllergen = !viewModel.expandedAllergen
                        },
                        onTitleClick = { /*TODO*/ },
                        cardState = CardState(
                            title = "Enthält: ",
                            onDelete = {},
                            toBeDeleted = false,
                            contentModifier = Modifier.heightIn(max = 500.dp)

                        ) {
                            DisplayAllergenLists(
                                editable = viewModel::isEditable,
                                allergenList = recipe.allergens,
                                add = { allergen ->
                                    viewModel.addDietarySpeciality(
                                        recipe,
                                        DietarySpeciality(
                                            allergen,
                                            DietaryTypes.ALLERGEN
                                        )
                                    )},
                                delete = { allergen ->
                                    viewModel.deleteDietarySpeciality(
                                        recipe,
                                        DietarySpeciality(
                                            allergen,
                                            DietaryTypes.ALLERGEN
                                        )
                                    )
                                })
                        }
                    )
                    ExpandableCard(
                        expanded = viewModel.expandedTraces,
                        onCardArrowClick = { viewModel.expandedTraces = !viewModel.expandedTraces },
                        onTitleClick = { /*TODO*/ },
                        cardState = CardState(
                            title = "Enthält Spuren von: ",
                            onDelete = {},
                            toBeDeleted = false,
                            contentModifier = Modifier.heightIn(max = 500.dp)

                        ) {
                            DisplayAllergenLists(
                                editable = { viewModel.isEditable() },
                                allergenList = recipe.traces,
                                add = { allergen ->
                                    viewModel.addDietarySpeciality(
                                        recipe,
                                        DietarySpeciality(
                                            allergen,
                                            DietaryTypes.TRACE
                                        )
                                    )},
                                delete = { allergen ->
                                    viewModel.deleteDietarySpeciality(
                                        recipe,
                                        DietarySpeciality(
                                            allergen,
                                            DietaryTypes.TRACE
                                        )
                                    )
                                })
                        })
                }


            }
            IngredientsInput(
                modifier = Modifier.padding(10.dp),
                ingredientGroups = recipe.ingredientGroups,
                editable = viewModel.isEditable(),
                onGroupAdd = { group ->
                    viewModel.addIngredient(
                        recipe,
                        IngredientGroup(group),
                        null
                    )
                },
                onIngredientDelete = { group, ingredient ->
                    viewModel.deleteIngredient(
                        recipe,
                        group = IngredientGroup(group),
                        ingredient = ingredient
                    )
                },
                onDeleteIngredientGroup = { group ->
                    viewModel.deleteIngredient(recipe, IngredientGroup(group))
                }


            )
            InstructionInput(
                modifier = Modifier.padding(10.dp),
                instructions = recipe.instructions,
                editable = viewModel.isEditable(),
                onAddInstruction = { instruction, index ->
                    if (index == null) {
                        viewModel.addInstructionStep(recipe, instruction, 0)
                    } else {
                        viewModel.addInstructionStep(recipe, instruction, index)
                    }
                }
            )

        }
    }
}


@Composable
fun DisplayAllergenLists(
    editable: () -> Boolean,
    allergenList: List<String>,
    add: (String) -> Unit,
    delete: (String) -> Unit,
) {

    var text = "new allergen"
    Column() {
        if (editable()) {
            TextField(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                value = text, onValueChange = { },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            add(text);
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add new allergen"
                        )
                    }
                },
            )
            HorizontalDivider()
        }
        LazyColumnWrapper(
            content = allergenList, DisplayContent = { allergen, _ ->
                if (editable()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(allergen)
                        IconButton(
                            onClick = {
                                delete(allergen)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete Allergen"
                            )
                        }
                    }
                } else {
                    Text(allergen)
                }
            },
            DisplayEmpty = { Text("Noch nichts eingetragen") }
        )
    }
}
