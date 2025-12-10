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
                /*  // check surname
                    if (surname!!.isEmpty()) {
                        return SingUpResult.SURNAMEEMPTY
                    }

                    // nick
                    if (nick!!.isEmpty()) {

                        return SingUpResult.NICKEMPTY
                    }
 */
                // check dni


                // check dob
                if (dob != null && dob is Date) {
                    if (dob!!.after(Date())) {
                        return UserResults.FUTUREBIRTH
                    }
                } else {
                    return UserResults.NODATEBRITH
                }

                val isValid = !isPhoneValid(phone)
                if (isValid && phone != "") {
                    return UserResults.INVALIDPHONE
                }
                if (email!!.length < 3) {
                    return UserResults.EMAILSHORT
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    return UserResults.EMAILFORMAT
                }
                if (password != null) {
                    if (!isPasswordValid(password!!)) {
                        return UserResults.PASSWORDSHORT
                    }
                }
                // check password equals
                if (!password.equals(passwordRepeat) && password != null) {
                    return UserResults.WRONGPASSWORD
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