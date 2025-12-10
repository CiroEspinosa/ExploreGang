package com.example.exploregang.dialog.customDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.StringRes
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import com.example.exploregang.R
import com.example.exploregang.databinding.DialogCustomBinding


class CustomDialog(
    @StringRes
    var title: Int? = null,
    @StringRes
    var message: Int? = null,
    private val dynamicButtons: MutableList<Button> = mutableListOf(),
    var onBind: ((binding: DialogCustomBinding) -> Unit)={},
) : DialogFragment() {


    private lateinit var binding: DialogCustomBinding

    var onDismiss: ()->Unit = {  }

    override fun dismiss() {
        super.dismiss()
        onDismiss()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCustomBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        onBind(binding)
    }

    private fun initUi() {
        binding.apply {
            ivClose.setOnClickListener { dismiss() }
            cancelBtnCd.setText(R.string.cancel)
            cancelBtnCd.setOnClickListener {
                dismiss()
            }
            acceptBtnCd.setText(R.string.accept)
            acceptBtnCd.setOnClickListener{
                dismiss()
            }
            if (dynamicButtons.size > 0) {
                for (button: Button in dynamicButtons) {
                    llcButtons.addView(button)
                }
            }
            if (title != null) {
                tvTitle.setText(title!!)
                tvTitle.isGone = false
            }
            if (message != null) {
                tvMessage.setText(message!!)
                tvMessage.isGone = false

            }

            val svMessageHeight = svMessage.layoutParams.height
            var requestDone = false
            tvMessage.viewTreeObserver.addOnGlobalLayoutListener {
                if (tvMessage.height < svMessageHeight && !requestDone) {
                    svMessage.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    svMessage.requestLayout()
                    requestDone = true
                } else if (!requestDone) {
                    svMessage.layoutParams.height = svMessageHeight
                    svMessage.requestLayout()
                    requestDone = true
                }
            }
        }
    }

    fun addButton(button: Button) {
        dynamicButtons.add(button)
    }
    fun addButtons(buttons: MutableList<Button>) {
        dynamicButtons.addAll(buttons)
    }
}


