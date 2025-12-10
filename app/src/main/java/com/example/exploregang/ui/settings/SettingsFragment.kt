package com.example.exploregang.ui.settings

import com.example.exploregang.dialog.customDialog.CustomDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.navigation.fragment.NavHostFragment
import com.example.exploregang.R
import com.example.exploregang.data.prefs.UserPrefsManager
import com.example.exploregang.data.repository.UserRepository
import com.example.exploregang.data.repository.UserRepository.currentUser
import com.example.exploregang.databinding.FragmentSettingsBinding
import com.example.exploregang.dialog.aboutDialog.AboutUsDialog
import com.example.exploregang.util.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView


class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.apply {
            if (currentUser == null || currentUser!!.imageId.isNullOrEmpty()) {
                ivUserPhoto.setImageResource(R.drawable.usericon)
                ivUserPhoto.setPadding(Utils.dpToPx(20, resources))
            } else {
                Utils.getImage(currentUser!!.imageId,ivUserPhoto)
            }
            aboutLayout.setOnClickListener {
                val dialog=AboutUsDialog()
                dialog.show(parentFragmentManager,"")
            }
            infoLayout.setOnClickListener {
                val dialogHelp = CustomDialog {}
                dialogHelp.onBind = { binding ->
                    binding.apply {
                        tvTitle.setText(R.string.info)
                        tvMessage.setText(R.string.info_message)
                        cancelBtnCd.setText(R.string.ok)
                        acceptBtnCd.isGone=true
                    }
                }
                dialogHelp.show(parentFragmentManager, "customDialog settings") }
            helpLayout.setOnClickListener {
                val dialogHelp = CustomDialog {}
                dialogHelp.onBind = { binding ->
                    binding.apply {
                        tvTitle.setText(R.string.help)
                        tvMessage.setText(R.string.help_message)
                        cancelBtnCd.setText(R.string.ok)
                        acceptBtnCd.isGone=true
                    }
                }
                dialogHelp.show(parentFragmentManager, "customDialog settings")
            }
            if (currentUser != null) {
                tvNameLabel.text =currentUser!!.name
                cllogout.isVisible = true
                cllogout.setOnClickListener { logout() }
                cldeleteaccount.isVisible = true
                lllogin.setOnClickListener {

                    NavHostFragment.findNavController(requireParentFragment())
                        .navigate(R.id.profileFragment)
                }
                cldeleteaccount.setOnClickListener {
                    val customDialog = CustomDialog { }
                    customDialog.onBind = { binding ->
                        binding.tvTitle.setText(R.string.delete_account)
                        binding.tvMessage.setText(R.string.delete_account_message)
                        binding.acceptBtnCd.isVisible = true
                        binding.cancelBtnCd.setText(R.string.cancel)
                        binding.acceptBtnCd.setText(R.string.delete)
                        binding.acceptBtnCd.setOnClickListener {
                            customDialog.dismiss()
                            UserRepository.deleteAccount(onSuccess = {
                                UserPrefsManager.quitUserSession(requireContext())
                                Toast.makeText(
                                    context,
                                    R.string.delete_account_message_success,
                                    Toast.LENGTH_SHORT
                                ).show()
                                NavHostFragment.findNavController(requireParentFragment())
                                    .navigate(R.id.fragmentLogin)

                            }, onFailure = {
                                if (it.isNullOrEmpty()) {
                                    Toast.makeText(
                                        context,
                                        R.string.delete_account_message_fail,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }else{
                                    Toast.makeText(
                                        context,
                                        R.string.login_for_delete,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                if (UserRepository.currentUser == null) {
                                    UserPrefsManager.quitUserSession(requireContext())
                                    Toast.makeText(
                                        context,
                                        R.string.delete_account_message_success,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    NavHostFragment.findNavController(requireParentFragment())
                                        .navigate(R.id.fragmentLogin)
                                }
                            })

                        }
                    }
                    customDialog.show(parentFragmentManager, "customDialog settings")

                }
            } else {
                cldeleteaccount.isVisible = false
                cllogout.isVisible = false
                lllogin.setOnClickListener {
                    UserPrefsManager.quitUserSession(requireContext())
                    logout()
                    NavHostFragment.findNavController(requireParentFragment())
                        .navigate(R.id.fragmentLogin)
                }
            }

        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<BottomNavigationView>(R.id.navigation_view)?.visibility =
            View.GONE
    }

    private fun logout() {
        UserPrefsManager.quitUserSession(requireContext())
         UserRepository.logout()
        NavHostFragment.findNavController(this).navigate(R.id.fragmentLogin)
    }
}