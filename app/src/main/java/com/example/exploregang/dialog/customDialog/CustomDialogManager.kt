package com.example.exploregang.dialog.customDialog

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.exploregang.R
import com.google.android.material.button.MaterialButton

object CustomDialogManager {
    fun showDialogKeepSesion(fragmentManager: Fragment, layoutInflater: LayoutInflater) {
        val customDialog = CustomDialog { dialogBinding ->
            dialogBinding.cancelBtnCd.setText(R.string.no)
        }
        customDialog.title = R.string.stay_signed_in
        customDialog.message = R.string.stay_signed_in_message
        val button= getButton(R.id.dcb_btnAcceptInvitation,layoutInflater)
        button.setText(R.string.yes)
        customDialog.addButton(button)
        customDialog.show(fragmentManager.parentFragmentManager, "customDialog")
    }

    fun getButton(buttonId:Int,layoutInflater: LayoutInflater): Button {
        val view = layoutInflater.inflate(R.layout.dialog_custom_buttons, null)
        val button:MaterialButton=view.findViewById(buttonId)
        val parent =button.parent as? ViewGroup
        parent?.removeView(button)
        return button
    }
}
