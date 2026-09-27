package com.nadaa.mpasiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val babyViewModel = remember { BabyViewModel() }
            var currentScreen by remember { mutableStateOf("home") }

            when (currentScreen) {
                "home" -> HomeScreen(
                    babyViewModel = babyViewModel,
                    currentRoute = currentScreen,
                    onNavigate = { route -> currentScreen = route }
                )
                "profile" -> ProfileScreen(
                    babyViewModel = babyViewModel,
                    currentRoute = currentScreen,
                    onNavigate = { route -> currentScreen = route }
                )
                "dice" -> DiceScreen(
                    babyViewModel = babyViewModel,
                    currentRoute = currentScreen,
                    onNavigate = { route -> currentScreen = route }
                )
                "schedule" -> ScheduleScreen(
                    babyViewModel = babyViewModel,
                    currentRoute = currentScreen,
                    onNavigate = { route -> currentScreen = route }
                )
            }
        }
    }
}