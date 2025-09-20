package com.tkw.record

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun RecordNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = RecordDestination.MAIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(RecordDestination.MAIN) {
            WaterLogScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

object RecordDestination {
    const val MAIN = "record_main"
}