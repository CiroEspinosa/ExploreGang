package com.example.exploregang.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup

import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.navigation.fragment.NavHostFragment
import com.example.exploregang.R
import com.example.exploregang.data.prefs.UserPrefsManager
import com.example.exploregang.data.repository.UserRepository
import com.example.exploregang.data.repository.UserRepository.currentUser
import com.example.exploregang.databinding.FragmentProfileBinding
import com.example.exploregang.dialog.customDialog.CustomDialog
import com.example.exploregang.util.Utils
import com.example.exploregang.util.Utils.dateToString
import com.google.android.material.bottomnavigation.BottomNavigationView


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<BottomNavigationView>(R.id.navigation_view)?.visibility =
            View.GONE
        init()
    }

    private fun showDialogLogin() {
        val dialog = CustomDialog()
        dialog.onBind = { binding ->
            binding.acceptBtnCd.isVisible = true
            binding.acceptBtnCd.setText(R.string.drawer_text_logout)
            binding.acceptBtnCd.setOnClickListener {
                dialog.dismiss()
                UserPrefsManager.quitUserSession(requireContext())
                UserRepository.logout()
                NavHostFragment.findNavController(this@ProfileFragment)
                    .navigate(R.id.action_profileFragment_to_fragmentLogin)
            }
        }
        dialog.title = R.string.drawer_text_logout
        dialog.message = R.string.logout_advert
        dialog.show(parentFragmentManager, "")
    }

    private fun init() {

        binding.apply {
            loadingP.isVisible = true

            if (currentUser == null || currentUser!!.imageId.isNullOrEmpty()) {

                ivUserPhoto.setImageResource(R.drawable.usericon)
                ivUserPhoto.setPadding(Utils.dpToPx(20, resources))
            } else {
                Utils.getImage(currentUser!!.imageId,binding.ivUserPhoto)
            }
            btnSeeContacts.setOnClickListener {
                NavHostFragment.findNavController(requireParentFragment())
                    .navigate(R.id.action_profileFragment_to_otherUserListFragment)
            }
            btnBackProfile.setOnClickListener {
                NavHostFragment.findNavController(requireParentFragment()).navigateUp()
            }
            btnLogaut.setOnClickListener { showDialogLogin() }
            btnEdit.setOnClickListener {
                NavHostFragment.findNavController(this@ProfileFragment)
                    .navigate(R.id.action_profileFragment_to_editProfileFragment)
            }



            etEmailP.setText(currentUser!!.email)
            etNameP.setText(currentUser!!.name)


            loadingP.isVisible = false
        }
    }


}