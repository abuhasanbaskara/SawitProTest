package com.example.sawitprotest.mvvm.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sawitprotest.R
import com.example.sawitprotest.databinding.ActivityDetailTicketBinding
import com.example.sawitprotest.mvvm.data.Ticket

class DetailTicketActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDetailTicketBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        initData()
        bindView()
    }

    @Suppress("DEPRECATION")
    private fun initData() {
        with(binding){
            val ticket = intent.getParcelableExtra<Ticket>("ticket")
            if (ticket != null){
                showDate.text = ticket.date
                showTime.text = ticket.time
                showLicenseNumber.text = ticket.licenseNumber
                showDriverName.text = ticket.driverName
                showInbound.text = ticket.inboundWeight
                showOutbound.text = ticket.outBoundWeight
                showNetWeight.text = ticket.netWeight
            }
        }
    }

    private fun bindView() {
        with(binding){
            customActionBar.tvTitle.text = getString(R.string.detail_ticket)
            customActionBar.ivBack.setOnClickListener {
                finish()
            }
        }
    }
}