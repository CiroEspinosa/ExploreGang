package com.example.exploregang.ui.changePassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.exploregang.data.repository.UserRepository
import com.example.exploregang.util.UserResults
import com.example.exploregang.util.Utils

class ChangePasswordViewModel : ViewModel() {
    private val _changePasswordResult = MutableLiveData<UserResults>()
    val changePasswordResult: LiveData<UserResults>
        get() = _changePasswordResult

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        repeatPassword: String,
    ) {
        // Realiza la validación de las contraseñas ingresadas
        val match = newPassword == repeatPassword
        if (currentPassword.isEmpty()) {
            _changePasswordResult.value = UserResults.PASSEMPTY
        } else if (!Utils.isPasswordValid(newPassword)) {
            _changePasswordResult.value = UserResults.PASSWORDFORMAT
        } else if (!match) {
            _changePasswordResult.value = UserResults.PASSWORDSDONTMATCH
        } else {

            UserRepository.changePassword(
                currentPassword,
                newPassword,
                onSuccess = {
                    _changePasswordResult.value = UserResults.SUCCESS
                }, onErrorReautentication = {
                    _changePasswordResult.value = UserResults.ERRORREAUTENTICATION
                }, onErrorChangePassword = {
                    _changePasswordResult.value=UserResults.ERRORCHANGEPASSWORD
                }
            )
        }
    }
}


