package com.tkw.omamul

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tkw.cup.CupNavHost
import com.tkw.home.HomeScreen
import com.tkw.init.InitNavHost
import com.tkw.record.WaterLogScreen
import com.tkw.setting.WaterSettingScreen
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.tkw.alarm.WaterAlarmViewModel
import com.tkw.alarmnoti.NotificationManager
import com.tkw.common.LocaleHelper
import com.tkw.home.WaterViewModel
import com.tkw.record.LogViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class WaterActivity : ComponentActivity() {
    private val waterViewModel: WaterViewModel by viewModels()
    private val logViewModel: LogViewModel by viewModels()
    private val alarmViewModel: WaterAlarmViewModel by viewModels()

    private val broadcastReceiver = DateChangeReceiver {
        waterViewModel.setToday()
        logViewModel.setToday()
    }
    private val receiveFilter = IntentFilter(Intent.ACTION_DATE_CHANGED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // SplashScreen을 initFlag 로딩이 완료될 때까지 유지
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            // initFlag가 로딩될 때까지 스플래시 화면 유지
            waterViewModel.initFlagStateFlow.value == null
        }

        initialize()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(broadcastReceiver, receiveFilter)
        alarmViewModel.setNotificationEnabled(NotificationManager.isNotificationEnabled(this))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

    private fun initialize() {
        initLanguage()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WaterApp()
                }
            }
        }
        setWorkManager()
    }

    //최초 설치 시 언어 선택하면 액티비티 재생성되지 않도록(configChanges 적용 안됨) 추가함.
    private fun initLanguage() {
        val getLanguage = LocaleHelper.getApplicationLocales().toLanguageTags()
        LocaleHelper.setApplicationLocales(getLanguage)
    }

    @Composable
    private fun WaterApp() {
        val navController = rememberNavController()
        val waterViewModel: WaterViewModel = hiltViewModel()

        // 초기화 상태 확인 (null이면 로딩 중)
        val isInitialized by waterViewModel.initFlagStateFlow.collectAsStateWithLifecycle()

        // 알림 권한 상태 업데이트
        LaunchedEffect(Unit) {
            alarmViewModel.setNotificationEnabled(NotificationManager.isNotificationEnabled(this@WaterActivity))
        }

        // initFlag가 로딩될 때까지 대기
        when (isInitialized) {
            null -> {
                // 로딩 중 - 빈 화면 또는 로딩 인디케이터 표시
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // 스플래시 스크린이 표시되므로 비워둘 수 있음
                }
            }
            else -> {
                // 로딩 완료 - 적절한 화면으로 이동
                NavHost(
                    navController = navController,
                    startDestination = if (isInitialized == true) "main_flow" else "onboarding_flow"
                ) {
                    // 온보딩 플로우
                    composable("onboarding_flow") {
                        InitNavHost(
                            onNavigateToHome = {
                                navController.navigate("main_flow") {
                                    popUpTo("onboarding_flow") { inclusive = true }
                                }
                            }
                        )
                    }

                    // 메인 앱 플로우
                    composable("main_flow") {
                        MainNavHost()
                    }
                }
            }
        }
    }

    @Composable
    private fun MainNavHost() {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        Scaffold(
            bottomBar = {
                NavigationBar {
                    val items = listOf(
                        BottomNavItem("home", "홈", Icons.Default.Home),
                        BottomNavItem("record", "기록", Icons.Default.Timeline),
                        BottomNavItem("setting", "설정", Icons.Default.Person)
                    )

                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    HomeScreen(
                        onNavigateToCupManagement = {
                            navController.navigate("cup_management")
                        }
                    )
                }

                composable("record") {
                    WaterLogScreen(
                        onNavigateBack = {
                            // Bottom Navigation에서는 뒤로 가기 필요없음
                        }
                    )
                }

                composable("setting") {
                    WaterSettingScreen()
                }

                composable("cup_management") {
                    CupNavHost(
                        navController = rememberNavController(),
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }

    data class BottomNavItem(
        val route: String,
        val label: String,
        val icon: androidx.compose.ui.graphics.vector.ImageVector
    )

    private fun setWorkManager() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(true)
            .setRequiresStorageNotLow(true)
            .build()


        val periodicWorkRequest = PeriodicWorkRequestBuilder<ScheduledWorkManager>(
            1, TimeUnit.DAYS,
            30, TimeUnit.MINUTES
        )
            .setInitialDelay(ScheduledWorkManager.getRemainTime(), TimeUnit.MILLISECONDS)  //새벽 3시부터 현재 시간 차이
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            ScheduledWorkManager.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest
        )
    }

    private fun cancelWorkManager(alarmId: String) {
        WorkManager.getInstance(this).cancelUniqueWork(ScheduledWorkManager.WORK_NAME)
    }
}
