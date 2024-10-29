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

package com.scouts.kitchenplaner.ui.view

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

/**
 * A generic header for the app, which only contains a title
 * @param title title of the section
 */
@Composable
fun Header(title: String) {
    HeaderWithButton(title = title, {}, {});
}

/**
 * Generic header for the app with a button on the right. The button can have a image like an icon
 * @param title title of the section
 * @param buttonClick action, what happens when clicking on the button
 * @param buttonImage image/icon which represents the button.
 */
@Composable
fun HeaderWithButton(
    title: String, buttonClick: () -> Unit, buttonImage: @Composable (() -> Unit)
) {
    EditableHeader(
        titleField = { Text(title) },
        buttonClick = buttonClick,
        buttonImage = buttonImage
    )
}


/**
 *
 * @param titleField A field that is displayed as the title of the section
 * @param buttonClick Action, what happens when clicking the button
 * @param buttonImage Image/Icon which represents the button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableHeader(
    titleField: @Composable () -> Unit,
    buttonClick: () -> Unit,
    buttonImage: @Composable (() -> Unit)
) {
    TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ), title = titleField, actions = {
        if (buttonClick != {}) {
            IconButton(
                colors = IconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ), onClick = buttonClick, content = buttonImage
            )
        }
    })
}