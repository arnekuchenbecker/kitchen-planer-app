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

package com.scouts.kitchenplaner.model

import android.net.Uri
import com.scouts.kitchenplaner.model.entities.MealPlan
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.Date

class PersonNumberTest {
    private val uri = mockk<Uri>(relaxed = true)
    private val project = Project(
        1,
        "Test Projekt",
        listOf(),
        listOf(),
        MealPlan(
            Date(0),
            Date(72*60*60*1000),
            listOf("Frühstück", "Mittagessen", "Abendessen"),
        ),
        uri
    )

    @OptIn(DomainLayerRestricted::class)
    @Test
    fun testTotalNumberCalculation() {
        project.mealPlan.setNumberChanges(
            mapOf(
                MealSlot(Date(0), "Mittagessen") to 5,
                MealSlot(Date(24*60*60*1000), "Abendessen") to 70,
                MealSlot(Date(48*60*60*1000), "Mittagessen") to -3,
                MealSlot(Date(72*60*60*1000), "Mittagessen") to -68,
                MealSlot(Date(72*60*60*1000), "Abendessen") to -4,
            )
        )

        Assertions.assertEquals(0, project.mealPlan[MealSlot(Date(0), "Frühstück")].second)
        Assertions.assertEquals(5, project.mealPlan[MealSlot(Date(0), "Mittagessen")].second)
        Assertions.assertEquals(5, project.mealPlan[MealSlot(Date(0), "Abendessen")].second)
        Assertions.assertEquals(5, project.mealPlan[MealSlot(Date(24*60*60*1000), "Frühstück")].second)
        Assertions.assertEquals(5, project.mealPlan[MealSlot(Date(24*60*60*1000), "Mittagessen")].second)
        Assertions.assertEquals(75, project.mealPlan[MealSlot(Date(24*60*60*1000), "Abendessen")].second)
        Assertions.assertEquals(75, project.mealPlan[MealSlot(Date(48*60*60*1000), "Frühstück")].second)
        Assertions.assertEquals(72, project.mealPlan[MealSlot(Date(48*60*60*1000), "Mittagessen")].second)
        Assertions.assertEquals(72, project.mealPlan[MealSlot(Date(48*60*60*1000), "Abendessen")].second)
        Assertions.assertEquals(72, project.mealPlan[MealSlot(Date(72*60*60*1000), "Frühstück")].second)
        Assertions.assertEquals(4, project.mealPlan[MealSlot(Date(72*60*60*1000), "Mittagessen")].second)
        Assertions.assertEquals(0, project.mealPlan[MealSlot(Date(72*60*60*1000), "Abendessen")].second)
    }
}