package com.tkw.cup

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.tkw.domain.model.Cup

@Composable
fun CupNavHost(
    navController: NavHostController,
    onNavigateBack: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "cup_management"
    ) {
        composable("cup_management") {
            CupManagementScreen(
                onNavigateToCreate = { cup ->
                    if (cup != null) {
                        navController.navigate("cup_creation/${cup.cupId}/${cup.cupName}/${cup.cupAmount}/${cup.createMode}")
                    } else {
                        navController.navigate("cup_creation/null/null/200/true")
                    }
                },
                onNavigateBack = onNavigateBack
            )
        }

        composable(
            "cup_creation/{cupId}/{cupName}/{cupAmount}/{createMode}",
            arguments = listOf(
                navArgument("cupId") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("cupName") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("cupAmount") { type = NavType.IntType },
                navArgument("createMode") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val cupId = backStackEntry.arguments?.getString("cupId")
            val cupName = backStackEntry.arguments?.getString("cupName")
            val cupAmount = backStackEntry.arguments?.getInt("cupAmount") ?: 200
            val createMode = backStackEntry.arguments?.getBoolean("createMode") ?: true

            val cup = if (cupId != "null" && cupName != "null") {
                Cup(
                    cupId = cupId ?: "",
                    cupName = cupName ?: "",
                    cupAmount = cupAmount
                ).apply {
                    this.createMode = createMode
                }
            } else {
                null
            }

            CupCreationScreen(
                cup = cup,
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = hiltViewModel()
            )
        }
    }
}