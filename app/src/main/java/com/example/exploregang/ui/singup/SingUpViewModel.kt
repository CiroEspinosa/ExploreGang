package com.example.exploregang.ui.singup

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.exploregang.data.model.UserRegisterFields
import com.example.exploregang.util.UserResults
import com.example.exploregang.util.Utils
import java.util.*

class SingUpViewModel : ViewModel() {


    fun checkFields(
        userRegisterFields: UserRegisterFields
    ): UserResults {
        userRegisterFields.apply {
            Utils.apply {
                if (name!!.isEmpty()) {
                    return UserResults.NAMEMPTY
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    return UserResults.EMAILFORMAT
                }
                if (password != null) {
                    if (!isPasswordValid(password!!)) {
                        return UserResults.PASSWORDSHORT
                    }
                }
               /* if (!isAdult(dob!!)) {
                    if (tutorName!!.isEmpty()) {
                        return UserResults.TUTORNAMESHORT
                    }
                    if (!isPhoneValid(tutorPhone) && tutorPhone != "") {
                        return UserResults.TUTORPHONEINVALID
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(tutorEmail).matches()) {
                        return UserResults.TUTOREMAILSHORT
                    }
                    // check tutor email @ character
                    if (!Patterns.EMAIL_ADDRESS.matcher(tutorEmail).matches()) {
                        return UserResults.TUTOREMAILSHORT
                    }
                }*/
            }




            return UserResults.SUCCESS
        }
    }


}