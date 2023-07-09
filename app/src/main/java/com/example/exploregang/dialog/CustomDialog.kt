package com.example.exploregang.dialog

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.exploregang.R
import com.example.exploregang.databinding.DialogCustomBinding


class CustomDialog(
    @StringRes
    var title: Int? = null,
    @StringRes
    var message: Int? = null,
    var isDefaultButtonVisible: Boolean = true,
    @StringRes
    var defaultButtonText: Int = R.string.ok,
    var defaultButtonOnClick: (() -> Unit)? = null,
    @DrawableRes
    var defaultButtonBackground: Int? = null,
    var defaultButtonBackgroundColor: Int? = null,
    var defaultButtonTextColor: Int? = null,
    var defaultButtonStyle: Int? = null,
    var verticalButtons: Boolean = false,
    var outOfDialogClose: Boolean = true,
    var isButtonCloseVisible: Boolean = true,
    var isCheckBoxVisible:Boolean=false,
    var isEditTextVisible: Boolean = false,
    var editTextHint: Int = R.string.write_something,
    var checkBoxText:Int?=R.string.do_not_show_again,

    private val dynamicButtons: MutableList<Button> = mutableListOf()
) : DialogFragment() {
    private lateinit var binding: DialogCustomBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCustomBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initUi()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCancelable(outOfDialogClose)
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_rounded_corners)
        return dialog
    }

    private fun initUi() {
        binding.apply {
            llcButtons.orientation = LinearLayoutCompat.HORIZONTAL

            ivClose.setOnClickListener { dismiss() }
            ivClose.isVisible = isButtonCloseVisible

            tilMessage.isVisible = isEditTextVisible
            cbDefault.isVisible=isCheckBoxVisible
            etMessage.setHint(editTextHint)

            btnDefault.isVisible = isDefaultButtonVisible
            btnDefault.setText(defaultButtonText)
            btnDefault.setOnClickListener {
                defaultButtonOnClick?.invoke()
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
            if(checkBoxText!=null){
                cbDefault.setText(checkBoxText!!)
            }
            if (defaultButtonBackground != null) {
                val drawable: Drawable? =
                    ContextCompat.getDrawable(requireContext(), defaultButtonBackground!!)
                btnDefault.background = drawable
            }
            if (defaultButtonBackgroundColor != null) {
                btnDefault.setBackgroundColor(defaultButtonBackgroundColor!!)
            }
            if (defaultButtonTextColor != null) {
                btnDefault.setTextColor(defaultButtonTextColor!!)
            }
            if (defaultButtonStyle != null) {
                btnDefault.setBackgroundResource(defaultButtonStyle!!)
            }

            if (verticalButtons) {
                llcButtons.orientation = LinearLayoutCompat.VERTICAL
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

    fun getDefaultButton(): Button {
        return binding.btnDefault
    }

    fun addButton(button: Button) {
        dynamicButtons.add(button)
    }

    fun addButtons(buttons: MutableList<Button>) {
        dynamicButtons.addAll(buttons)
    }
}