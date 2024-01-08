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

package com.scouts.kitchenplaner.datalayer.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.scouts.kitchenplaner.datalayer.dtos.ProjectStubDTO
import com.scouts.kitchenplaner.datalayer.entities.MealEntity
import com.scouts.kitchenplaner.datalayer.entities.PersonNumberChangeEntity
import com.scouts.kitchenplaner.datalayer.entities.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDAO {
    @Transaction
    suspend fun createProject(
        project: ProjectEntity,
        meals: List<MealEntity>
    ) : Long {
        val rowId = insertProject(project)
        val projectId = getProjectIdByRowId(rowId)

        meals.forEach {
            it.projectId = projectId
            insertMealEntity(it)
        }

        return projectId
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProject(entity: ProjectEntity) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMealEntity(entity: MealEntity) : Long

    @Query("SELECT * FROM projects WHERE projects.id = :id")
    fun getProjectById(id: Long) : Flow<ProjectEntity>

    @Query("SELECT * FROM meals WHERE meals.projectId = :id")
    fun getMealsByProjectID(id: Long) : Flow<List<MealEntity>>

    @Query("SELECT * FROM personNumberChanges WHERE personNumberChanges.projectId = :id")
    fun getPersonNumberChangesByProjectID(id: Long) : Flow<List<PersonNumberChangeEntity>>

    @Query("SELECT id FROM projects WHERE rowId = :rowId")
    suspend fun getProjectIdByRowId(rowId: Long) : Long

    @Query("SELECT projects.name AS name, projects.id AS id, projects.imageUri AS imageUri " +
            "FROM projects INNER JOIN userprojects ON projects.id = userprojects.projectId " +
            "WHERE userprojects.username = :username")
    fun getProjectsForUser(username: String) : Flow<List<ProjectStubDTO>>

    @Query("SELECT projects.name AS name, projects.id AS id, projects.imageUri AS imageUri " +
            "FROM projects")
    fun getAllProjectStubs() : Flow<List<ProjectStubDTO>>

    @Query("SELECT * FROM projects WHERE name = :projectName")
    suspend fun getProjectByProjectName(projectName: String) : ProjectEntity
}
