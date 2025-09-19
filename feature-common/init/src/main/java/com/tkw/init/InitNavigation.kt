package com.tkw.init

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Navigation Routes
object InitDestination {
    const val LANGUAGE = "init_language"
    const val TIME = "init_time"
    const val INTAKE = "init_intake"
}

@Composable
fun InitNavHost(
    navController: NavHostController = rememberNavController(),
    onNavigateToHome: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = InitDestination.LANGUAGE
    ) {
        // 언어 선택 화면
        composable(InitDestination.LANGUAGE) {
            LanguageSelectionScreen(
                onNavigateNext = {
                    navController.navigate(InitDestination.TIME) {
                        popUpTo(InitDestination.LANGUAGE) { inclusive = true }
                    }
                }
            )
        }

        // 시간 설정 화면
        composable(InitDestination.TIME) {
            TimeSettingScreen(
                onNavigateNext = {
                    navController.navigate(InitDestination.INTAKE) {
                        popUpTo(InitDestination.TIME) { inclusive = true }
                    }
                }
            )
        }

        // 목표량 설정 화면 (최종 단계)
        composable(InitDestination.INTAKE) {
            IntakeGoalScreen(
                onNavigateToHome = onNavigateToHome
            )
        }
    }
}