package com.tkw.omamul.ui.view.water.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.data.model.CupEntity
import com.tkw.omamul.data.model.WaterEntity
import com.tkw.omamul.databinding.FragmentWaterBinding
import com.tkw.omamul.ui.dialog.WaterIntakeDialog
import com.tkw.omamul.ui.view.water.main.home.adapter.CupPagerAdapter
import com.tkw.omamul.ui.view.water.main.home.adapter.SnapDecoration
import com.tkw.omamul.ui.view.water.main.WaterViewModel
import com.tkw.omamul.util.autoCleared


class WaterFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentWaterBinding>()
    private val viewModel: WaterViewModel by activityViewModels { ViewModelFactory }
    private var countObject: List<WaterEntity>? = null
    private lateinit var cupPagerAdapter: CupPagerAdapter
    private lateinit var snapHelper: PagerSnapHelper

    private var i = 0

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
        snapHelper = PagerSnapHelper()
        cupPagerAdapter = CupPagerAdapter(clickScrollListener, addListener)
        initItemMenu()
        initGlobalLayout()
    }

    private fun initObserver() {
        viewModel.countStreamLiveData.observe(viewLifecycleOwner, Observer {
            countObject = it.dayOfList
//            dataBinding.tvCount.text = "${
//                it.dayOfList.sumOf { water ->
//                    water.amount
//                }
//            }"
        })
    }

    private fun initListener() {
        dataBinding.btnAdd.setOnClickListener {
            viewModel.addCount()
        }

        dataBinding.btnRemove.setOnClickListener {
            if(!countObject.isNullOrEmpty()) {
                viewModel.removeCount(countObject!!.last())
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

    private fun initGlobalLayout() {
        dataBinding.root.let {
            it.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    it.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    dataBinding.rvList.apply {
                        adapter = cupPagerAdapter
                        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        snapHelper.attachToRecyclerView(this)
                        addItemDecoration(SnapDecoration(dataBinding.root.width))
                    }
                }
            })
        }
    }

    private val clickScrollListener: (Int) -> Unit = { position ->
        scrollToPosition(dataBinding.rvList, position)
    }

    private val addListener = {
        val lastPosition = cupPagerAdapter.itemCount - 1
        val touchedView: View? = dataBinding.rvList.layoutManager!!.findViewByPosition(lastPosition)
        if(touchedView != null && isSnapped(touchedView)) {
            addItem()
        } else {
            scrollToPosition(dataBinding.rvList, lastPosition)
        }
    }

    private fun addItem() {
        val item = CupEntity().apply {
            cupName = "test" + i++
        }
        val currentList = cupPagerAdapter.currentList.toMutableList()
        currentList.add(item)
        cupPagerAdapter.submitList(currentList) { snapFirstItemAdded() }
    }

    private fun scrollToPosition(rv: RecyclerView, position: Int) {
        val touchedView: View? = rv.layoutManager!!.findViewByPosition(position)
        if(touchedView != null) {
            val itemWidth = touchedView.measuredWidth
            val centerView = snapHelper.findSnapView(rv.layoutManager)
            val centerPosition = rv.getChildAdapterPosition(centerView!!)

            rv.smoothScrollBy((position - centerPosition) * itemWidth, 0)
        }

    }

    private fun isSnapped(view: View): Boolean {
        val centerView = snapHelper.findSnapView(dataBinding.rvList.layoutManager)
        return view === centerView
    }

    private fun snapFirstItemAdded() {
        if(cupPagerAdapter.itemCount == 2) {
            scrollToPosition(dataBinding.rvList, 0)
        }
    }
}