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

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import com.scouts.kitchenplaner.ui.view.NumberFieldType
import com.scouts.kitchenplaner.ui.view.OutlinedNumberField
import com.scouts.kitchenplaner.ui.view.PicturePicker
import com.scouts.kitchenplaner.ui.view.recipes.IngredientsInput
import com.scouts.kitchenplaner.ui.view.recipes.InstructionInput
import com.scouts.kitchenplaner.ui.viewmodel.editRecipe.EditRecipeViewModel


/**
 * Screen that shows a recipe and provide options to edit this.
 * Note that the changes have to be saved by a click on the respective button and
 * they also can be depraved.
 *
 * @param recipeID The recipe to be displayed
 * @param viewModel The view model that provides data and saves the changes
 */
@Composable
fun RecipeDetails(
    recipeID: Long, viewModel: EditRecipeViewModel = hiltViewModel()
) {
    var recipeInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = null) {
        viewModel.getRecipe(recipeID)
        recipeInitialized = true
    }
    if (recipeInitialized) {
        val recipe by viewModel.recipeFlow.collectAsState()

        BackHandler(enabled = viewModel.isEditable()) {
            viewModel.deactivateEditMode()
        }
        Scaffold(topBar = {
            EditableHeader(titleField = {
                if (viewModel.isEditable()) {
                    TextField(
                        value = viewModel.state.name,
                        onValueChange = { viewModel.setRecipeName(it) },
                        singleLine = true
                    )

                } else {
                    Text(recipe.name)
                }
            }, buttonClick = {
                if (!viewModel.isEditable()) {
                    viewModel.activateEditMode(recipe)
                } else {
                    viewModel.deactivateEditMode()
                }
            }, buttonImage = {
                if (viewModel.isEditable()) {
                    Icon(
                        imageVector = Icons.Filled.Close, contentDescription = "dismiss changes"
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Edit, contentDescription = "Edit recipe"
                    )
                }
            })
        }, floatingActionButton = {
            if (viewModel.isEditable()) {
                ExtendedFloatingActionButton(onClick = {
                    viewModel.saveChangesAndDeactivateEditMode()
                }, text = { Text("Speichern") }, icon = {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = "save changes")
                })
            }
        }) { paddingValues ->

            Column(
                modifier = Modifier
                    .verticalScroll(state = rememberScrollState())
                    .padding(paddingValues)
            ) {
                Row(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        if ((recipe.imageURI != Uri.EMPTY && !viewModel.isEditable()) || viewModel.isEditable()) {
                            Modifier.fillMaxWidth(0.5f)
                        } else {
                            Modifier
                        }
                    ) {
                        if (viewModel.isEditable()) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Für ")
                                OutlinedNumberField(
                                    modifier = Modifier.fillMaxWidth(0.3f),
                                    value = if (viewModel.state.amount == 0) {
                                        ""
                                    } else {
                                        viewModel.state.amount.toString()
                                    },

                                    onValueChange = {
                                        if (it.isEmpty()) {
                                            viewModel.setAmountOfPeople(0)
                                        } else {
                                            viewModel.setAmountOfPeople(it.toInt())
                                        }
                                    },
                                    label = { Text("") },
                                    type = NumberFieldType.POSITIVE
                                )
                                if (viewModel.state.amount > 1) {
                                    Text(" Personen")

                                } else {
                                    Text(" Person")
                                }
                            }
                        } else {
                            Text(
                                "Für " + recipe.numberOfPeople + if (recipe.numberOfPeople > 1) {
                                    " Personen"
                                } else {
                                    " Person"
                                },
                                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                            )
                        }

                    }
                    if (viewModel.isEditable()) {
                        PicturePicker(onPathSelected = { uri ->
                            if (uri != null) {
                                viewModel.setRecipePicture(uri)
                            }
                        }, path = viewModel.state.imageURI)
                    } else {
                        if (recipe.imageURI != Uri.EMPTY) {
                            AsyncImage(
                                model = recipe.imageURI,
                                contentDescription = "Recipe picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }


                }
                HorizontalDivider()
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = if (viewModel.isEditable()) {
                        viewModel.state.description
                    } else {
                        recipe.description
                    },
                    readOnly = !viewModel.isEditable(),
                    onValueChange = { viewModel.setRecipeDescription(it) },
                )

                ContentBox(title = "Allergene", modifier = Modifier.padding(10.dp)) {
                    Column {
                        ExpandableCard(expanded = viewModel.expandedFreeOf, onCardArrowClick = {
                            viewModel.expandedFreeOf = !viewModel.expandedFreeOf
                        }, onTitleClick = { }, cardState = CardState(
                            title = "Frei von: ",
                            onDelete = {},
                            toBeDeleted = false,
                            contentModifier = Modifier.heightIn(max = 500.dp)
                        ) {
                            DisplayDietarySpecialityList(
                                editable = { viewModel.isEditable() },
                                allergenList = if (viewModel.isEditable()) {
                                    viewModel.state.freeOf
                                } else {
                                    recipe.freeOfAllergen
                                },
                                add = { allergen ->
                                    viewModel.addDietarySpeciality(
                                        DietarySpeciality(
                                            allergen, DietaryTypes.FREE_OF
                                        )
                                    )
                                },
                                delete = { allergen ->
                                    viewModel.deleteDietarySpeciality(
                                        DietarySpeciality(
                                            allergen, DietaryTypes.FREE_OF
                                        )
                                    )
                                })
                        })
                        ExpandableCard(expanded = viewModel.expandedAllergen, onCardArrowClick = {
                            viewModel.expandedAllergen = !viewModel.expandedAllergen
                        }, onTitleClick = { }, cardState = CardState(
                            title = "Enthält: ",
                            onDelete = {},
                            toBeDeleted = false,
                            contentModifier = Modifier.heightIn(max = 500.dp)

                        ) {
                            DisplayDietarySpecialityList(
                                editable = viewModel::isEditable,
                                allergenList = if (viewModel.isEditable()) {
                                    viewModel.state.allergens
                                } else {
                                    recipe.allergens
                                },
                                add = { allergen ->
                                    viewModel.addDietarySpeciality(
                                        DietarySpeciality(
                                            allergen, DietaryTypes.ALLERGEN
                                        )
                                    )
                                },
                                delete = { allergen ->
                                    viewModel.deleteDietarySpeciality(
                                        DietarySpeciality(
                                            allergen, DietaryTypes.ALLERGEN
                                        )
                                    )
                                })
                        })
                        ExpandableCard(expanded = viewModel.expandedTraces, onCardArrowClick = {
                            viewModel.expandedTraces = !viewModel.expandedTraces
                        }, onTitleClick = { }, cardState = CardState(
                            title = "Enthält Spuren von: ",
                            onDelete = {},
                            toBeDeleted = false,
                            contentModifier = Modifier.heightIn(max = 500.dp)

                        ) {
                            DisplayDietarySpecialityList(
                                editable = { viewModel.isEditable() },
                                allergenList = if (viewModel.isEditable()) {
                                    viewModel.state.traces
                                } else {
                                    recipe.traces
                                },
                                add = { allergen ->
                                    viewModel.addDietarySpeciality(
                                        DietarySpeciality(
                                            allergen, DietaryTypes.TRACE
                                        )
                                    )
                                },
                                delete = { allergen ->
                                    viewModel.deleteDietarySpeciality(
                                        DietarySpeciality(
                                            allergen, DietaryTypes.TRACE
                                        )
                                    )
                                })
                        })
                    }
                }
                IngredientsInput(modifier = Modifier.padding(10.dp),
                    ingredientGroups = if (viewModel.isEditable()) {
                        viewModel.state.ingredients
                    } else {
                        recipe.ingredientGroups
                    },
                    editable = viewModel.isEditable(),
                    onGroupAdd = { viewModel.addIngredient(it) },
                    onIngredientDelete = { group, ingredient ->
                        viewModel.deleteIngredient(
                            group,
                            ingredient
                        )
                    },
                    onIngredientAdd = { group, ingredient ->
                        viewModel.addIngredient(
                            group = group, ingredient = ingredient
                        )
                    },
                    onDeleteIngredientGroup = { viewModel.deleteIngredient(it) },
                    onAlterIngredient = { group, ingredient, newName, newAmount, newUnit ->
                        viewModel.editIngredient(
                            group = IngredientGroup(group),
                            ingredient = ingredient,
                            newName = newName,
                            newAmount = newAmount,
                            newUnit = newUnit
                        )
                    }
                )
                InstructionInput(modifier = Modifier.padding(10.dp),
                    instructions = if (viewModel.isEditable()) {
                        viewModel.state.instructions
                    } else {
                        recipe.instructions
                    },
                    editable = viewModel.isEditable(),
                    onAddInstruction = { instruction, index ->
                        if (index == null) {
                            viewModel.addInstructionStep(
                                step = instruction,
                                index = 0
                            )
                        } else {
                            viewModel.addInstructionStep(
                                step = instruction,
                                index = index
                            )

                        }
                    },
                    onDeleteInstruction = { index ->
                        viewModel.removeInstructionStep(index)
                    },
                    onAlterInstruction = { index, instruction ->
                        viewModel.updateInstructionStep(
                            index, instruction
                        )
                    })

                //To allow scrolling stuff from behind the FAB
                if (viewModel.isEditable()) {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }
    }
}