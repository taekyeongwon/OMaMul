package com.tkw.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.tkw.common.autoCleared
import com.tkw.ui.databinding.CustomDialogViewBinding

open class CustomDialog: DialogFragment(), DialogResize by DialogResizeImpl() {
    private var dataBinding by autoCleared<CustomDialogViewBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = CustomDialogViewBinding.inflate(layoutInflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dataBinding.root
    }

    override fun onResume() {
        super.onResume()
        onResize(this, 0.9f)
    }

    fun setView(view: View, isOneButton: Boolean = false) {
        dataBinding.llParent.addView(view)
        if(isOneButton) {
            dataBinding.btnCancel.visibility = View.GONE
        }
    }

    fun setTextView(title: String, message: String) {
        dataBinding.clTextView.visibility = View.VISIBLE
        dataBinding.tvTitle.text = title
        dataBinding.tvMessage.text = message
    }

    fun setTextView(message: String) {
        dataBinding.clTextView.visibility = View.VISIBLE
        dataBinding.tvTitle.visibility = View.GONE
        dataBinding.tvMessage.text = message
    }

    fun setButtonListener(
        cancelButtonTitle: String = "",
        confirmButtonTitle: String = "",
        cancelAction: () -> Unit = {},
        confirmAction: () -> Unit = {}
    ) {
        if(cancelButtonTitle.isNotBlank()) dataBinding.btnCancel.text = cancelButtonTitle
        if(confirmButtonTitle.isNotBlank()) dataBinding.btnSave.text = confirmButtonTitle
        dataBinding.btnCancel.setOnClickListener { cancelAction() }
        dataBinding.btnSave.setOnClickListener { confirmAction() }
    }
}