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

package com.scouts.kitchenplaner.datalayer.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.scouts.kitchenplaner.datalayer.dtos.MealIdentifierDTO
import com.scouts.kitchenplaner.datalayer.dtos.PersonNumberChangeIdentifierDTO
import com.scouts.kitchenplaner.datalayer.dtos.ProjectArchivedDTO
import com.scouts.kitchenplaner.datalayer.dtos.ProjectIdDTO
import com.scouts.kitchenplaner.datalayer.dtos.ProjectImageDTO
import com.scouts.kitchenplaner.datalayer.dtos.ProjectStubDTO
import com.scouts.kitchenplaner.datalayer.entities.MealEntity
import com.scouts.kitchenplaner.datalayer.entities.PersonNumberChangeEntity
import com.scouts.kitchenplaner.datalayer.entities.ProjectEntity
import com.scouts.kitchenplaner.datalayer.entities.UserEntity
import com.scouts.kitchenplaner.datalayer.entities.UserProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDAO {
    @Transaction
    suspend fun createProject(
        project: ProjectEntity,
        meals: List<MealEntity>
    ): Long {
        val rowId = insertProject(project)
        val projectId = getProjectIdByRowId(rowId)

        meals.forEach {
            it.projectId = projectId
            insertMealEntity(it)
        }

        return projectId
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProject(entity: ProjectEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMealEntity(entity: MealEntity): Long

    @Query(
        "UPDATE meals " +
                "SET `order` = `order` + 1 " +
                "WHERE projectId = :projectId " +
                "AND `order` >= :index"
    )
    suspend fun increaseMealOrder(projectId: Long, index: Int)

    @Query(
        "UPDATE meals " +
                "SET `order` = `order` - 1 " +
                "WHERE projectId = :projectId " +
                "AND `order` >= :index"
    )
    suspend fun decreaseMealOrder(projectId: Long, index: Int)

    @Delete(MealEntity::class)
    suspend fun deleteMeal(meal: MealIdentifierDTO)

    @Query("SELECT `order` FROM meals WHERE projectId = :projectId AND name = :name")
    suspend fun getMealOrder(projectId: Long, name: String): Int

    @Query("SELECT * FROM projects WHERE projects.id = :id")
    fun getProjectById(id: Long): Flow<ProjectEntity>

    @Query("SELECT name FROM meals WHERE meals.projectId = :id ORDER BY meals.`order`")
    fun getMealsByProjectID(id: Long): Flow<List<String>>

    @Query("SELECT * FROM personNumberChanges WHERE personNumberChanges.projectId = :id")
    fun getPersonNumberChangesByProjectID(id: Long): Flow<List<PersonNumberChangeEntity>>

    @Query("SELECT id FROM projects WHERE rowId = :rowId")
    suspend fun getProjectIdByRowId(rowId: Long): Long

    @Query(
        "SELECT projects.name AS name, projects.id AS id, projects.imageUri AS imageUri " +
                "FROM projects INNER JOIN userprojects ON projects.id = userprojects.projectId " +
                "WHERE userprojects.username = :username AND projects.isArchived = 0"
    )
    fun getProjectsForUser(username: String): Flow<List<ProjectStubDTO>>

    @Query(
        "SELECT projects.name AS name, projects.id AS id, projects.imageUri AS imageUri " +
                "FROM projects"
    )
    fun getAllProjectStubs(): Flow<List<ProjectStubDTO>>

    @Query("SELECT * FROM projects WHERE name = :projectName")
    suspend fun getProjectByProjectName(projectName: String): ProjectEntity

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username)")
    suspend fun getExistsUserByName(username: String): Int

    @Insert
    suspend fun insertUser(user: UserEntity)

    @Update(entity = ProjectEntity::class)
    suspend fun updateImage(image: ProjectImageDTO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonNumberChange(entity: PersonNumberChangeEntity)

    @Delete(PersonNumberChangeEntity::class)
    suspend fun deletePersonNumberChange(identifierDTO: PersonNumberChangeIdentifierDTO)

    @Insert
    suspend fun addUserToProject(user: UserProjectEntity)

    @Delete
    suspend fun removeUserFromProject(user: UserProjectEntity)

    @Update
    suspend fun updateLastShownProjectForUser(entity: UserProjectEntity)

    @Query("SELECT * FROM userprojects WHERE username = :user ORDER BY lastShown DESC LIMIT :limit")
    fun getLatestShownProjectsForUser(user: String, limit: Int): Flow<List<UserProjectEntity>>

    // Methods for archiving projects

    @Delete(MealEntity::class)
    suspend fun deleteMealsByProjectId(projectId: ProjectIdDTO)

    @Update(ProjectEntity::class)
    suspend fun setProjectArchivedStatus(projectArchived: ProjectArchivedDTO)
}
