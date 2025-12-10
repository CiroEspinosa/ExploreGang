package com.example.exploregang.ui.login


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.exploregang.R
import com.example.exploregang.data.prefs.UserPrefsManager
import com.example.exploregang.data.prefs.UserPrefsManager.isUserLogged
import com.example.exploregang.data.prefs.UserPrefsManager.saveUserSession
import com.example.exploregang.data.repository.UserRepository
import com.example.exploregang.data.repository.UserRepository.userSession
import com.example.exploregang.databinding.DialogCustomBinding
import com.example.exploregang.databinding.FragmentLoginBinding
import com.example.exploregang.dialog.customDialog.CustomDialog
import com.example.exploregang.util.Extensions.showError
import com.example.exploregang.util.UserResults
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException


class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {


        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.fragmentLoginTvRegister.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_fragmentLogin_to_fragmentSingUp)
        }
        binding.fragmentLoginTvSkip.setOnClickListener {
            UserPrefsManager.quitUserSession(requireContext())
            UserRepository.logout()
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_fragmentLogin_to_fragmentHome)
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //showDialog()

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

        binding.apply {
            scrollView.postDelayed({
                if (isAdded) {
                    etEmail.requestFocus()
                }
            }, 150)


            tvForgotPass.setOnClickListener {
                UserRepository.sendPasswordResetEmail(etEmail.text.toString(), onSuccess = {
                    Toast.makeText(
                        context, R.string.email_sent, Toast.LENGTH_SHORT
                    ).show()
                }, onFailure = {
                    Toast.makeText(
                        context, R.string.email_not_sent, Toast.LENGTH_SHORT
                    ).show()
                }, onNoEmail = { etEmail.showError(tilEmail, R.string.empty_email_forgot_pass) })
            }

            btnLogin.setOnClickListener {
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                when (loginViewModel.checkLoginCredentials(email, password)) {
                    UserResults.EMAILFORMAT -> {
                        etEmail.showError(tilEmail, R.string.email_invalid)
                        //   Toast.makeText(requireContext(), R.string.email_invalid, Toast.LENGTH_SHORT).show()
                    }
                    UserResults.EMAILANDPASSEMPTY -> {
                        etEmail.showError(tilEmail, R.string.email_pass_empty)
                        // Toast.makeText(requireContext(), R.string.email_pass_empty, Toast.LENGTH_SHORT).show()
                    }
                    UserResults.PASSEMPTY -> {
                        etPassword.showError(tilPassword, R.string.pass_empty)
                        //Toast.makeText(requireContext(), R.string.pass_empty, Toast.LENGTH_SHORT).show()
                    }
                    UserResults.CORRECTCREDENTIALS -> {
                        binding.loading.isVisible = true
                        UserRepository.loginUser(email, password, onSuccess = {
                            saveUserSession(requireContext())
                            NavHostFragment.findNavController(requireParentFragment())
                                .navigate(R.id.action_fragmentLogin_to_fragmentHome)
                        }, onFailure = { exception ->
                            when (exception) {
                                is FirebaseAuthInvalidUserException -> {
                                    // El usuario no existe
                                    Toast.makeText(
                                        context, R.string.user_not_found, Toast.LENGTH_SHORT
                                    ).show()
                                }
                                is FirebaseAuthInvalidCredentialsException -> {
                                    // La contraseña es incorrecta
                                    Toast.makeText(
                                        context, R.string.incorrect_pass, Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else -> {
                                    Toast.makeText(
                                        context, R.string.login_failed, Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            binding.loading.isVisible = false
                            binding.btnLogin.isEnabled = true
                        }, onFailureDownload = {
                            /*  Toast.makeText(
                                  context, R.string.fail_getuser, Toast.LENGTH_SHORT
                              ).show()*/
                            binding.btnLogin.isEnabled = true
                            binding.loading.isVisible = false
                            UserRepository.logout()
                        })
                    }
                    else -> {

                    }
                }
            }
        }

    }

    private fun showDialog() {
        val dialogCustom = CustomDialog()
        dialogCustom.onBind = { binding: DialogCustomBinding ->
            binding.tvMessage.visibility = View.GONE
            binding.tvTitle.text = getString(R.string.drawer_text_logout)
            binding.acceptBtnCd.setText(android.R.string.ok)
            binding.acceptBtnCd.visibility = View.VISIBLE
            binding.cancelBtnCd.setText(android.R.string.cancel)
            binding.acceptBtnCd.setOnClickListener { v: View? ->
                dialogCustom.dismiss()

            }
            null
        }

        dialogCustom.show(parentFragmentManager, "customDialog")


    }

    override fun onResume() {
        super.onResume()
        if (userSession != null&&isUserLogged(requireContext())) {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_fragmentLogin_to_fragmentHome)
        }
        requireActivity().findViewById<BottomNavigationView>(R.id.navigation_view)?.visibility =
            View.GONE
    }


}