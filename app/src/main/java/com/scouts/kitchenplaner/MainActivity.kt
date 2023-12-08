package com.scouts.kitchenplaner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.scouts.kitchenplaner.ui.theme.KitchenPlanerTheme
import com.scouts.kitchenplaner.ui.view.createproject.CreateProject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KitchenPlanerTheme(dynamicColor = false) {
                CreateProject()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KitchenPlanerTheme (darkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background) {
            Text("Test")
        }
    }
}