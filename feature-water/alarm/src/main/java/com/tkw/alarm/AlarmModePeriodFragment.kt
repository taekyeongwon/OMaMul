package com.tkw.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.tkw.alarm.databinding.FragmentAlarmModePeriodBinding
import com.tkw.alarm.dialog.AlarmPeriodDialog
import com.tkw.alarm.dialog.AlarmTimeBottomDialog
import com.tkw.common.autoCleared
import com.tkw.common.util.DateTimeUtils
import com.tkw.common.util.toEpochMilli
import com.tkw.domain.model.AlarmModeSetting
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlarmModePeriodFragment : Fragment() {
    private var dataBinding by autoCleared<FragmentAlarmModePeriodBinding>()
    private val viewModel: WaterAlarmViewModel by hiltNavGraphViewModels(R.id.alarm_nav_graph)
    private var periodMode: AlarmModeSetting = AlarmModeSetting()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentAlarmModePeriodBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObserver()
        initListener()
    }

    private fun initView() {
        //wake all period alarm
        lifecycleScope.launch {
            initNotification()
        }
    }

    private suspend fun initNotification() {
        if(viewModel.isNotificationAlarmEnabled().first()) {
            viewModel.wakeAllAlarm()
        }
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.periodModeSettingsFlow.collect {
                    //해당 값으로 화면 구성
                    it?.let { period ->
                        periodMode = period
                        viewModel.setTmpPeriodMode(period)
                        dataBinding.alarmWeek.setChecked(period.selectedDate)
                        dataBinding.tvIntervalSet.text = DateTimeUtils.Time.getFormat(
                            period.interval.toLong(),
                            requireContext().getString(com.tkw.ui.R.string.hour),
                            requireContext().getString(com.tkw.ui.R.string.minute)
                        )
                        dataBinding.tvAlarmTime.text = period.run {
                            getTimeRange(
                                DateTimeUtils.Time.getFormat(startTime),
                                DateTimeUtils.Time.getFormat(endTime)
                            )
                        }
                        dataBinding.ivEdit.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tmpPeriodModeFlow.collect {
                    if(it != periodMode) {
                        dataBinding.btnSave.visibility = View.VISIBLE
                    } else {
                        dataBinding.btnSave.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun initListener() {
        dataBinding.alarmWeek.setCheckListListener {
            lifecycleScope.launch {
                viewModel.tmpPeriodModeFlow.value?.let { setting ->
                    viewModel.setTmpPeriodMode(setting.copy(selectedDate = it))
                }
            }
        }
        dataBinding.clPeriod.setOnClickListener {
            val currentPeriod = DateTimeUtils.Time.getLocalTime(
                dataBinding.tvIntervalSet.text.toString(),
                requireContext().getString(com.tkw.ui.R.string.hour),
                requireContext().getString(com.tkw.ui.R.string.minute)
            )
            val dialog = AlarmPeriodDialog(currentPeriod) {
                dataBinding.tvIntervalSet.text = it
                val interval = DateTimeUtils.Time.getLocalTime(
                    it,
                    requireContext().getString(com.tkw.ui.R.string.hour),
                    requireContext().getString(com.tkw.ui.R.string.minute)
                ).toSecondOfDay()
                lifecycleScope.launch {
                    viewModel.tmpPeriodModeFlow.value?.let { setting ->
                        viewModel.setTmpPeriodMode(setting.copy(interval = interval))
                    }
                }
            }
            dialog.show(childFragmentManager, dialog.tag)
        }
        dataBinding.btnSave.setOnClickListener {
            lifecycleScope.launch {
                viewModel.tmpPeriodModeFlow.value?.let {
                    updateModeSetting(it)
                    setAlarm(it)
                }
            }
        }
        // TODO: 기상시간과 취침시간을 각각 설정할 수 있는 UI 버튼이 필요합니다
        // 임시로 기존 클릭 리스너를 기상시간 설정으로 변경
        dataBinding.clAlarmTimeEdit.setOnClickListener {
            showWakeTimeDialog()
        }
    }

    private suspend fun updateModeSetting(period: AlarmModeSetting?) {
        period?.let {
            viewModel.updateAlarmModeSetting(it)
        }
    }

    private suspend fun setAlarm(period: AlarmModeSetting) {
        viewModel.setPeriodAlarm(period)
    }

    private fun showWakeTimeDialog() {
        lifecycleScope.launch {
            viewModel.tmpPeriodModeFlow.value?.let {
                val dialog = AlarmTimeBottomDialog(
                    selectedTime = DateTimeUtils.Time.getLocalTime(it.startTime),
                    resultListener = { wakeTime ->
                        lifecycleScope.launch {
                            viewModel.tmpPeriodModeFlow.value?.let { setting ->
                                val newSetting = setting.copy(startTime = wakeTime.toEpochMilli())
                                viewModel.setTmpPeriodMode(newSetting)
                                dataBinding.tvAlarmTime.text = newSetting.run {
                                    getTimeRange(
                                        DateTimeUtils.Time.getFormat(startTime),
                                        DateTimeUtils.Time.getFormat(endTime)
                                    )
                                }
                            }
                        }
                    }
                )
                dialog.show(childFragmentManager, dialog.tag)
            }
        }
    }

    private fun showSleepTimeDialog() {
        lifecycleScope.launch {
            viewModel.tmpPeriodModeFlow.value?.let {
                val dialog = AlarmTimeBottomDialog(
                    selectedTime = DateTimeUtils.Time.getLocalTime(it.endTime),
                    resultListener = { sleepTime ->
                        lifecycleScope.launch {
                            viewModel.tmpPeriodModeFlow.value?.let { setting ->
                                val newSetting = setting.copy(endTime = sleepTime.toEpochMilli())
                                viewModel.setTmpPeriodMode(newSetting)
                                dataBinding.tvAlarmTime.text = newSetting.run {
                                    getTimeRange(
                                        DateTimeUtils.Time.getFormat(startTime),
                                        DateTimeUtils.Time.getFormat(endTime)
                                    )
                                }
                            }
                        }
                    }
                )
                dialog.show(childFragmentManager, dialog.tag)
            }
        }
    }
}