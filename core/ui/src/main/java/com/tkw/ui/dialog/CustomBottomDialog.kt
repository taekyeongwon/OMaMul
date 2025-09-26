package com.tkw.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tkw.common.autoCleared
import com.tkw.ui.databinding.CustomBottomDialogBinding

/**
 * 해당 클래스 상속 시 onCreateView 재정의
 * return super.onCreateView(inflater, container, savedInstanceState) 해줘야 함.
 */
abstract class CustomBottomDialog<T: ViewBinding>: BottomSheetDialogFragment(), BottomExpand by BottomExpandImpl() {
    private var dataBinding by autoCleared<CustomBottomDialogBinding>()
    abstract var childBinding: T
    abstract var buttonCount: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        onSetBottomBehavior(dialog)
        dataBinding = CustomBottomDialogBinding.inflate(inflater, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView(childBinding.root)
    }

    private fun setView(view: View) {
        dataBinding.llParent.addView(view)
        when(buttonCount) {
            0 -> {
                dataBinding.btnSave.visibility = View.GONE
                dataBinding.btnCancel.visibility = View.GONE
                dataBinding.btnContainer.visibility = View.GONE
            }
            1 -> {
                dataBinding.btnCancel.visibility = View.GONE
                dataBinding.btnContainer.visibility = View.VISIBLE
            }
            else -> {
                dataBinding.btnSave.visibility = View.VISIBLE
                dataBinding.btnCancel.visibility = View.VISIBLE
                dataBinding.btnContainer.visibility = View.VISIBLE
            }
        }
    }

    fun setButtonListener(
        cancelAction: () -> Unit = {},
        confirmAction: () -> Unit = {}
    ) {
        dataBinding.btnCancel.setOnClickListener { cancelAction() }
        dataBinding.btnSave.setOnClickListener { confirmAction() }
    }

    fun setButtonTitle(
        cancelTitle: String? = null,
        confirmTitle: String? = null
    ) {
        cancelTitle?.let { dataBinding.btnCancel.text = it }
        confirmTitle?.let { dataBinding.btnSave.text = it }
    }
}