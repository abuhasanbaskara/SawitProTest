package com.example.sawitprotest.mvvm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sawitprotest.R
import com.example.sawitprotest.databinding.TicketCardBinding
import com.example.sawitprotest.mvvm.data.Ticket

class TicketListAdapter(private val context: Context) :
    RecyclerView.Adapter<TicketListAdapter.TicketViewHolder>(){

    private var tickets = ArrayList<Ticket>()
    private lateinit var onItemCallback: OnItemCallback
    private var filteredTickets = ArrayList<Ticket>()

    fun updateAdapter(ticket: ArrayList<Ticket>){
        tickets = ticket
        filteredTickets.clear()
        filteredTickets.addAll(ticket)
        notifyDataSetChanged()
    }

    fun setOnItemCallback(onItemCallback: OnItemCallback){
        this.onItemCallback = onItemCallback
    }

    fun filter(query: String) {
        filteredTickets.clear()
        if (query.isEmpty()) {
            filteredTickets.addAll(tickets)
        } else {
            for (ticket in tickets) {
                if (ticketMatchesQuery(ticket, query)) {
                    filteredTickets.add(ticket)
                }
            }
        }
        notifyDataSetChanged()
    }

    private fun ticketMatchesQuery(ticket: Ticket, query: String): Boolean {
        return ticket.date.contains(query, ignoreCase = true) ||
                ticket.licenseNumber.contains(query, ignoreCase = true) ||
                ticket.driverName.contains(query, ignoreCase = true)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TicketListAdapter.TicketViewHolder {
        val binding =
            TicketCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TicketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TicketListAdapter.TicketViewHolder, position: Int) {
        with(holder.binding){
            val itemTicket = tickets[position]
            tvDate.text = context.getString(R.string.date_format, itemTicket.date)
            tvLicenseNumber.text = context.getString(R.string.license_number_format, itemTicket.licenseNumber)
            tvDriverName.text = context.getString(R.string.driver_name_format, itemTicket.driverName)

            buttonDetail.setOnClickListener {
                onItemCallback.onItemSeeDetail(itemTicket)
            }
            buttonEdit.setOnClickListener {
                onItemCallback.onItemEdit(itemTicket)
            }
            buttonAddNew.setOnClickListener {
                onItemCallback.onItemAddNew()
            }
        }
    }

    override fun getItemCount(): Int = filteredTickets.size

    inner class TicketViewHolder(val binding: TicketCardBinding) :
            RecyclerView.ViewHolder(binding.root)

    interface OnItemCallback {
        fun onItemSeeDetail(ticket: Ticket)
        fun onItemEdit(ticket: Ticket)
        fun onItemAddNew()
    }
}