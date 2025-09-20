package com.tkw.alarm

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AlarmNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = AlarmDestination.MAIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(AlarmDestination.MAIN) {
            AlarmSettingScreen(
                onNavigateToMode = {
                    navController.navigate(AlarmDestination.MODE)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(AlarmDestination.MODE) {
            AlarmModeScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

object AlarmDestination {
    const val MAIN = "alarm_main"
    const val MODE = "alarm_mode"
}