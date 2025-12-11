package com.example.exploregang.ui.singup


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.exploregang.R
import com.example.exploregang.data.model.UserRegisterFields
import com.example.exploregang.data.prefs.UserPrefsManager
import com.example.exploregang.data.repository.UserRepository
import com.example.exploregang.databinding.FragmentSingUpBinding
import com.example.exploregang.util.Extensions.showError
import com.example.exploregang.util.UserResults
import com.example.exploregang.util.Utils
import com.example.exploregang.util.Utils.abrirGaleria
import com.example.exploregang.util.Utils.showDateTimePickerDialog
import com.google.firebase.auth.FirebaseAuthUserCollisionException


class SingUpFragment() : Fragment() {
    private var imageId: String? = ""
    private val registerViewModel by activityViewModels<SingUpViewModel>()

    private lateinit var binding: FragmentSingUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSingUpBinding.inflate(layoutInflater)
        binding.apply {
            binding.ivDeletePhoto.isVisible = false
            Glide.with(requireContext())
                .load(R.drawable.ic_wojak)
                .circleCrop()
                .into(binding.ivUserPhoto)

            btnBack.setOnClickListener {
                NavHostFragment.findNavController(requireParentFragment()).navigateUp()
            }
            btnSingUp.setOnClickListener {
                requireActivity().currentFocus?.clearFocus()
                singUp()
            }
            ivUserPhoto.setOnClickListener {
                abrirGaleria(pickMedia)
            }
        }
        return binding.root
    }

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Utils.setImageWithUri(uri,binding.ivUserPhoto){
                binding.ivDeletePhoto.isVisible = true
                binding.ivDeletePhoto.setOnClickListener {
                    binding.ivDeletePhoto.isVisible = false
                    imageId = ""
                    Glide.with(requireContext())
                        .load(R.drawable.ic_wojak)
                        .circleCrop()
                        .into(binding.ivUserPhoto)
                }
            }
            Utils.saveImage(uri) { imageId: String? ->
                this.imageId = imageId
            }
        } else {
        }
    }


    private fun singUp() {
        binding.apply {

            val userRegisterFields = UserRegisterFields()
            userRegisterFields.apply {
                name = etName.text.toString().trim()
                /*   nick = etNick.text.toString().trim()
                   surname = etSurname.text.toString().trim()*/
                email = etEmail.text.toString().trim()
                password = etPassword.text.toString().trim()

                /* tutorName = etTutorName.text.toString()
                 tutorEmail = etTutorEmail.text.toString().trim()
                 tutorPhone = etTutorPhone.text.toString().trim()*/


                photo = imageId


                when (registerViewModel.checkFields(userRegisterFields)) {
                    UserResults.SUCCESS -> {
                        loading.isVisible = true
                        UserRepository.singUpUser(
                            userRegisterFields!!,
                            onFailureSingUp = { exception ->
                                if (exception is FirebaseAuthUserCollisionException) {
                                    //Toast.makeText(context, R.string.email_alredy_used, Toast.LENGTH_SHORT).show()
                                    etEmail.showError(tilEmail, R.string.email_alredy_used)
                                    loading.isVisible = false
                                } else {
                                    // Otro tipo de error durante el registro
                                    Toast.makeText(
                                        context,
                                        R.string.failed_register_user,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loading.isVisible = false
                                }
                            },
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    R.string.successful_registration,
                                    Toast.LENGTH_SHORT
                                ).show()
                                UserPrefsManager.saveUserSession(requireContext())
                                NavHostFragment.findNavController(requireParentFragment())
                                    .navigate(R.id.action_fragmentSingUp_to_fragmentHome)
                            },
                            onFailureUpload = {
                                Toast.makeText(
                                    context, R.string.registration_failed, Toast.LENGTH_SHORT
                                ).show()
                                loading.isVisible = false
                            })
                    }
                    UserResults.NAMEMPTY -> {
                        etName.showError(tilName, R.string.name_empty)
                        // Toast.makeText(requireContext(), R.string.name_empty,Toast.LENGTH_SHORT).show()
                    }
                    UserResults.EMAILFORMAT -> {

                        etEmail.showError(tilEmail, R.string.register_message_email_incorrect)
                        //  Toast.makeText(requireContext(), R.string.register_message_email_incorrect, Toast.LENGTH_SHORT).show()
                    }
                    UserResults.PASSWORDSHORT -> {

                        etPassword.showError(tilPassword, R.string.invalid_password)
                        // Toast.makeText(requireContext(), R.string.invalid_password, Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        Toast.makeText(
                            requireContext(),
                            R.string.something_happened,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        }
    }


}