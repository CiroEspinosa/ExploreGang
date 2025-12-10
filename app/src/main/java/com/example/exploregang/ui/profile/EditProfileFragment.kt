package com.example.exploregang.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.navigation.fragment.NavHostFragment
import com.example.exploregang.R
import com.example.exploregang.data.model.UserRegisterFields
import com.example.exploregang.data.repository.UserRepository
import com.example.exploregang.data.repository.UserRepository.currentUser
import com.example.exploregang.databinding.FragmentEditProfileBinding

import com.example.exploregang.ui.changePassword.ChangePasswordFragment
import com.example.exploregang.ui.singup.SingUpViewModel
import com.example.exploregang.util.Extensions.showError
import com.example.exploregang.util.UserResults
import com.example.exploregang.util.Utils
import com.example.exploregang.util.Utils.abrirGaleria
import com.example.exploregang.util.Utils.stringToDate
import com.google.android.material.bottomnavigation.BottomNavigationView


class EditProfileFragment : Fragment() {
    private val singUpViewModel = SingUpViewModel()
    private lateinit var binding: FragmentEditProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }


    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Utils.setImageWithUri(uri,  binding.ivUserPhoto){
                binding.ivDeletePhoto.isVisible = true
                binding.ivDeletePhoto.setOnClickListener {
                    binding.ivDeletePhoto.isVisible = false
                    currentUser!!.imageId= ""
                    binding.ivUserPhoto.setImageResource(R.drawable.ic_camera)
                    binding.ivUserPhoto.setPadding(Utils.dpToPx(20, resources))
                }
            }
            Utils.saveImage(uri) { imageId: String? ->
                currentUser!!.imageId = imageId
            }
        } else {
            // El usuario no seleccionó ninguna imagen
        }
    }




    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<BottomNavigationView>(R.id.navigation_view)?.visibility =
            View.GONE
    }

    private fun init() {
        binding.apply {
            loadingP.isVisible = true

            if (currentUser == null ||currentUser!!.imageId.isNullOrEmpty()) {
                ivDeletePhoto.isVisible = false
                ivUserPhoto.setImageResource(R.drawable.ic_camera)
                ivUserPhoto.setPadding(Utils.dpToPx(20, resources))
                ivUserPhoto.isVisible=true
            } else {
                Utils.getImage(currentUser!!.imageId,binding.ivUserPhoto)
                ivDeletePhoto.isVisible = true
            }


            etEmailP.setText(currentUser!!.email)
            etNameP.setText(currentUser!!.name)
            etPhoneP.setText(currentUser!!.phone)
            loadingP.isVisible = false
            btnBackProfile.setOnClickListener {
                NavHostFragment.findNavController(requireParentFragment()).navigateUp()
            }
            tvChangePass.setOnClickListener {
                val changePassDialog = ChangePasswordFragment()
                changePassDialog.show(parentFragmentManager, "changePasswordDialog")
            }
            ivUserPhoto.setOnClickListener {
                abrirGaleria(pickMedia)
            }
            ivDeletePhoto.setOnClickListener {
                currentUser!!.imageId = ""
                ivUserPhoto.setImageResource(R.drawable.usericon)
                ivUserPhoto.setPadding(Utils.dpToPx(20, resources))
                ivDeletePhoto.isVisible = false
            }

            etEmailP.isEnabled = false
            etDob.setText(Utils.dateToString(currentUser!!.dob))
            etDob.setOnClickListener { Utils.showDateTimePickerDialog(etDob, requireContext()) }
            cbIsPublic.isChecked= currentUser!!.isPublic!!
            btnUpdate.setOnClickListener {

                requireActivity().currentFocus?.clearFocus()
                //etDobP.setOnClickListener { }
                updateUser()
            }
        }
    }


    private fun updateUser() {
        binding.apply {

            val userRegisterFields = UserRegisterFields()
            userRegisterFields.apply {
                name = etNameP.text.toString().trim()
                email = etEmailP.text.toString().trim()
                password = null
                passwordRepeat = null
                phone = etPhoneP.text.toString().trim()
                dob=stringToDate(etDob.text.toString())
                isPublic=cbIsPublic.isChecked


                when (singUpViewModel.checkFields(userRegisterFields)) {
                    UserResults.SUCCESS -> {
                        loadingP.isVisible = true
                        currentUser!!.name=name
                        currentUser!!.dob=dob
                        currentUser!!.isPublic=isPublic
                        currentUser!!.phone=phone
                        NavHostFragment.findNavController(this@EditProfileFragment)
                            .navigateUp()
                        UserRepository.uploadUser(currentUser!!)
                    }

                    UserResults.NAMEMPTY -> {
                        etNameP.showError(tilNameP, R.string.name_empty)
                    }

                    UserResults.FUTUREBIRTH -> {
                        etDob.showError(tilDob, R.string.dob_future)
                    }
                    UserResults.NODATEBRITH -> {
                        etDob.showError(tilDob, R.string.register_message_select_dob)
                    }
                    UserResults.INVALIDPHONE -> {
                        etPhoneP.showError(tilPhoneP, R.string.phone_invalid)
                    }
                    UserResults.EMAILSHORT -> {
                        etEmailP.showError(tilEmailP, R.string.register_message_email_short)
                    }
                    UserResults.EMAILFORMAT -> {
                        etEmailP.showError(tilEmailP, R.string.register_message_email_incorrect)
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