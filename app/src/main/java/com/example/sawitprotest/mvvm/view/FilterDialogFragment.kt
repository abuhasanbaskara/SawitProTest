package com.example.sawitprotest.mvvm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sawitprotest.R
import com.example.sawitprotest.databinding.FragmentFilterDialogBinding
import com.example.sawitprotest.util.TimePicker
import com.example.sawitprotest.util.setSingleOnClickListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

typealias OnApplyListener = (startDate: String?, endDate: String?, licenseNumber: String?, driverName: String?) -> Unit

class FilterDialogFragment(
    private var startDate: String? = null,
    private var endDate: String? = null,
    private var licenseNumber: String? = null,
    private var driverName: String? = null,
    private val listener: OnApplyListener? = null
) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentFilterDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            if (startDate != null && endDate != null){
                editDateRange.setText(getString(R.string.date_range_format, startDate, endDate))
            }
            editLicenseNumber.setText(licenseNumber)
            editDriverName.setText(driverName)

            editDateRange.setSingleOnClickListener {
                binding.root.context?.let { _ ->
                    TimePicker.showDateRangePicker(requireActivity()) { startDateResult, endDateResult ->
                        startDate = startDateResult
                        endDate = endDateResult
                        editDateRange.setText(getString(R.string.date_range_format, startDate, endDate))
                    }
                }
            }

            tvClearAll.setOnClickListener {
                editDateRange.setText("")
                editLicenseNumber.setText("")
                editDriverName.setText("")
            }

            buttonApply.setOnClickListener {
                if (editDateRange.text.isBlank()){
                    startDate = null
                    endDate = null
                }
                listener?.invoke(
                    startDate,
                    endDate,
                    editLicenseNumber.text.toString(),
                    editDriverName.text.toString()
                )
                dismiss()
            }
        }
    }
}