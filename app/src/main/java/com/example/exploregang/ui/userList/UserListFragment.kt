package com.example.exploregang.ui.userList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exploregang.R
import com.example.exploregang.adapter.UserAdapter
import com.example.exploregang.data.model.User
import com.example.exploregang.databinding.FragmentUserListBinding
import com.example.exploregang.ui.activityList.ListResult
import com.example.exploregang.util.Constants.collectionUser
import com.google.android.material.bottomnavigation.BottomNavigationView


class UserListFragment : Fragment(), UserAdapter.OnClickUserListener {
    private lateinit var binding: FragmentUserListBinding
    private lateinit var viewModel: UserListViewModel
    private lateinit var adapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserListBinding.inflate(layoutInflater)
        binding.btnBack.setOnClickListener { NavHostFragment.findNavController(this).navigateUp() }
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (binding.etSearch.text.toString().isNullOrEmpty()) {
                viewModel.getAllUsers()
            } else {
                viewModel.filterUsers(binding.etSearch.text.toString())
            }

        }
        binding.etSearch.addTextChangedListener {
            if (binding.etSearch.text.toString().isNullOrEmpty()) {
                viewModel.getAllUsers()
            } else {
                viewModel.filterUsers(binding.etSearch.text.toString())
            }
        }
        binding.ivFilters.setOnClickListener {
            if (binding.etSearch.text.toString().isNullOrEmpty()) {
                viewModel.getAllUsers()
            } else {
                viewModel.filterUsers(binding.etSearch.text.toString())
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[UserListViewModel::class.java]
        viewModel.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                ListResult.LOADING -> {
                    binding.loading.isVisible = true
                    binding.ivNoData.isVisible = false
                    binding.tvNoData.isVisible = false
                    binding.rvUsers.isVisible=false
                    adapter.clear()
                }
                ListResult.SUCCESS -> {
                    binding.rvUsers.isVisible=true
                    binding.loading.isVisible = false
                    binding.ivNoData.isVisible = false
                    binding.tvNoData.isVisible = false
                    adapter.update(viewModel.userList)
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                ListResult.NODATA -> {
                    binding.rvUsers.isVisible=false
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
        viewModel.getAllUsers()

    }

    private fun initAdapter() {
        adapter = UserAdapter(this, requireContext())
        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.navigation_view)
        bottomNavigationView.selectedItemId = R.id.fragmentUserList
        requireActivity().findViewById<BottomNavigationView>(R.id.navigation_view)?.visibility =
            View.VISIBLE
    }

    override fun onClickUser(user: User?) {

        val bundle = Bundle()
        bundle.putParcelable(collectionUser, user)
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_fragmentUserList_to_userDetailsFragment, bundle)
    }

    override fun onLongClickUser(user: User?) {
        val bundle = Bundle()
    }

}