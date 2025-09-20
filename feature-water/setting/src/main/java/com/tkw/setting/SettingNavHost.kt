package com.tkw.setting

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun SettingNavHost(
    navController: NavHostController = rememberNavController(),
    onNavigateToCup: () -> Unit = {},
    onNavigateToAlarm: () -> Unit = {},
    onShowIntakeDialog: () -> Unit = {},
    onShowUnitDialog: () -> Unit = {},
    onShowLanguageDialog: () -> Unit = {},
    onShowLogoutDialog: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onSyncClick: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = "water_setting"
    ) {
        composable("water_setting") {
            WaterSettingScreen(
                onNavigateToCup = onNavigateToCup,
                onNavigateToAlarm = onNavigateToAlarm,
                onShowIntakeDialog = onShowIntakeDialog,
                onShowUnitDialog = onShowUnitDialog,
                onShowLanguageDialog = onShowLanguageDialog,
                onShowLogoutDialog = onShowLogoutDialog,
                onLoginClick = onLoginClick,
                onSyncClick = onSyncClick
            )
        }
    }
}