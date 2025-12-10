package com.example.exploregang.ui.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.exploregang.util.UserResults

class LoginViewModel:ViewModel() {

   fun checkLoginCredentials(email: String, password: String):UserResults{


        // Verifica que el usuario haya ingresado un correo y una contraseña
        if (email.isEmpty() && password.isEmpty()) {
            return UserResults.EMAILANDPASSEMPTY
        }

       if (password.isEmpty()) {
           return UserResults.PASSEMPTY
       }

        // Verifica que el correo tenga un formato válido
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return UserResults.EMAILFORMAT
        }
       return UserResults.CORRECTCREDENTIALS

    }
}
