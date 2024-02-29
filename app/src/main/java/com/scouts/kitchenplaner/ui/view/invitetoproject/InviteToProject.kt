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

package com.scouts.kitchenplaner.ui.view.invitetoproject

import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.scouts.kitchenplaner.ui.theme.KitchenPlanerTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteToProject(projectId: Long, onNavigateToProject: () -> Unit) {
    var invitePeople by remember { mutableStateOf(false) }

    val link = "http://joinproject.app?id=$projectId"
    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    Scaffold (
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                title = {
                    Text("Invite other people to your project")
                }
            )
        },
        floatingActionButton = {
            if (invitePeople) {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToProject,
                    text = { Text("Fertig") },
                    icon = { Icon(imageVector = Icons.Filled.Check, contentDescription = "Fertig") }
                )
            }
        }
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(it)
        ) {
            if (invitePeople) {
                Text(
                    modifier = Modifier.padding(10.dp),
                    fontSize = 20.sp,
                    text = "Share this link with other people you want to invite to the project:")

                Box (
                    modifier = Modifier
                        .padding(10.dp)
                        .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(5.dp))
                        .clickable {
                            clipboardManager.setText(AnnotatedString(link))
                        }
                ) {
                    Text(
                        modifier = Modifier
                            .padding(8.dp),
                        text = link
                    )
                }

                Button(onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, link)
                    }
                    ContextCompat.startActivity(context, Intent.createChooser(intent, "Share link"), null)


                }) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Invite other people",
                        modifier = Modifier.padding(end = 10.dp)
                    )

                    Text("Projekt teilen")
                }
            } else {
                Text(
                    modifier = Modifier.padding(5.dp),
                    text = "Möchtest du andere Personen zu dem Projekt einladen?")

                Button(
                    modifier = Modifier.padding(5.dp),
                    onClick = {
                        invitePeople = true
                        //TODO make project available online
                    }
                ) {
                    Text("Ja")
                }

                Button(
                    modifier = Modifier.padding(5.dp),
                    onClick = onNavigateToProject
                ) {
                    Text("Nein")
                }
            }
        }
    }


}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun previewInviteScreen() {
    KitchenPlanerTheme (dynamicColor = false) {
        InviteToProject(projectId = 5, onNavigateToProject = {})
    }
}