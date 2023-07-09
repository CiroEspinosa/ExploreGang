package com.example.exploregang.ui.home

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.activity.addCallback
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.exploregang.R
import com.example.exploregang.data.prefs.UserPrefsManager
import com.example.exploregang.data.repository.UserSession
import com.example.exploregang.databinding.FragmentHomeBinding
import com.example.exploregang.dialog.CustomDialog
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        requireActivity().findViewById<BottomNavigationView>(R.id.navigation_view)?.visibility =
            View.VISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        showDialogKeepSesion()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }
    }

    private fun showDialogKeepSesion() {
        if(UserSession.isLoggedIn&&!UserPrefsManager.getUserEmailPref(requireContext()).equals(UserSession.email)){
            var customDialog = CustomDialog()
            customDialog.title = R.string.stay_signed_in
            customDialog.message = R.string.stay_signed_in_message
            customDialog.defaultButtonText = R.string.no
            customDialog.verticalButtons = false
            customDialog.isDefaultButtonVisible = true
            customDialog.outOfDialogClose = true
            var btnAccept = Button(context)
            btnAccept.setText(R.string.yes)
            btnAccept.setOnClickListener {
                UserPrefsManager.setUserEmailPref(UserSession.email, requireContext())
                customDialog.dismiss()
            }
            customDialog.addButton(btnAccept)

            customDialog.show(parentFragmentManager, "customDialog")

        }
    }

    private fun initUi() {
        binding.apply {
            btnUser.setOnClickListener {
                NavHostFragment.findNavController(requireParentFragment())
                    .navigate(R.id.action_fragmentHome_to_settingsFragment)
            }
            btnActivities.setOnClickListener {
                NavHostFragment.findNavController(requireParentFragment())
                    .navigate(R.id.action_fragmentHome_to_fragmentActivities)
            }
            if (rvMyActivitiesList.isEmpty()) {
                llMyActivities.isVisible = false
                llNoActivities.isVisible = true
            }
            if (rvPost.isEmpty()) {
                llPosts.isVisible = false
            }
            if (flnextEvent.isEmpty()) {
                tvNextEvent.setText(R.string.neg_next_event)
                llNextEventInfo.isVisible = false
            }
        }
    }
}