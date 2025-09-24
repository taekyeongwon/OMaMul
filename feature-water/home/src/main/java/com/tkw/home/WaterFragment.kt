package com.tkw.home

import android.animation.ValueAnimator
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.tkw.common.autoCleared
import com.tkw.common.util.DateTimeUtils
import com.tkw.common.util.DimenUtils
import com.tkw.domain.model.DayOfWater
import com.tkw.domain.model.Water
import com.tkw.home.adapter.CupPagerAdapter
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
    private lateinit var cupPagerAdapter: CupPagerAdapter

    private var lottieHeight = 0f
    private var dayOfWater: DayOfWater? = null

    private val clickScrollListener: (Int) -> Unit = { position ->
        scrollToPosition(position, true)
    }

    private val addListener = {
//        val lastPosition = cupPagerAdapter.itemCount - 1
//        if(dataBinding.vpCupList.currentItem == lastPosition) {
//            //add버튼 선택했고, snap된 상태면 관리화면 이동
            findNavController().deepLinkNavigateTo(requireContext(), DeepLinkDestination.Cup)
//        } else {
//            //snap되지 않은 상태면 맨 마지막으로 스크롤
//            scrollToPosition(lastPosition, true)
//        }
    }

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

    override fun onPause() {
        super.onPause()
        viewModel.cupPagerScrollPosition.value = dataBinding.vpCupList.currentItem
    }

    private fun initBinding() {
        dataBinding.run {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@WaterFragment.viewModel
            executePendingBindings()
        }
    }

    private fun initView() {
        initViewPager()
        initItemMenu()
        initLottie()
    }

    /**
     * PageTransformer
     * 현재 선택된 page기준 position은 0
     * offsetPx -> 카드 내부 ViewPager 영역에서 실제 아이템 크기 제외한 만큼의 길이
     * 각 page 포지션 값에 -offsetPx 곱한 만큼 옮긴다.
     */
    private fun initViewPager() {
        cupPagerAdapter = CupPagerAdapter(clickScrollListener)

        val pageMarginPx = DimenUtils.dpToPx(requireContext(), 10)
        val pagerWidth = DimenUtils.dpToPx(requireContext(), 100)
        val screenWidth = resources.displayMetrics.widthPixels
        val cardPadding = DimenUtils.dpToPx(requireContext(), 16) * 2 // card_cup_selector padding 좌우 16dp씩
        val containerPadding = DimenUtils.dpToPx(requireContext(), 16) * 2 // fragment padding 좌우 16dp씩
        val availableWidth = screenWidth - cardPadding - containerPadding
        val offsetPx = availableWidth - pageMarginPx - pagerWidth

        dataBinding.vpCupList.apply {
            adapter = cupPagerAdapter
            offscreenPageLimit = 3
            setPageTransformer { page, position ->
                page.translationX = position * -offsetPx
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cupListLiveData.collect {
                    cupPagerAdapter.submitList(it) {
                        dataBinding.vpCupList.doOnLayout { snapSavedPosition() }
                    }
                }
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

    /**
     * PageTransformer에서 page 포지션 이동이 적용되어 있고,
     * setCurrentItem(smoothFlag = false)를 호출하는 경우
     * 항목이 2~3개 일 때 살짝 스크롤 하고 나면 뷰가 그려지는 현상 발생
     */
    private fun snapSavedPosition() {
        if(cupPagerAdapter.itemCount > 0) {
            val savedPosition = viewModel.cupPagerScrollPosition.value ?: 0
            scrollToPosition(savedPosition, false)
        }
    }

    private fun scrollToPosition(position: Int, smoothFlag: Boolean) {
        dataBinding.vpCupList.setCurrentItem(position, smoothFlag)
        if(!smoothFlag) {
            dataBinding.vpCupList.run {
                //PageTransformer의 transformPage 메서드가 제대로 발생하지 않았을 때 호출
                post { requestTransform() }
            }
        }
    }

    private fun initListener() {
        dataBinding.btnAddWater.setOnClickListener {
            val currentPosition = dataBinding.vpCupList.currentItem
            if(currentPosition < cupPagerAdapter.itemCount) {
                val currentCup = cupPagerAdapter.currentList[currentPosition]
                viewModel.addCount(currentCup.cupAmount, DateTimeUtils.DateTime.getToday())
            }
        }

        dataBinding.btnRemoveWater.setOnClickListener {
            if(!countObject.isNullOrEmpty()) {
                viewModel.removeCount(countObject!!.last())
            }
        }

        // 관리 버튼 클릭 이벤트
        dataBinding.btnManageCups.setOnClickListener {
            addListener()
        }
    }
}