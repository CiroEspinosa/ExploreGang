package com.example.exploregang.ui.otherProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import com.example.exploregang.R
import com.example.exploregang.data.model.User
import com.example.exploregang.data.repository.UserRepository
import com.example.exploregang.data.repository.UserRepository.currentUser
import com.example.exploregang.data.repository.UserRepository.uploadUser
import com.example.exploregang.databinding.FragmentUserDetailsBinding
import com.example.exploregang.util.Constants.collectionUser
import com.example.exploregang.util.Utils
import com.example.exploregang.util.Utils.dateToString
import com.google.android.material.bottomnavigation.BottomNavigationView


class UserDetailsFragment : Fragment() {
    private lateinit var user: User
    private lateinit var binding: FragmentUserDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        user = requireArguments().getParcelable(collectionUser)!!
        binding = FragmentUserDetailsBinding.inflate(layoutInflater)
        binding.apply {
            btnBackProfile.setOnClickListener {
                NavHostFragment.findNavController(this@UserDetailsFragment).navigateUp()
            }
            if (UserRepository.currentUser == null) {
                btnAddtoContact.isVisible = false
            } else {
                if (currentUser!!.id == user.id) {
                    btnAddtoContact.isVisible = false
                }
                /*if (currentUser!!.contacts!!.contains(user.id)) {
                    btnAddtoContact.setText(R.string.remove_contact)
                    btnAddtoContact.setOnClickListener {

                        removeContact()
                    }
                } else {
                    btnAddtoContact.setOnClickListener { addContact() }
                }*/
            }



            etNameP.setText(user.name)

            etEmailP.setText(user.email)
            if (!user.imageId.isNullOrEmpty()) {
                Utils.getImage(user.imageId,binding.ivUserPhoto)

            } else {
                ivUserPhoto.setImageResource(R.drawable.usericon)
            }
        }

        return binding.root
    }

    private fun removeContact() {
        //currentUser!!.contacts!!.remove(user.id)
        uploadUser(currentUser!!, { }, {})
        NavHostFragment.findNavController(this).navigateUp()
        binding.btnAddtoContact.isVisible = false
    }

    private fun addContact() {
       /* binding.btnAddtoContact.isVisible = false
        currentUser!!.contacts!!.add(user.id!!)
        uploadUser(
            currentUser!!,
            {},
            {
                Toast.makeText(requireContext(), R.string.something_failed, Toast.LENGTH_SHORT)
                    .show()
            })
        NavHostFragment.findNavController(this).navigateUp()*/

    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<BottomNavigationView>(R.id.navigation_view)?.visibility =
            View.GONE
    }
}