/*
 * KitchenPlanerApp is the android app frontend for the KitchenPlaner, a tool
 * to cooperatively plan a meal plan for a campout.
 * Copyright (C) 2023  Arne Kuchenbecker, Antonia Heiming
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

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.scouts.kitchenplaner.ui.navigation.NavHostGeneral
import com.scouts.kitchenplaner.ui.navigation.home
import com.scouts.kitchenplaner.ui.navigation.projects_graph
import com.scouts.kitchenplaner.ui.navigation.recipes_graph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitchenPlannerLayout(
) {

    val navController: NavHostController = rememberNavController()
    var selectedItem by remember { mutableStateOf(0) }

    val sites = listOf(
        Triple("Start",home.route,Icons.Filled.Home),
        Triple("Projekte",projects_graph.route,Icons.Filled.Build),
        Triple("Rezepte", recipes_graph.route, Icons.Filled.AccountBox)
    )

    val backStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(bottomBar = {
        NavigationBar {
            sites.forEach { item ->
                NavigationBarItem(
                    selected = backStackEntry?.destination?.hierarchy?.any { it.route == item.second } ?: false,
                    onClick = {
                        navController.navigate(item.second) {
                            launchSingleTop = true
                        }
                              },
                    label = {Text(item.first)},
                    icon = { Icon(imageVector = item.third, contentDescription = item.first) })
            }
        }
    }) {


        NavHostGeneral(modifier = Modifier.padding(it), navController = navController, startDestination = "start")
    }
}

@Preview
@Composable

fun preview(){
  KitchenPlannerLayout()
}
