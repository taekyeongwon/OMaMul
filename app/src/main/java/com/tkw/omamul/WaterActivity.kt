package com.tkw.omamul

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.tkw.common.LocaleHelper
import com.tkw.common.WaterAlarmManager
import com.tkw.omamul.databinding.ActivityWaterBinding
import com.tkw.home.WaterViewModel
import com.tkw.record.LogViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class WaterActivity : AppCompatActivity() {
    private lateinit var dataBinding: ActivityWaterBinding
    private val waterViewModel: WaterViewModel by viewModels()
    private val logViewModel: LogViewModel by viewModels()
    private val mainFragmentSet = setOf(
        com.tkw.home.R.id.waterFragment,
        com.tkw.record.R.id.waterLogFragment,
        com.tkw.setting.R.id.settingFragment
    )

    private val broadcastReceiver = DateChangeReceiver {
        waterViewModel.setToday()
        logViewModel.setToday()
    }
    private val receiveFilter = IntentFilter(Intent.ACTION_DATE_CHANGED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initLanguage()
        installSplashScreen()
        initBinding()
        initView()

        val triggerTime = Calendar.getInstance()
        triggerTime.set(Calendar.HOUR_OF_DAY, 22)
        triggerTime.set(Calendar.MINUTE, 40)
        triggerTime.set(Calendar.SECOND, 0)
        triggerTime.set(Calendar.MILLISECOND, 0)
        WaterAlarmManager.setAlarm(this, triggerTime.timeInMillis, 1000 * 60)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(broadcastReceiver, receiveFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

    //최초 설치 시 언어 선택하면 액티비티 재생성되지 않도록(configChanges 적용 안됨) 추가함.
    private fun initLanguage() {
        val getLanguage = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        LocaleHelper.setApplicationLocales(this, getLanguage)
    }

    private fun initBinding() {
        dataBinding = ActivityWaterBinding.inflate(layoutInflater)
    }

    private fun initView() {
        setContentView(dataBinding.root)
        setSupportActionBar(dataBinding.toolbar)
        initNavigate()
    }

    private fun initNavigate() {
        val navController = findNavController(R.id.fragment_container_view)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            dataBinding.bottomNav.visibility =
                if(mainFragmentSet.contains(destination.id)) View.VISIBLE
                else View.GONE
        }

        val appBarConfiguration = AppBarConfiguration(mainFragmentSet.plus(com.tkw.init.R.id.initLanguageFragment))
        NavigationUI.setupWithNavController(dataBinding.toolbar, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(dataBinding.bottomNav, navController)

        lifecycleScope.launch {
            setStartDestination(navController)
        }
    }

    private suspend fun setStartDestination(nav: NavController) {
        val navGraph = nav.navInflater.inflate(R.navigation.nav_graph)

        if(waterViewModel.getInitFlag()) {
            navGraph.setStartDestination(com.tkw.home.R.id.home_nav_graph)
        } else {
            navGraph.setStartDestination(com.tkw.init.R.id.init_nav_graph)
        }
        nav.graph = navGraph
    }
}