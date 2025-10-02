package com.tkw.alarm

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.tkw.alarm.databinding.FragmentAlarmModeBinding
import com.tkw.alarm.dialog.AlarmModeBottomDialog
import com.tkw.alarm.dialog.ExactAlarmDialog
import com.tkw.alarmnoti.NotificationManager
import com.tkw.common.PermissionHelper
import com.tkw.common.autoCleared
import com.tkw.domain.IAlarmManager
import com.tkw.domain.model.AlarmMode
import com.tkw.ui.custom.SwitchView
import com.tkw.ui.dialog.SettingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import javax.inject.Inject

@AndroidEntryPoint
class AlarmModeFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentAlarmModeBinding>()
    private val viewModel: WaterAlarmViewModel by hiltNavGraphViewModels(R.id.alarm_nav_graph)
    private var currentMode: AlarmMode? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var toolbarSwitchView: SwitchView

    @Inject
    lateinit var alarmManager: IAlarmManager

    private val fragmentList by lazy {
        listOf(
            AlarmModePeriodFragment(),
            AlarmModeCustomFragment()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                //MenuProvider onCreateMenu가 resume마다 다시 호출되므로 아무 것도 처리하지 않음.
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentAlarmModeBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObserver()
        initListener()
    }

    private fun initView() {
        initItemMenu()
        dataBinding.run {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@AlarmModeFragment.viewModel
            executePendingBindings()
        }
    }

    private fun initItemMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_toggle, menu)
                val toggleItem = menu.findItem(R.id.alarm_toggle)
                toolbarSwitchView = toggleItem.actionView!!.findViewById(R.id.toolbar_switch)

                NotificationManager.isNotificationEnabled(requireContext())
                    .also {
                        lifecycleScope.launch {
                            viewModel.setNotificationEnabled(it)
                            setSwitchButtonCheckedWithEnabled(it)
                            setSwitchButtonCheckedListener(it)
                        }
//                        checkApi31ExactAlarm()
                    }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private suspend fun setSwitchButtonCheckedWithEnabled(isNotificationEnabled: Boolean) {
        val isEnabled = isNotificationEnabled && viewModel.getNotificationEnabled()
        if(isEnabled) {
            alarmOn()
        } else {
            alarmOff()
        }
        toolbarSwitchView.setChecked(isEnabled)
        viewModel.setAlarmEnabled(isEnabled)
    }

    private fun setSwitchButtonCheckedListener(isNotificationEnabled: Boolean) {
        toolbarSwitchView.setCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                viewModel.setAlarmEnabled(isChecked)
            }
            if(isChecked) {
                if(!isNotificationEnabled) showAlert()
                else alarmOn() //알람 설정
            } else alarmOff() //알람 cancel
        }
    }

    private fun alarmOn() {
        viewModel.wakeAllAlarm()
    }

    private fun alarmOff() {
        viewModel.sleepAllAlarm()
    }

    private fun showAlert() {
        val dialog = SettingDialog(
            cancelAction = {
                toolbarSwitchView.setChecked(false)
            },
            confirmAction = {
                PermissionHelper.goToNotificationSetting(
                    requireActivity(),
                    activityResultLauncher
                )
            }
        )
        dialog.show(childFragmentManager, dialog.tag)
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.alarmModeFlow.collect {
                    //현재 뷰모델 setting에서 가져온 모드로 replace
                    it?.let {
                        setAlarmModeText(it)
                        currentMode = it

                        when(it) {
                            AlarmMode.PERIOD -> {
                                replaceFragment(fragmentList[0])
                            }
                            AlarmMode.CUSTOM -> {
                                replaceFragment(fragmentList[1])
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Default) {
            viewModel.timeTickerFlow.flatMapLatest {
                setTimeContent(it)
            }.collect {
                viewModel.setRemainTimeContent(it)
            }
        }
    }

    private fun setAlarmModeText(mode: AlarmMode) {
        val text = when(mode) {
            AlarmMode.PERIOD -> getString(com.tkw.ui.R.string.alarm_mode_period)
            AlarmMode.CUSTOM -> getString(com.tkw.ui.R.string.alarm_mode_custom)
        }
        dataBinding.tvAlarmMode.setText(text)
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.commit {
            replace(dataBinding.container.id, fragment, fragment.tag)
        }
    }

    private fun setTimeContent(time: Long): Flow<String> {
        var remainTime = time

        return flow {
            if(time == -1L) {
                emit(getString(com.tkw.ui.R.string.alarm_detail_empty))
            } else if(!viewModel.isNotificationAlarmEnabled().first()) {
                emit(getString(com.tkw.ui.R.string.alarm_detail_switch_off))
            } else {
                while (remainTime > 0) {
                    val content = getRemainTimeContent(remainTime)
                    if(content.isEmpty()) {
                        emit(getString(com.tkw.ui.R.string.alarm_ringing_soon))
                    } else {
                        emit(
                            String.format(
                                getString(com.tkw.ui.R.string.alarm_detail_remain),
                                content
                            )
                        )
                    }
                    remainTime -= WaterAlarmViewModel.TIME_UNIT_SECONDS
                    delay(WaterAlarmViewModel.TIME_UNIT_SECONDS)
                }
            }
        }

    }

    private fun getRemainTimeContent(remainTime: Long): String {
        val text = StringBuilder()

        val days = remainTime / (1000 * 60 * 60 * 24)
        val hour = (remainTime / (1000 * 60 * 60)) % 24
        val minute = (remainTime / (1000 * 60)) % 60

        if (days != 0L) {
            text.append(days)
                .append(getString(com.tkw.ui.R.string.day))
                .append(" ")
        }
        if (hour != 0L) {
            text.append(hour)
                .append(getString(com.tkw.ui.R.string.hour))
                .append(" ")
        }
        if (minute != 0L) {
            text.append(minute)
                .append(getString(com.tkw.ui.R.string.minute))
                .append(" ")
        }

        return text.toString()
    }

    private fun initListener() {
        dataBinding.tvAlarmMode.setOnClickListener {
            val dialog = AlarmModeBottomDialog(currentMode!!) {
                viewModel.updateAlarmMode(it)
            }
            dialog.show(childFragmentManager, dialog.tag)
        }
    }
}