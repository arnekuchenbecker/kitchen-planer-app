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

package com.scouts.kitchenplaner.ui.view

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun OverviewField(
    onClick: () -> Unit = {},
    imageUri: Uri? = Uri.EMPTY,
    imageDescription: String = "",
    text: String,
    additional: @Composable () -> Unit = {}
) {
    Box(modifier = Modifier
        .clickable { onClick() }
        .fillMaxWidth()
        .padding(5.dp)
        .border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(6.dp)
        )
        .height(75.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 5.dp, end = 15.dp)
        ) {
            if (imageUri == Uri.EMPTY) {
                Icon(
                    modifier = Modifier
                        .fillMaxHeight(0.9f)
                        .aspectRatio(1.0f)
                        .padding(start = 5.dp),
                    imageVector = Icons.Filled.HideImage,
                    contentDescription = "Projektplatzhalter"
                )
            } else {
                AsyncImage(
                    model = imageUri,
                    contentDescription = imageDescription,
                    modifier = Modifier
                        .fillMaxHeight(0.85f)
                        .aspectRatio(1.0f)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            }

            Text(
                modifier = Modifier.padding(10.dp),
                text = text,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            additional()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun prevOverviewField() {
    OverviewField(text = "Test", additional = {
        Icon(
            imageVector = Icons.Filled.HideImage, contentDescription = "Projektplatzhalter"
        )
    })
}