package com.example.exploregang.ui.activityList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exploregang.R
import com.example.exploregang.adapter.ActivityAdapter
import com.example.exploregang.data.model.Actividad
import com.example.exploregang.data.prefs.UserPrefsManager
import com.example.exploregang.data.repository.ActivityRepository
import com.example.exploregang.data.repository.UserRepository
import com.example.exploregang.data.repository.UserRepository.logout
import com.example.exploregang.databinding.FragmentActivityListBinding
import com.example.exploregang.dialog.customDialog.CustomDialog
import com.example.exploregang.dialog.filtersDialog.FilterData
import com.example.exploregang.dialog.filtersDialog.FiltersDialog
import com.example.exploregang.util.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView


class ActivityListFragment : Fragment(), ActivityAdapter.OnClickActivityListener {
    private lateinit var viewModel: ActivityListViewModel
    private lateinit var binding: FragmentActivityListBinding
    private lateinit var adapter: ActivityAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FilterData.apply {
            searchActivity = ""
            searchLocation = ""
            searchDate = ""
        }
        binding = FragmentActivityListBinding.inflate(layoutInflater)
        binding.btnBack.setOnClickListener { NavHostFragment.findNavController(this).navigateUp() }
        binding.btnCreate.setOnClickListener {
            if (UserRepository.currentUser==null) {
                var customDialog = CustomDialog()
                customDialog.onBind = { binding ->
                    binding.tvTitle.setText(R.string.create_activity)
                    binding.tvMessage.setText(R.string.user_login_necesary)
                    binding.acceptBtnCd.setText(R.string.accept)
                    binding.cancelBtnCd.setOnClickListener {
                        customDialog.dismiss()
                    }
                    binding.acceptBtnCd.setOnClickListener {
                        UserPrefsManager.quitUserSession(requireContext())
                        logout()
                         NavHostFragment.findNavController(this)
                            .navigate(R.id.action_fragmentActivityList_to_fragmentLogin)
                        customDialog.dismiss()
                    }
                }
                customDialog.show(parentFragmentManager, "activityListFragment")
            } else {
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_fragmentActivityList_to_addActivityFragment)
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            FilterData.apply {

                    viewModel.getAllActivities(searchActivity, searchLocation, searchDate)

            }
        }
        binding.ivFilters.setOnClickListener {
            val dialogF = FiltersDialog(viewModel)
            dialogF.show(parentFragmentManager, "ActivityListFragment")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initViewModel()


    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[ActivityListViewModel::class.java]
        viewModel.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                ListResult.LOADING -> {
                    binding.loading.isVisible = true
                    binding.ivNoData.isVisible = false
                    binding.tvNoData.isVisible = false
                    adapter.clear()
                }
                ListResult.SUCCESS -> {
                    binding.loading.isVisible = false
                    binding.ivNoData.isVisible = false
                    binding.tvNoData.isVisible = false
                    adapter.update(viewModel.activityList)
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                ListResult.NODATA -> {
                    binding.loading.isVisible = false
                    binding.ivNoData.isVisible = true
                    binding.tvNoData.isVisible = true
                    adapter.clear()
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                ListResult.FAILURE -> {
                    binding.loading.isVisible = false
                    binding.ivNoData.isVisible = true
                    binding.tvNoData.isVisible = true
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                else -> {}
            }
        }

       // viewModel.getAllActivities()
        FilterData.apply {
        viewModel.getAllActivities(searchActivity, searchLocation, searchDate)
        }

    }

    private fun initAdapter() {
        adapter = ActivityAdapter(this, requireContext())
        binding.rvActivities.layoutManager = LinearLayoutManager(requireContext())
        binding.rvActivities.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.navigation_view)
        bottomNavigationView.selectedItemId = R.id.fragmentActivityList
        requireActivity().findViewById<BottomNavigationView>(R.id.navigation_view)?.visibility =
            View.VISIBLE
    }

    override fun onClickActivity(activity: Actividad?) {
        val bundle = Bundle()
        bundle.putParcelable(Constants.collectionActivity, activity)
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_fragmentActivityList_to_activityDetailFragment, bundle)
    }

    override fun onLongClickActivity(actividad: Actividad?) {
        if(actividad!!.creator!!.id== UserRepository.currentUser!!.id){
            showDialogRemoveActivity(actividad)
        }
    }
    private fun showDialogRemoveActivity(actividad: Actividad?) {
        val dialog = CustomDialog()
        dialog.onBind = { binding ->
            binding.acceptBtnCd.isVisible = true
            binding.acceptBtnCd.setText(R.string.delete)
            binding.acceptBtnCd.setOnClickListener {
                dialog.dismiss()
                ActivityRepository.deleteActivity(actividad!!,
                    { Toast.makeText(requireContext(),R.string.delete_activity_correct, Toast.LENGTH_SHORT).show()
                        viewModel.getAllActivities(FilterData.searchActivity, FilterData.searchLocation, FilterData.searchDate)
                    }
                    ,{ Toast.makeText(requireContext(),R.string.something_failed, Toast.LENGTH_SHORT).show()})
            }
        }
        dialog.title = R.string.delete_activity
        dialog.message = R.string.remove_activity_advert
        dialog.show(parentFragmentManager, "")
    }

}