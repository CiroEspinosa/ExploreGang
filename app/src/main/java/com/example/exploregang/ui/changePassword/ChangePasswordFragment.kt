package com.example.exploregang.ui.changePassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.exploregang.R
import com.example.exploregang.data.repository.UserRepository
import com.example.exploregang.databinding.FragmentChangePasswordBinding
import com.example.exploregang.util.Extensions.showError
import com.example.exploregang.util.UserResults


class ChangePasswordFragment : DialogFragment() {
    private lateinit var binding: FragmentChangePasswordBinding
    private lateinit var changePasswordViewModel: ChangePasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePasswordBinding.inflate(layoutInflater)
        changePasswordViewModel = ViewModelProvider(this)[ChangePasswordViewModel::class.java]
        changePasswordViewModel.changePasswordResult.observe(viewLifecycleOwner) { passwordsMatch ->
            binding.loading.isVisible=false
            when (passwordsMatch) {
                UserResults.SUCCESS -> {
                    dismiss()
                    Toast.makeText(context, R.string.success_change_password, Toast.LENGTH_SHORT)
                        .show()
                }
                UserResults.ERRORREAUTENTICATION -> {
                    Toast.makeText(context, R.string.error_reautentication, Toast.LENGTH_SHORT)
                        .show()
                }
                UserResults.ERRORCHANGEPASSWORD -> {
                    Toast.makeText(context, R.string.error_change_password, Toast.LENGTH_SHORT)
                        .show()
                }
                UserResults.PASSEMPTY -> {
                    binding.etCurrentPassword.showError(binding.tilCurrentPassword,R.string.pass_empty)
                   // Toast.makeText(context, R.string.pass_empty, Toast.LENGTH_SHORT).show()
                }
                UserResults.PASSWORDFORMAT -> {
                    binding.etNewPassword.showError(binding.tilNewPassword, R.string.invalid_password)
                  //  Toast.makeText(context, R.string.invalid_password, Toast.LENGTH_SHORT).show()
                }
                UserResults.PASSWORDSDONTMATCH -> {
                    binding.etRepeatPassword.showError(binding.tilRepeatPassword,R.string.register_message_passwords_different)
                    //Toast.makeText(context, R.string.register_message_passwords_different, Toast.LENGTH_SHORT).show()
                }
                else -> {

                }
            }
        }
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            ivClose.setOnClickListener { dismiss() }
            tvForgotPass.setOnClickListener {
                UserRepository.sendPasswordResetEmail(
                    UserRepository.currentUser!!.email!!,
                    onSuccess = {
                        Toast.makeText(
                            context, R.string.email_sent, Toast.LENGTH_SHORT
                        ).show()
                    },
                    onFailure = {
                        Toast.makeText(
                            context, R.string.email_not_sent, Toast.LENGTH_SHORT
                        ).show()
                    }, onNoEmail = {
                        Toast.makeText(requireContext(),R.string.email_not_found,Toast.LENGTH_SHORT)
                    })
            }
            binding.btnAccept.setOnClickListener {
                binding.loading.isVisible=true
                val currentPassword = binding.etCurrentPassword.text.toString()
                val newPassword = binding.etNewPassword.text.toString()
                val repeatPassword = binding.etRepeatPassword.text.toString()
                changePasswordViewModel.changePassword(currentPassword, newPassword, repeatPassword)
            }
        }
    }
}