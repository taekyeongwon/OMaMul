package com.tkw.home

import android.animation.ValueAnimator
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.graphics.PointF
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.core.animation.doOnEnd
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.tkw.common.autoCleared
import com.tkw.common.util.DateTimeUtils
import com.tkw.common.util.DimenUtils
import com.tkw.domain.model.DayOfWater
import com.tkw.domain.model.Water
import com.tkw.home.databinding.FragmentWaterBinding
import com.tkw.home.dialog.WaterIntakeDialog
import com.tkw.navigation.DeepLinkDestination
import com.tkw.navigation.deepLinkNavigateTo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WaterFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentWaterBinding>()
    private val viewModel: WaterViewModel by activityViewModels()
    private var countObject: List<Water>? = null
    private var lottieHeight = 0f
    private var dayOfWater: DayOfWater? = null
    private var isFabMenuOpen = false
    private var currentCup: com.tkw.domain.model.Cup? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentWaterBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        initView()
        initObserver()
        initListener()
    }


    private fun initBinding() {
        dataBinding.run {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@WaterFragment.viewModel
            executePendingBindings()
        }
    }

    private fun initView() {
        initItemMenu()
        initLottie()
        initCurrentCup()
    }

    private fun initCurrentCup() {
        // 현재 선택된 컵 정보를 표시하는 초기화 함수
        lifecycleScope.launch {
            viewModel.cupListLiveData.collect { cupList ->
                if (cupList.isNotEmpty()) {
                    currentCup = cupList.first() // 첫 번째 컵을 기본으로 사용
                    // TODO: 컵 아이콘 또는 이미지 업데이트
                }
            }
        }
    }

    private fun initItemMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId) {
                    R.id.waterIntakeDialog -> {
                        val dialog = WaterIntakeDialog()
                        dialog.show(childFragmentManager, dialog.tag)
                        true
                    }
                    else -> {
                        menuItem.onNavDestinationSelected(findNavController())
                    }
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun initLottie() {
//        dataBinding.lotti.addLottieOnCompositionLoadedListener {
//            lottieHeight = it.bounds.bottom.toFloat()
//        }
        val displayMetrics = resources.displayMetrics
        val density = displayMetrics.density
        lottieHeight = density * 200    //애니메이션 픽셀 높이 값 : 200
    }

    private fun initObserver() {
        //홈 화면은 오늘 날짜의 데이터만 보여주도록 여기서 세팅
        viewModel.setToday()

        lifecycleScope.launch {
            viewModel.amountLiveData.collect {
                val intakeGoal = viewModel.getIntakeAmount()
                val currentIntake = it.getTotalIntakeByDate()
                val prevWater = dayOfWater
                val currentAmount = minOf(currentIntake / intakeGoal.toFloat(), 1.0f)
                val dy = lottieHeight * (1 - currentAmount)

                // Lottie 애니메이션 업데이트
                if(prevWater != null) {
                    val prevAmount = minOf(prevWater.getTotalIntakeByDate() / intakeGoal.toFloat(), 1.0f)
                    val prevDy = lottieHeight * (1 - prevAmount)
                    startWaterAnimation(prevDy, dy)
                } else {
                    startWaterAnimation(lottieHeight, dy)
                }

                // UI 데이터 업데이트
                updateWaterDisplay(currentIntake, intakeGoal)

                dayOfWater = it
                countObject = it.dayOfList
            }
        }


        // 목표량 표시
        lifecycleScope.launch {
            val intakeGoal = viewModel.getIntakeAmount()
            dataBinding.tvTargetAmount.text = "${intakeGoal}${getString(com.tkw.ui.R.string.unit_ml_no_bracket)}"
        }

        // 다음 알람 시간 표시
        // TODO: 알람 기능 연동 시 실제 다음 알람 시간으로 업데이트
        dataBinding.tvNextAlarm.text = getString(com.tkw.ui.R.string.water_alarm_off)
    }

    private fun updateWaterDisplay(currentIntake: Int, intakeGoal: Int) {
        // 목표 대비 비율 계산
        val percentage = if (intakeGoal > 0) {
            ((currentIntake.toFloat() / intakeGoal.toFloat()) * 100).toInt()
        } else 0

        dataBinding.tvGoalRatio.text = getString(com.tkw.ui.R.string.water_goal_ratio, intakeGoal, percentage)

        // 격려 메시지 업데이트
        val remainingAmount = maxOf(0, intakeGoal - currentIntake)
        dataBinding.tvEncouragement.text = when {
            remainingAmount == 0 -> getString(com.tkw.ui.R.string.water_goal_achieved)
            remainingAmount <= 250 -> getString(com.tkw.ui.R.string.water_goal_close, remainingAmount)
            remainingAmount <= 500 -> getString(com.tkw.ui.R.string.water_goal_near, remainingAmount)
            else -> getString(com.tkw.ui.R.string.water_goal_far, remainingAmount)
        }
    }

    private fun startWaterAnimation(startValue: Float, endValue: Float) {
        val animator = ValueAnimator.ofFloat(startValue, endValue)
        animator.addUpdateListener {
            val animatedValue = it.getAnimatedValue() as Float
            dataBinding.lotti.addValueCallback(
                KeyPath("**", "Shape Layer"),
                LottieProperty.TRANSFORM_POSITION
            ) {
                return@addValueCallback PointF(it.startValue.x, animatedValue)
            }
        }
        animator.start()
    }


    private fun initListener() {
        // 메인 플로팅 버튼 클릭 - 물 추가
        dataBinding.fabCurrentCup.setOnClickListener {
            // Pressed 애니메이션
            animateFabPress()

            currentCup?.let { cup ->
                viewModel.addCount(cup.cupAmount, DateTimeUtils.DateTime.getToday())
            }
            // 메뉴가 열려있어도 닫지 않음
        }

        // 메인 플로팅 버튼 롱클릭 - 메뉴 토글
        dataBinding.fabCurrentCup.setOnLongClickListener {
            // 진동 효과
            it.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            toggleFabMenu()
            true
        }

        // 실행 취소 버튼
        dataBinding.fabUndo.setOnClickListener {
            if (!countObject.isNullOrEmpty()) {
                viewModel.removeCount(countObject!!.last())
            }
            // 메뉴를 닫지 않음
        }

        // 설정 버튼
        dataBinding.fabCupSettings.setOnClickListener {
            findNavController().deepLinkNavigateTo(requireContext(), DeepLinkDestination.Cup)
            // 메뉴를 닫지 않음
        }

        // 메인 FAB 컨테이너 클릭 - 메뉴를 닫지 않음
        dataBinding.fabMenuContainer.setOnClickListener {
            // 아무것도 하지 않음 (이벤트 소비)
        }

        // 배경 오버레이 클릭 - 메뉴 닫기
        dataBinding.fabOverlay.setOnClickListener {
            closeFabMenu()
        }
    }

    private fun animateFabPress() {
        val scaleUp = ObjectAnimator.ofFloat(dataBinding.fabCurrentCup, "scaleX", 1f, 1.1f)
        val scaleUpY = ObjectAnimator.ofFloat(dataBinding.fabCurrentCup, "scaleY", 1f, 1.1f)
        val scaleDown = ObjectAnimator.ofFloat(dataBinding.fabCurrentCup, "scaleX", 1.1f, 1f)
        val scaleDownY = ObjectAnimator.ofFloat(dataBinding.fabCurrentCup, "scaleY", 1.1f, 1f)

        val pressSet = AnimatorSet().apply {
            playTogether(scaleUp, scaleUpY)
            duration = 100
        }

        val releaseSet = AnimatorSet().apply {
            playTogether(scaleDown, scaleDownY)
            duration = 100
        }

        val fullAnimation = AnimatorSet().apply {
            playSequentially(pressSet, releaseSet)
        }

        fullAnimation.start()

        // 진동 효과
        dataBinding.fabCurrentCup.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }

    private fun toggleFabMenu() {
        if (isFabMenuOpen) {
            closeFabMenu()
        } else {
            openFabMenu()
        }
    }

    private fun openFabMenu() {
        isFabMenuOpen = true

        // 배경 오버레이 표시
        dataBinding.fabOverlay.apply {
            visibility = View.VISIBLE
            ObjectAnimator.ofFloat(this, "alpha", 0f, 0.5f).apply {
                duration = 200
                interpolator = DecelerateInterpolator()
                start()
            }
        }

        // 메뉴 컨테이너 슬라이드 인 애니메이션 (아래에서 위로)
        dataBinding.fabMenuContainer.apply {
            visibility = View.VISIBLE
            translationY = 200f // 아래에서 시작
            alpha = 0f

            // 슬라이드 인 애니메이션
            ObjectAnimator.ofFloat(this, "translationY", 200f, 0f).apply {
                duration = 300
                interpolator = OvershootInterpolator(0.8f)
                start()
            }

            ObjectAnimator.ofFloat(this, "alpha", 0f, 1f).apply {
                duration = 250
                start()
            }

            // 개별 버튼 애니메이션
            ObjectAnimator.ofFloat(this, "scaleX", 0.5f, 1f).apply {
                duration = 300
                startDelay = 50
                interpolator = OvershootInterpolator()
                start()
            }
            ObjectAnimator.ofFloat(this, "scaleY", 0.5f, 1f).apply {
                duration = 300
                startDelay = 50
                interpolator = OvershootInterpolator()
                start()
            }
        }
    }

    private fun closeFabMenu() {
        isFabMenuOpen = false

        // 배경 오버레이 숨김
        ObjectAnimator.ofFloat(dataBinding.fabOverlay, "alpha", 0.5f, 0f).apply {
            duration = 200
            interpolator = DecelerateInterpolator()
            doOnEnd { dataBinding.fabOverlay.visibility = View.GONE }
            start()
        }

        // 메뉴 컨테이너 슬라이드 아웃 애니메이션 (위에서 아래로)
        dataBinding.fabMenuContainer.apply {
            ObjectAnimator.ofFloat(this, "translationY", 0f, 200f).apply {
                duration = 250
                interpolator = DecelerateInterpolator()
                start()
            }

            ObjectAnimator.ofFloat(this, "alpha", 1f, 0f).apply {
                duration = 200
                start()
            }

            ObjectAnimator.ofFloat(this, "scaleX", 1f, 0.5f).apply {
                duration = 200
                start()
            }

            ObjectAnimator.ofFloat(this, "scaleY", 1f, 0.5f).apply {
                duration = 200
                start()
            }.also {
                it.doOnEnd {
                    visibility = View.GONE
                    translationY = 0f
                    alpha = 1f
                    scaleX = 1f
                    scaleY = 1f
                }
            }
        }
    }
}