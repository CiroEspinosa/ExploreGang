package com.example.exploregang.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.exploregang.R
import com.example.exploregang.data.repository.UserSession
import com.example.exploregang.databinding.FragmentLoginBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.fragmentLoginTvRegister.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_fragmentLogin_to_fragmentSingUp)
        }
        binding.fragmentLoginTvSkip.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_fragmentLogin_to_fragmentHome)
        }
        return binding.root

    }
    private fun checkLogin(email: String, password: String) {
        // Oculta el teclado virtual
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)

        // Verifica que el usuario haya ingresado un correo y una contraseña
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(requireContext(), "Debes ingresar un correo y una contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        // Verifica que el correo tenga un formato válido
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Debes ingresar un correo válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Verifica que la contraseña tenga al menos 6 caracteres
        if (password.length < 6) {
            Toast.makeText(requireContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }
        binding.loading.visibility = View.VISIBLE
        // Intenta realizar el inicio de sesión
        UserSession.loginUser(email, password,requireContext(),this,binding)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel =
            ViewModelProvider(this)[LoginViewModel::class.java]

        val usernameEditText = binding.etEmail
        val passwordEditText = binding.etPassword
        val loginButton = binding.btnLogin

        loginButton.setOnClickListener {

            checkLogin(usernameEditText.text.toString(), passwordEditText.text.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<BottomNavigationView>(R.id.navigation_view)?.visibility = View.GONE
    }





}