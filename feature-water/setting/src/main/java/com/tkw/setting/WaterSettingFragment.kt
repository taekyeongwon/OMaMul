package com.tkw.setting

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withStarted
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.tkw.common.autoCleared
import com.tkw.common.util.DateTimeUtils
import com.tkw.domain.Authentication
import com.tkw.firebase.AccessTokenManager
import com.tkw.firebase.BackupForeground
import com.tkw.home.dialog.WaterIntakeDialog
import com.tkw.navigation.DeepLinkDestination
import com.tkw.navigation.deepLinkNavigateTo
import com.tkw.setting.databinding.FragmentSettingBinding
import com.tkw.setting.dialog.LanguageDialog
import com.tkw.setting.dialog.UnitDialog
import com.tkw.ui.dialog.CustomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WaterSettingFragment : Fragment()
    , AccessTokenManager<ActivityResultLauncher<IntentSenderRequest>,
            AuthorizationResult> by GoogleAccessTokenManager() {

    private var dataBinding by autoCleared<FragmentSettingBinding>()
    private val viewModel by activityViewModels<SettingViewModel>()

    @Inject
    lateinit var oAuth: Authentication

    private val googleDriveSyncLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if(it != null) {
                try {
                    val authorizationResult = Identity.getAuthorizationClient(requireActivity())
                        .getAuthorizationResultFromIntent(it.data)
                    startBackupService(false, authorizationResult.accessToken)
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }

    private val googleDriveUploadLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if(it != null) {
                try {
                    val authorizationResult = Identity.getAuthorizationClient(requireActivity())
                        .getAuthorizationResultFromIntent(it.data)
                    startBackupService(true, authorizationResult.accessToken)
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }

    private val broadcastReceiver = object: BroadcastReceiver() {
        var animator: ObjectAnimator? = null
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                BackupForeground.ACTION_SERVICE_START -> {
                    animator = syncRotateStart(dataBinding.settingInfo.ivSync)
                }
                BackupForeground.ACTION_SERVICE_STOP -> {
                    val anim = animator
                    if(anim != null && anim.isRunning) {
                        anim.end()
                    }
                }
                BackupForeground.ACTION_SERVICE_ERROR -> {
                    val anim = animator
                    if(anim != null && anim.isRunning) {
                        anim.end()
                    }
                    val errorType = intent.getStringExtra(BackupForeground.EXTRA_ERROR_TYPE)
                    val errorMessage = intent.getStringExtra(BackupForeground.EXTRA_ERROR_MESSAGE)
                    showErrorDialog(errorType, errorMessage)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentSettingBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObserver()
        initListener()

//        Test().nullableInt()
    }

    override fun onStart() {
        super.onStart()
        updateUI(oAuth.isLoggedIn())
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction(BackupForeground.ACTION_SERVICE_START)
        intentFilter.addAction(BackupForeground.ACTION_SERVICE_STOP)
        intentFilter.addAction(BackupForeground.ACTION_SERVICE_ERROR)
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(broadcastReceiver)
    }

    private fun initView() {
        dataBinding.run {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@WaterSettingFragment.viewModel
            executePendingBindings()
        }
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.lastSyncFlow.collect {
                    if(it == -1L || it == 0L) {
                        dataBinding.settingInfo.tvLastSync.text = getString(com.tkw.ui.R.string.setting_last_sync_empty)
                    } else {
                        dataBinding.settingInfo.tvLastSync.text = DateTimeUtils.DateTime.getFormat(
                            DateTimeUtils.DateTime.getLocalDateTime(it))
                    }
                }
            }
        }
    }

    private fun initListener() {
        dataBinding.settingInfo.tvSync.setOnClickListener {
            doLogin()
        }
        dataBinding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
        dataBinding.settingInfo.ivSync.setOnClickListener {
//            cloudStorageUpload()
            viewModel.lastSyncFlow.value.let {
                if(it == -1L) {
                    googleDriveStartSync()
                } else {
                    val currentTime = System.currentTimeMillis()
                    if(currentTime > it + 1000 * 60)
                        googleDriveUpload()
                }
            }

        }

        dataBinding.settingWater.clWaterSettingIntake.setOnClickListener {
            val dialog = WaterIntakeDialog()
            dialog.show(childFragmentManager, dialog.tag)
        }
        dataBinding.settingWater.clWaterSettingCup.setOnClickListener {
            findNavController().deepLinkNavigateTo(requireContext(), DeepLinkDestination.Cup)
        }
        dataBinding.settingWater.clWaterSettingUnit.setOnClickListener {
            lifecycleScope.launch {
                val dialog = UnitDialog(viewModel.unitFlow.first())
                dialog.show(childFragmentManager, dialog.tag)
            }
        }

        dataBinding.settingAlarm.clAlarmSetting.setOnClickListener {
            findNavController().deepLinkNavigateTo(requireContext(), DeepLinkDestination.Alarm)
        }

        dataBinding.settingEtc.clEtcSettingLanguage.setOnClickListener {
            lifecycleScope.launch {
                val dialog = LanguageDialog(viewModel.currentLangFlow.firstOrNull() ?: "")
                dialog.show(childFragmentManager, dialog.tag)
            }
        }
    }

    private fun doLogin() {
        oAuth.signOut()
        oAuth.signIn {
            if(it) {
                updateUI(true)
            }
        }
    }

    private fun showLogoutDialog() {
        val dialog = CustomDialog()
        dialog.show(childFragmentManager, dialog.tag)
        lifecycleScope.launch {
            dialog.withStarted {
                dialog.setTextView(getString(com.tkw.ui.R.string.setting_logout_confirm))
                dialog.setButtonListener(
                    confirmButtonTitle = getString(com.tkw.ui.R.string.ok),
                    cancelAction = {
                        dialog.dismiss()
                    },
                    confirmAction = {
                        dialog.dismiss()
                        oAuth.signOut()
                        updateUI(false)
                    }
                )
            }
        }
    }

    private fun updateUI(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            dataBinding.settingInfo.tvSync.text = oAuth.fetchInfo()?.name ?: "-"
            dataBinding.settingInfo.tvLastSync.visibility = View.VISIBLE
            dataBinding.settingInfo.ivSync.visibility = View.VISIBLE
            dataBinding.btnLogout.visibility = View.VISIBLE
        } else {
            dataBinding.settingInfo.tvSync.text = getString(com.tkw.ui.R.string.setting_sync)
            dataBinding.settingInfo.tvLastSync.visibility = View.GONE
            dataBinding.settingInfo.ivSync.visibility = View.GONE
            dataBinding.btnLogout.visibility = View.GONE
        }
        Glide
            .with(requireContext())
            .load(oAuth.fetchInfo()?.photoUrl)
            .placeholder(com.tkw.ui.R.drawable.account_circle)
            .error(com.tkw.ui.R.drawable.account_circle)
            .fallback(com.tkw.ui.R.drawable.account_circle)
            .apply(RequestOptions().circleCrop())
            .into(dataBinding.settingInfo.ivImage)
    }

//    private fun cloudStorageUpload() {
//        val storage = CloudStorage()
//        val file = File(requireContext().filesDir, "default.realm")
//        storage.upload(oAuth.fetchInfo()?.uId ?: "", file, file.name)
//    }

    private fun googleDriveStartSync() {
        getAccessTokenAsync(
            requireContext().applicationContext,
            googleDriveSyncLauncher
        ) {
            if(it.accessToken != null) {
                startBackupService(false, it.accessToken)
            }
        }
    }

    private fun googleDriveUpload() {
        getAccessTokenAsync(
            requireContext().applicationContext,
            googleDriveUploadLauncher
        ) {
            if(it.accessToken != null) {
                startBackupService(true, it.accessToken)
            }
        }
    }

    private fun startBackupService(isUpdate: Boolean, accessToken: String?) {
        Intent(requireActivity(), BackupForeground::class.java).apply {
            putExtra(BackupForeground.EXTRA_IS_UPDATE, isUpdate)
            putExtra(BackupForeground.EXTRA_ACCESS_TOKEN, accessToken)
            requireActivity().startForegroundService(this)
        }
    }

    private fun syncRotateStart(view: View): ObjectAnimator {
        val currentDegree = view.rotation
        val anim = ObjectAnimator.ofFloat(view, View.ROTATION, currentDegree, currentDegree + 360f)
            .setDuration(1000)
        anim.repeatCount = ValueAnimator.INFINITE
        anim.start()
        return anim
    }

    private fun syncRotateStop(animator: ObjectAnimator) {
        animator.cancel()
    }

    private fun showErrorDialog(errorType: String?, errorMessage: String?) {
        val dialog = CustomDialog()
        dialog.show(childFragmentManager, dialog.tag)
        lifecycleScope.launch {
            dialog.withStarted {
                val message = when (errorType) {
                    "quota_exceeded" -> getString(com.tkw.ui.R.string.backup_error_quota_exceeded)
                    "auth_error" -> getString(com.tkw.ui.R.string.backup_error_auth)
                    "network_error" -> getString(com.tkw.ui.R.string.backup_error_network)
                    "permission_denied" -> getString(com.tkw.ui.R.string.backup_error_permission)
                    else -> getString(com.tkw.ui.R.string.backup_error_unknown)
                }
                dialog.setTextView(message)
                dialog.setButtonListener(
                    confirmButtonTitle = getString(com.tkw.ui.R.string.ok),
                    confirmAction = {
                        dialog.dismiss()
                    }
                )
            }
        }
    }
}