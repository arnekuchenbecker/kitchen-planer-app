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

package com.scouts.kitchenplaner

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.scouts.kitchenplaner.datalayer.KitchenAppDatabase
import com.scouts.kitchenplaner.datalayer.daos.ProjectDAO
import com.scouts.kitchenplaner.datalayer.repositories.ProjectRepository
import com.scouts.kitchenplaner.model.entities.Project
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ProjectDatabaseTest {

    private lateinit var repo: ProjectRepository
    private lateinit var projectDAO: ProjectDAO
    private lateinit var db: KitchenAppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, KitchenAppDatabase::class.java
        ).build()
        projectDAO = db.projectDao()
        repo = ProjectRepository(projectDAO)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }


    @Test
    @Throws(Exception::class)
    fun testInsertProject(): Unit = runBlocking {
        val project = Project(name = "Test")
        repo.insertProject(project)
        val retValue = repo.getProjectByProjectName("Test")
        assertEquals("Test", retValue.name)
        assertNotEquals(null, retValue.id)
    }


    @Test
    @Throws(Exception::class)
    fun testGetAllProjects(): Unit = runTest {
        val project = Project(name = "Test")
        val project2 = Project(name = "Test2")

        repo.getAllProjectsOverview().test {
            val projectId = repo.insertProject(project)
            val flowContent = awaitItem()

            assertEquals("Incorrect number of projects in flow", 1, flowContent.size)
            assertEquals("Incorrect project name", project.name, flowContent[0].name)
            assertEquals("Incorrect project id", projectId, flowContent[0].id)

            val project2Id = repo.insertProject(project2)

            assertNotEquals("Same project id for different projects", projectId, project2Id)

            val flowContent2 = awaitItem()

            assertEquals("Incorrect number of projects in flow", 2, flowContent2.size)
            assertTrue("No project with id $projectId found", flowContent2.any { it.id == projectId })
            assertTrue("No project with id $project2Id found", flowContent2.any { it.id == project2Id })
            assertTrue("No project with name ${project.name} found", flowContent2.any { it.name == project.name })
            assertTrue("No project with name ${project2.name} found", flowContent2.any { it.name == project2.name })
        }
    }
}