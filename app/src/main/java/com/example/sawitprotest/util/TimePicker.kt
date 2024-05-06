package com.example.sawitprotest.util

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimePicker {

    fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
        val builder = MaterialDatePicker.Builder.datePicker()
        val picker = builder.build()

        picker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = Date(selection)
            val formattedDate = formatDate(selectedDate)
            onDateSelected(formattedDate)
        }

        picker.show((context as FragmentActivity).supportFragmentManager, picker.toString())
    }

    fun showDateRangePicker(context: Context, onDateRangeSelected: (String, String) -> Unit) {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val picker = builder.build()

        picker.addOnPositiveButtonClickListener { selection ->
            val startDate = Date(selection.first)
            val endDate = Date(selection.second)
            val formattedStartDate = formatDate(startDate)
            val formattedEndDate = formatDate(endDate)
            onDateRangeSelected(formattedStartDate, formattedEndDate)
        }

        picker.show((context as FragmentActivity).supportFragmentManager, picker.toString())
    }

    private fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(date)
    }

    fun showTimePicker(context: Context, onTimeSelected: (String) -> Unit) {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setInputMode(INPUT_MODE_CLOCK)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute

            val selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
            onTimeSelected(selectedTime)
        }

        timePicker.show((context as FragmentActivity).supportFragmentManager, "timePicker")
    }

}