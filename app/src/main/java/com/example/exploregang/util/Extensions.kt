package com.example.exploregang.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout

object Extensions {

    fun EditText.showError(textInputLayout: TextInputLayout, message:Int) {

            textInputLayout.requestFocus()
            textInputLayout.error = (resources.getString(message))

        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayout.error = null
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }


}
