package com.example.exploregang.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.exploregang.R
import com.example.exploregang.databinding.FragmentSettingsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSettingsBinding.inflate(inflater,container,false)
        binding.apply {
            logout.setOnClickListener {
                logOut()
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<BottomNavigationView>(R.id.navigation_view)?.visibility = View.GONE
    }

    private fun logOut() {

    }
}