package com.example.exploregang.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback

import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exploregang.R
import com.example.exploregang.adapter.ActivityAdapter
import com.example.exploregang.data.model.Actividad
import com.example.exploregang.data.prefs.UserPrefsManager
import com.example.exploregang.data.repository.UserRepository
import com.example.exploregang.data.repository.UserRepository.currentUser
import com.example.exploregang.data.repository.UserRepository.logout
import com.example.exploregang.databinding.FragmentHomeBinding
import com.example.exploregang.dialog.customDialog.CustomDialog
import com.example.exploregang.ui.activityList.ListResult
import com.example.exploregang.ui.activityList.ActivityListViewModel
import com.example.exploregang.util.Constants
import com.example.exploregang.util.Constants.collectionActivity
import com.example.exploregang.util.Utils

import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Fragmento que muestra la pantalla de inicio (Home) de la aplicación.
 * Muestra la proxima actividad del usuario si existe y proporciona opciones de navegación y configuración.
 * Permite a los usuarios autenticados acceder a su perfil y ver sus actividades propias e inscritas.
 * Si el usuario no está autenticado, se muestra una interfaz distinta.
 */
class HomeFragment : Fragment(), ActivityAdapter.OnClickActivityListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: ActivityListViewModel
    private lateinit var adapter: ActivityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        hideUi()
        if (UserPrefsManager.isUserLogged(requireContext()) && currentUser == null) {

            UserRepository.getUserData(
                onSuccess = {

                    initAdapter()
                    initViewModel()
                    showUi()
                    initUi()
                },
                onFailure = {

                    initAdapter()
                    initViewModel()
                    showUi()
                    initUi()
                }
            )
        }else{

            initAdapter()
            initViewModel()
            showUi()
            initUi()
        }
        return binding.root
    }



    override fun onResume() {
        super.onResume()
        setupBottomNavigationView()
        setBackPressedCallback()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[ActivityListViewModel::class.java]
        viewModel.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                ListResult.LOADING -> {
                    binding.loadingP.isVisible = true
                    binding.llNoActivities.isGone = true
                    binding.llNextActivities.isGone = true
                    adapter.clear()
                }
                ListResult.SUCCESS -> {
                    binding.loadingP.isVisible = false
                    binding.llNoActivities.isGone = true
                    binding.llNextActivities.isVisible = true
                    adapter.update(viewModel.activityList)
                }
                ListResult.NODATA -> {
                    binding.loadingP.isVisible = false
                    binding.llNoActivities.isVisible = true
                    adapter.clear()

                }
                ListResult.FAILURE -> {

                    binding.loadingP.isVisible = false
                    binding.llNoActivities.isVisible = true
                    adapter.clear()
                }
                ListResult.ACTIVITYDELETED -> {
                    Toast.makeText(
                        requireContext(),
                        R.string.activities_deleted,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {}
            }
        }
        if (currentUser != null) {
            viewModel.getNextActivities(requireContext())
        }
    }

    private fun initAdapter() {
        adapter = ActivityAdapter(this, requireContext())
        binding.rvNextActivity.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNextActivity.adapter = adapter

    }

    private fun initUi() {
        binding.apply {
            if (currentUser == null) {
                llNextActivities.isGone = true
                btnSeeEnrolledActivities.isGone = true
                btnSeeOwnActivities.isGone = true
            }

            ivUserPhoto.setOnClickListener {
                if (currentUser != null) {
                    NavHostFragment.findNavController(requireParentFragment())
                        .navigate(R.id.action_fragmentHome_to_profileFragment)
                } else {
                    showDialogLogin()
                }
            }
            tvhello.setOnClickListener {
                if (currentUser != null) {
                    NavHostFragment.findNavController(requireParentFragment())
                        .navigate(R.id.action_fragmentHome_to_profileFragment)
                } else {
                    showDialogLogin()
                }
            }
            btnSettings.setOnClickListener {
                NavHostFragment.findNavController(requireParentFragment())
                    .navigate(R.id.action_fragmentHome_to_settingsFragment)
            }
            btnActivities.setOnClickListener {
                val bottomNavigationView =
                    requireActivity().findViewById<BottomNavigationView>(R.id.navigation_view)
                bottomNavigationView.selectedItemId = R.id.fragmentActivityList
            }
            btnSeeOwnActivities.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(collectionActivity, Constants.owned)
                NavHostFragment.findNavController(this@HomeFragment)
                    .navigate(R.id.action_fragmentHome_to_otherActivityListFragment, bundle)
            }
            btnSeeEnrolledActivities.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(collectionActivity, Constants.enrolled)
                NavHostFragment.findNavController(this@HomeFragment)
                    .navigate(R.id.action_fragmentHome_to_otherActivityListFragment, bundle)
            }
        }
    }


    private fun setDefaultImage() {
        binding.ivUserPhoto.setImageResource(R.drawable.usericon)
        binding.ivUserPhoto.setPadding(Utils.dpToPx(20, resources))
    }


    private fun showDialogLogin() {
        val dialog = CustomDialog()
        dialog.onBind = { binding ->
            binding.acceptBtnCd.isVisible = true
            binding.acceptBtnCd.setText(R.string.accept)
            binding.acceptBtnCd.setOnClickListener {
                UserPrefsManager.quitUserSession(requireContext())
                logout()
                NavHostFragment.findNavController(this@HomeFragment)
                    .navigate(R.id.action_fragmentHome_to_fragmentLogin)
                dialog.dismiss()

            }
        }
        dialog.title = R.string.log_in
        dialog.message = R.string.user_login_necesary
        dialog.show(parentFragmentManager, "")
    }

    override fun onClickActivity(activity: Actividad?) {
        val bundle = Bundle()
        bundle.putParcelable(Constants.collectionActivity, activity)
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_fragmentHome_to_activityDetailFragment, bundle)
    }

    override fun onLongClickActivity(activity: Actividad?) {
        val bundle = Bundle()
    }

    private fun hideUi() {
        binding.apply {
            ivUserPhoto.isGone = true
            view.isGone = true
            tvhello.isGone = true
            btnSettings.isGone = true
            loadingP.isVisible = true
            llNextActivities.isGone = true
            btnSeeEnrolledActivities.isGone = true
            btnSeeOwnActivities.isGone = true
            llNoActivities.isGone = true
            binding.tvInfoActivity.isGone = true
        }
    }
    private fun showUi() {
        binding.apply {
            ivUserPhoto.isVisible = true
            view.isVisible = true
            tvhello.isVisible = true
            btnSettings.isVisible = true
            loadingP.isVisible = false
            if (currentUser != null) {
                if (currentUser!!.imageId.isNullOrEmpty()) {
                    setDefaultImage()
                } else {
                   Utils.getImage(currentUser!!.imageId,ivUserPhoto)
                }

                val name = currentUser!!.name
                val greetings = getString(R.string.greetings_message, name)
                binding.tvhello.text = greetings
                viewModel.getNextActivities(requireContext())

                if (currentUser!!.enrolledActivities.isNotEmpty()) {
                    btnSeeEnrolledActivities.isVisible = true
                }
                if (currentUser!!.ownActivities.isNotEmpty()) {
                    btnSeeOwnActivities.isVisible = true
                }
               // llNextActivities.isVisible = true
            }else{
                setDefaultImage()
                llNoActivities.isVisible=true
               tvInfoActivity.isVisible = true
            }

        }
    }



    private fun setBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }
    }

    private fun setupBottomNavigationView() {
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.navigation_view)
        bottomNavigationView.selectedItemId = R.id.fragmentHome
        requireActivity().findViewById<BottomNavigationView>(R.id.navigation_view)?.visibility =
            View.VISIBLE
    }
}
