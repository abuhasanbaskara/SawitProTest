package com.example.sawitprotest.mvvm.view

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.sawitprotest.util.DecimalInputFilter
import com.example.sawitprotest.util.TimePicker
import com.example.sawitprotest.util.setSingleOnClickListener
import com.example.sawitprotest.R
import com.example.sawitprotest.databinding.ActivityAddTicketBinding
import com.example.sawitprotest.mvvm.data.Ticket
import com.example.sawitprotest.mvvm.viewmodel.TicketViewModel
import com.example.sawitprotest.util.AlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddTicketActivity : AppCompatActivity() {

    private val binding by lazy { ActivityAddTicketBinding.inflate(layoutInflater) }
    private val viewModel: TicketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initData()
        bindView()
    }

    @Suppress("DEPRECATION")
    private fun initData() {
        with(binding){
            viewModel.ticket = intent.getParcelableExtra("ticket")
            if (viewModel.ticket != null){
                viewModel.isEdit = true
                editDate.setText(viewModel.ticket?.date)
                editTime.setText(viewModel.ticket?.time)
                editLicenseNumber.setText(viewModel.ticket?.licenseNumber)
                editDriverName.setText(viewModel.ticket?.driverName)
                editInbound.setText(viewModel.ticket?.inboundWeight)
                editOutbound.setText(viewModel.ticket?.outBoundWeight)
                editNetWeight.setText(viewModel.ticket?.netWeight)
            }
        }
    }

    private fun bindView() {
        with(binding){
            disableButtonSubmit()
            val correctDrawable = ContextCompat.getDrawable(binding.root.context, R.drawable.green_check_circle_outline_32)
            val errorDrawable = ContextCompat.getDrawable(binding.root.context, R.drawable.red_error_outline_32)
            customActionBar.tvTitle.text = getString(R.string.ticket_form)
            customActionBar.ivBack.setSingleOnClickListener {
                finish()
            }
            editDate.setSingleOnClickListener {
                binding.root.context?.let { it1 ->
                    TimePicker.showDatePicker(it1) { selectedDate ->
                        editDate.setText(selectedDate)
                    }
                }
            }
            editTime.setSingleOnClickListener {
                binding.root.context?.let { it1 ->
                    TimePicker.showTimePicker(it1) { selectedTime ->
                        editTime.setText(selectedTime)
                    }
                }
            }
            editDate.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    checkAllForm()
                }
            })
            editTime.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    checkAllForm()
                }
            })
            editLicenseNumber.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
            editLicenseNumber.filters += InputFilter.LengthFilter(11)
            editLicenseNumber.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    if (editLicenseNumber.text.isEmpty()){
                        editLicenseNumber.hideEndDrawable()
                        editLicenseNumber.setBackgroundResource(R.drawable.curved_outlined_background)
                    }else if (isValidLicensePlate(editLicenseNumber.text.toString())){
                        editLicenseNumber.showEndDrawable(correctDrawable)
                        editLicenseNumber.setBackgroundResource(R.drawable.curved_outlined_background)
                    } else {
                        editLicenseNumber.showEndDrawable(errorDrawable)
                        editLicenseNumber.setBackgroundResource(R.drawable.curved_error_background)
                    }

                    checkAllForm()
                }
            })
            editDriverName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    checkAllForm()
                }
            })
            editInbound.filters = arrayOf(DecimalInputFilter(maxValue = 10))
            editOutbound.filters = arrayOf(DecimalInputFilter(maxValue = 10))
            editNetWeight.filters = arrayOf(DecimalInputFilter(maxValue = 10))
            editInbound.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    val inbound = editInbound.text.toString().toDoubleOrNull() ?: 0.0
                    val outbound = editOutbound.text.toString().toDoubleOrNull() ?: 0.0
                    if (inbound >= outbound && editOutbound.text.isNotEmpty()) {
                        val result = inbound - outbound
                        editNetWeight.setText(result.toString())
                        editOutbound.hideEndDrawable()
                        editOutbound.setBackgroundResource(R.drawable.curved_outlined_background)
                    } else {
                        editOutbound.showEndDrawable(errorDrawable)
                        editOutbound.setBackgroundResource(R.drawable.curved_error_background)
                    }
                    if (editInbound.text.isNotEmpty()){
                        editInbound.hideEndDrawable()
                        editInbound.setBackgroundResource(R.drawable.curved_outlined_background)
                    } else {
                        editInbound.showEndDrawable(errorDrawable)
                        editInbound.setBackgroundResource(R.drawable.curved_error_background)
                    }

                    checkAllForm()
                }
            })
            editOutbound.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    val inbound = editInbound.text.toString().toDoubleOrNull() ?: 0.0
                    val outbound = editOutbound.text.toString().toDoubleOrNull() ?: 0.0
                    if (inbound >= outbound && editOutbound.text.isNotEmpty()) {
                        val result = inbound - outbound
                        editNetWeight.setText(result.toString())
                        editOutbound.hideEndDrawable()
                        editOutbound.setBackgroundResource(R.drawable.curved_outlined_background)
                    } else {
                        editOutbound.showEndDrawable(errorDrawable)
                        editOutbound.setBackgroundResource(R.drawable.curved_error_background)
                    }

                    checkAllForm()
                }
            })
            editNetWeight.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    checkAllForm()
                }
            })
            buttonSubmit.setSingleOnClickListener {
                AlertDialog.showAlertDialog(
                    binding.root.context,
                    title = if (viewModel.isEdit){
                        getString(R.string.edit_ticket)}
                    else {
                        getString(R.string.input_ticket)},
                    message = if (viewModel.isEdit){
                        getString(R.string.edit_confirmation)}
                    else {
                        getString(R.string.input_data_confirmation)},
                    negativeButtonText = getString(R.string.cancel),
                    positiveButtonText = if (viewModel.isEdit){
                        getString(R.string.edit)}
                    else {
                        getString(R.string.input)},
                    positiveButtonAction = {
                        insertOrUpdateData()
                    },
                    negativeButtonAction = {

                    }
                )
            }
        }
    }

    private fun insertOrUpdateData() {
        with(binding){
            val date = editDate.text.toString()
            val time = editTime.text.toString()
            val licenseNumber = editLicenseNumber.text.toString()
            val driverName = editDriverName.text.toString()
            val inboundWeight = editInbound.text.toString()
            val outBoundWeight = editOutbound.text.toString()
            val netWeight = editNetWeight.text.toString()

            val ticket = if (viewModel.isEdit) {
                Ticket(
                    id = viewModel.ticket?.id,
                    date = date,
                    time = time,
                    licenseNumber = licenseNumber,
                    driverName = driverName,
                    inboundWeight = inboundWeight,
                    outBoundWeight = outBoundWeight,
                    netWeight = netWeight,
                    lastUpdated = System.currentTimeMillis()
                )
            } else {
                Ticket(
                    id = null,
                    date = date,
                    time = time,
                    licenseNumber = licenseNumber,
                    driverName = driverName,
                    inboundWeight = inboundWeight,
                    outBoundWeight = outBoundWeight,
                    netWeight = netWeight,
                    lastUpdated = System.currentTimeMillis()
                )
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val operation = if (viewModel.isEdit) {
                        viewModel.updateTicket(ticket)
                        getString(R.string.edit_success)
                    } else {
                        viewModel.insertTicket(ticket)
                        getString(R.string.input_success)
                    }

                    val data = Intent()
                    data.putExtra("toast_message", operation)
                    setResult(Activity.RESULT_OK, data)
                    finish()
                } catch (e: Exception) {
                    print(e)
                    finish()
                }
            }
        }
    }

    private fun checkAllForm(){
        with(binding){
            if (editDate.text.isNotEmpty() &&
                editTime.text.isNotEmpty() &&
                isValidLicensePlate(editLicenseNumber.text.toString()) &&
                editDriverName.text.isNotEmpty() &&
                editInbound.text.isNotEmpty() &&
                editOutbound.text.isNotEmpty() &&
                editNetWeight.text.isNotEmpty()){
                enableButtonSubmit()
            } else {
                disableButtonSubmit()
            }
        }
    }

    private fun EditText.showEndDrawable(endDrawable: Drawable?) {
        endDrawable?.let {
            setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, it, null)
        }
    }

    private fun EditText.hideEndDrawable() {
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
    }

    private fun disableButtonSubmit(){
        with(binding){
            buttonSubmit.isEnabled = false
            buttonSubmit.isClickable = false
            buttonSubmit.setBackgroundResource(R.drawable.curved_disabled_background)
            buttonSubmit.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
        }
    }

    private fun enableButtonSubmit(){
        with(binding){
            buttonSubmit.isEnabled = true
            buttonSubmit.isClickable = true
            buttonSubmit.setBackgroundResource(R.drawable.curved_background)
            buttonSubmit.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
        }
    }

    fun isValidLicensePlate(plateNumber: String): Boolean {
        val pattern = "^[A-Z]{1,2}\\s\\d{1,4}\\s[A-Z]{1,3}$"
        val regex = Regex(pattern)
        return regex.matches(plateNumber)
    }
}