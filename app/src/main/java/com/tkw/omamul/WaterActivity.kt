package com.tkw.omamul

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.tkw.alarm.WaterAlarmViewModel
import com.tkw.alarmnoti.NotificationManager
import com.tkw.common.LocaleHelper
import com.tkw.omamul.databinding.ActivityWaterBinding
import com.tkw.home.WaterViewModel
import com.tkw.record.LogViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class WaterActivity : AppCompatActivity() {
    private lateinit var dataBinding: ActivityWaterBinding
    private val waterViewModel: WaterViewModel by viewModels()
    private val logViewModel: LogViewModel by viewModels()
    private val alarmViewModel: WaterAlarmViewModel by viewModels()
    // Compose Navigation으로 마이그레이션되어 Fragment ID 참조 제거
    // private val mainFragmentSet = setOf()
    // private val hideTitleFragmentSet = setOf()

    private val broadcastReceiver = DateChangeReceiver {
        waterViewModel.setToday()
        logViewModel.setToday()
    }
    private val receiveFilter = IntentFilter(Intent.ACTION_DATE_CHANGED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()
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
        initBinding()
        initView()
        setWorkManager()
    }

    //최초 설치 시 언어 선택하면 액티비티 재생성되지 않도록(configChanges 적용 안됨) 추가함.
    private fun initLanguage() {
        val getLanguage = LocaleHelper.getApplicationLocales().toLanguageTags()
        LocaleHelper.setApplicationLocales(getLanguage)
    }

    private fun initBinding() {
        dataBinding = ActivityWaterBinding.inflate(layoutInflater)
    }

    private fun initView() {
        setContentView(dataBinding.root)
        setupNavigation()
        setSupportActionBar(dataBinding.toolbar)
        //툴바 설정 후 호출
        setDestinationChangedListener()
        setNavBackListener()
    }

    private fun setupNavigation() {
        // Compose Navigation으로 마이그레이션되어 Fragment Navigation 코드 간소화
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navController = navHostFragment.navController

        // Fragment Navigation 설정 제거 (Compose에서 처리)
        // NavigationUI.setupWithNavController(dataBinding.toolbar, navController, appBarConfiguration)
        // NavigationUI.setupWithNavController(dataBinding.bottomNav, navController)

        runBlocking {
            setStartDestination(navController)
        }
    }

    private suspend fun setStartDestination(nav: NavController) {
        // Compose Navigation으로 마이그레이션되어 Navigation 설정 간소화
        val navGraph = nav.navInflater.inflate(R.navigation.nav_graph)
        nav.graph = navGraph

        // Compose Navigation에서 시작 화면 결정은 Compose 내에서 처리
        // if(waterViewModel.getInitFlag()) -> HomeScreen or InitScreen
    }

    private fun setDestinationChangedListener() {
        // Compose Navigation으로 마이그레이션되어 Fragment 기반 Listener 제거
        // Bottom Navigation 및 Toolbar 표시/숨김은 Compose에서 처리

        // 기본적으로 Bottom Navigation 숨김, Toolbar 숨김 (Compose에서 처리)
        dataBinding.bottomNav.visibility = View.GONE
        supportActionBar?.hide()
        setWindowInsets()
    }

    private fun setWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setWindowInsetsExcludeBottom() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    private fun setNavBackListener() {
        dataBinding.toolbar.setNavigationOnClickListener {
            // 시스템 back key 동작과 동일하게 설정.
            // CupManageFragment 등 특정 flag에서 백 키 또는 업 버튼 눌렀을 때 백스택 이동 제어하기 위해 설정.
            // 다른 프래그먼트에서 onBackPressedDispatcher에 콜백을 설정함으로써 백스택 이동을 제어할 수 있음.
            onBackPressedDispatcher.onBackPressed()
        }
    }

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
