package com.example.exploregang.ui.otherUserLists

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exploregang.R
import com.example.exploregang.adapter.UserAdapter
import com.example.exploregang.data.model.Actividad
import com.example.exploregang.data.model.User
import com.example.exploregang.data.repository.ActivityRepository
import com.example.exploregang.data.repository.UserRepository.currentUser
import com.example.exploregang.data.repository.UserRepository.uploadUser
import com.example.exploregang.databinding.FragmentOtherUserListBinding
import com.example.exploregang.dialog.customDialog.CustomDialog
import com.example.exploregang.ui.activityList.ListResult
import com.example.exploregang.ui.userList.UserListViewModel
import com.example.exploregang.util.Constants
import com.example.exploregang.util.Constants.collectionActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class OtherUserListFragment : Fragment(), UserAdapter.OnClickUserListener {
    private lateinit var binding: FragmentOtherUserListBinding
    private lateinit var viewModel: UserListViewModel
    private lateinit var adapter: UserAdapter
    private lateinit var actividad: Actividad
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (arguments != null) {
            actividad = requireArguments().getParcelable(collectionActivity)!!
        }
        binding = FragmentOtherUserListBinding.inflate(layoutInflater)
        binding.btnBack.setOnClickListener { NavHostFragment.findNavController(this).navigateUp() }
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (arguments == null) {
                if (binding.etSearch.text.toString().isNullOrEmpty()) {
                    viewModel.getContacts()
                } else {
                    viewModel.filterContacts(binding.etSearch.text.toString())
                }
            } else {
                if (binding.etSearch.text.toString().isNullOrEmpty()) {
                    viewModel.getEnrolledUsers(actividad)
                } else {
                    viewModel.filterEnrolledUsers(binding.etSearch.text.toString(), actividad)
                }
            }


        }
        binding.etSearch.addTextChangedListener {
            if (arguments == null) {
                if (binding.etSearch.text.toString().isNullOrEmpty()) {
                    viewModel.getContacts()
                } else {
                    viewModel.filterContacts(binding.etSearch.text.toString())
                }
            } else {
                if (binding.etSearch.text.toString().isNullOrEmpty()) {
                    viewModel.getEnrolledUsers(actividad)
                } else {
                    viewModel.filterEnrolledUsers(binding.etSearch.text.toString(), actividad)
                }
            }
        }
        binding.ivFilters.setOnClickListener {
            if (arguments == null) {
                if (binding.etSearch.text.toString().isNullOrEmpty()) {
                    viewModel.getContacts()
                } else {
                    viewModel.filterContacts(binding.etSearch.text.toString())
                }
            } else {
                if (binding.etSearch.text.toString().isNullOrEmpty()) {
                    viewModel.getEnrolledUsers(actividad)
                } else {
                    viewModel.filterEnrolledUsers(binding.etSearch.text.toString(), actividad)
                }
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
                    binding.rvUsers.isVisible = false
                    adapter.clear()
                }
                ListResult.SUCCESS -> {
                    binding.rvUsers.isVisible = true
                    binding.loading.isVisible = false
                    binding.ivNoData.isVisible = false
                    binding.tvNoData.isVisible = false
                    adapter.update(viewModel.userList)
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                ListResult.NODATA -> {
                    binding.rvUsers.isVisible = false
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
        if (arguments == null) {
            viewModel.getContacts()

        } else {
            viewModel.getEnrolledUsers(actividad)
        }


    }

    private fun initAdapter() {
        adapter = UserAdapter(this, requireContext())
        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<BottomNavigationView>(R.id.navigation_view)?.visibility =
            View.GONE
    }

    override fun onClickUser(user: User?) {
        if (user!!.isPublic!!) {
            val bundle = Bundle()
            bundle.putParcelable(Constants.collectionUser, user)
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_otherUserListFragment_to_userDetailsFragment, bundle)
        } else {
            Toast.makeText(requireContext(), R.string.user_private, Toast.LENGTH_SHORT).show()
        }

    }

    private fun showDialogRemoveUserContact(user: User) {
        val dialog = CustomDialog()
        dialog.onBind = { binding ->
            binding.acceptBtnCd.isVisible = true
            binding.acceptBtnCd.setText(R.string.remove)
            binding.acceptBtnCd.setOnClickListener {
                dialog.dismiss()
                currentUser!!.contacts!!.remove(user.id)
                this.binding.loading.isVisible = true
                uploadUser(currentUser!!)
                viewModel.getContacts()
                this.binding.loading.isVisible = false

            }
        }
        dialog.title = R.string.remove_contact
        dialog.message = R.string.advert_remove_user
        dialog.show(parentFragmentManager, "")
    }

    private fun showDialogRemoveUserActivity(user: User) {
        val dialog = CustomDialog()
        dialog.onBind = { binding ->
            binding.acceptBtnCd.isVisible = true
            binding.acceptBtnCd.setText(R.string.disenroll)
            binding.acceptBtnCd.setOnClickListener {
                dialog.dismiss()
                actividad.participantsIds.remove(user.id)
                user.enrolledActivities!!.remove(actividad.id)
                uploadUser(user, {}, {})
                ActivityRepository.uploadActivity(actividad,
                    { viewModel.getEnrolledUsers(actividad) }, {})

            }
        }
        dialog.title = R.string.disenroll_user
        dialog.message = R.string.advert_disenroll_user_activity
        dialog.show(parentFragmentManager, "")
    }

    override fun onLongClickUser(user: User?) {
        if (arguments == null) {
            showDialogRemoveUserContact(user!!)
        } else {
            if (currentUser!!.ownActivities!!.contains(actividad.id)) {
                showDialogRemoveUserActivity(user!!)
            }
        }
    }

}