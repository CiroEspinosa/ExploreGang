package com.example.exploregang.dialog.filtersDialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.exploregang.databinding.DialogFiltersBinding
import com.example.exploregang.ui.activityList.ActivityListViewModel

class FiltersDialog(
    private var viewModel: ActivityListViewModel,
    private var list: String=""
) : DialogFragment() {
    private lateinit var binding: DialogFiltersBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFiltersBinding.inflate(inflater, container, false)
        binding.apply {
            FilterData.apply {
                etSearchForActivity.setText(searchActivity)
                etSearchForDate.setText(searchDate)
                etSearchForLocation.setText(searchLocation)
            }

        }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        binding.apply {
            FilterData.apply {

                searchActivity = etSearchForActivity.text.toString()
                searchDate = etSearchForDate.text.toString()
                searchLocation = etSearchForLocation.text.toString()
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            ivClose.setOnClickListener { dismiss() }
            btnCleanFilters.setOnClickListener {
                etSearchForActivity.setText("")
                etSearchForLocation.setText("")
                etSearchForDate.setText("")
              //  etSearchForDate.setOnClickListener { showDateTimePickerDialogWithExactTime(etSearchForDate,requireContext()) }
                if (list.isEmpty()) {
                    viewModel.getAllActivities()
                } else {
                    viewModel.getUserActivities(list)
                }

                dismiss()

            }
            btnSearch.setOnClickListener {
                var searchActivity: String = etSearchForActivity.text.toString()
                var searchLocation: String = etSearchForLocation.text.toString()
                var searchDate = etSearchForDate.text.toString()
                if (list.isEmpty()) {
                    viewModel.getAllActivities(searchActivity, searchLocation, searchDate)
                } else {
                    viewModel.getUserActivities(searchActivity,searchLocation,searchDate,list)
                }

                dismiss()
            }
        }
    }


}