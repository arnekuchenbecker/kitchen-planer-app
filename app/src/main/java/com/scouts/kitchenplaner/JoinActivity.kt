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

package com.scouts.kitchenplaner

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scouts.kitchenplaner.ui.theme.KitchenPlanerTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import javax.inject.Inject

/**
 * Activity for joining a project
 */
@AndroidEntryPoint
class JoinActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val projectId = intent?.data?.getQueryParameter("id")

        setContent {
            KitchenPlanerTheme(dynamicColor = false) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //TODO Join Screen with the found projectID
                    Column {
                        Text("YOU DID IT! $projectId")

                        TestImage(1L)
                    }
                }
            }
        }
    }
}

@Composable
fun TestImage(projectID: Long, vm: TestVM = hiltViewModel()) {
    Column {
        Button(onClick = { vm.loadImage(projectID) }) { Text("Load Image") }

        if (vm.loaded) {
            Image(vm.image.asImageBitmap(), "TestImage")
        } else {
            Text("Loading image...")
        }
    }
}

@HiltViewModel
class TestVM @Inject constructor(
    private val testService: TestImageService
) : ViewModel() {
    lateinit var image: Bitmap
    var loaded by mutableStateOf(false)

    fun loadImage(projectID: Long) {
        viewModelScope.launch {
            val response = testService.getImage(projectID)
            val bytes = response.body()!!.bytes()
            image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            loaded = true
        }
    }
}

interface TestImageService {
    @GET("media/projects/{projectID}")
    suspend fun getImage(@Path("projectID") projectID: Long): Response<ResponseBody>
}
