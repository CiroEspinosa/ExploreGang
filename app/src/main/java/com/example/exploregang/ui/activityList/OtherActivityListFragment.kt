package com.example.exploregang.ui.activityList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exploregang.R
import com.example.exploregang.adapter.ActivityAdapter
import com.example.exploregang.data.model.Actividad
import com.example.exploregang.data.repository.ActivityRepository
import com.example.exploregang.data.repository.UserRepository.currentUser
import com.example.exploregang.data.repository.UserRepository.uploadUser
import com.example.exploregang.databinding.FragmentOtherActivityListBinding
import com.example.exploregang.dialog.customDialog.CustomDialog
import com.example.exploregang.dialog.filtersDialog.FilterData
import com.example.exploregang.dialog.filtersDialog.FiltersDialog
import com.example.exploregang.util.Constants.collectionActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class OtherActivityListFragment : Fragment(), ActivityAdapter.OnClickActivityListener {
    private lateinit var binding: FragmentOtherActivityListBinding
    private lateinit var viewModel: ActivityListViewModel
    private lateinit var adapter: ActivityAdapter
    private lateinit var list: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FilterData.apply {
            searchActivity = ""
            searchLocation = ""
            searchDate = ""
        }
        list = requireArguments().getString(collectionActivity)!!
        binding = FragmentOtherActivityListBinding.inflate(layoutInflater)
        binding.btnBack.setOnClickListener { NavHostFragment.findNavController(this).navigateUp() }
        binding.swipeRefreshLayout.setOnRefreshListener {

            FilterData.apply {
                viewModel.getUserActivities(searchActivity, searchLocation, searchDate, list)
            }
        }
        binding.ivFilters.setOnClickListener {
            val dialogF = FiltersDialog(viewModel, list)
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
            viewModel.getUserActivities(searchActivity, searchLocation, searchDate, list)
        }

    }

    private fun initAdapter() {
        adapter = ActivityAdapter(this, requireContext())
        binding.rvActivities.layoutManager = LinearLayoutManager(requireContext())
        binding.rvActivities.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<BottomNavigationView>(R.id.navigation_view)?.visibility =
            View.GONE
    }

    override fun onClickActivity(activity: Actividad?) {
        val bundle = Bundle()
        bundle.putParcelable(collectionActivity, activity)
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_otherActivityListFragment_to_activityDetailFragment, bundle)
    }

    override fun onLongClickActivity(actividad: Actividad?) {
        if (actividad!!.creator!!.id == currentUser!!.id) {
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
                currentUser!!.ownActivities.remove(actividad!!.id)
                uploadUser(currentUser!!)
                ActivityRepository.deleteActivity(actividad!!,
                    {
                        FilterData.apply {
                            viewModel.getUserActivities(
                                searchActivity,
                                searchLocation,
                                searchDate,
                                list
                            )
                        }
                    }
                )
            }
        }
        dialog.title = R.string.delete_activity
        dialog.message = R.string.remove_activity_advert
        dialog.show(parentFragmentManager, "")
    }
}