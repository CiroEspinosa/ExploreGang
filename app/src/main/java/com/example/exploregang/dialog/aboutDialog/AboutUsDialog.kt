package com.example.exploregang.dialog.aboutDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.exploregang.databinding.DialogAboutBinding

class AboutUsDialog:DialogFragment() {
    private lateinit var binding:DialogAboutBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DialogAboutBinding.inflate(layoutInflater)
        binding.ivClose.setOnClickListener { dismiss() }
        return binding.root
    }
}