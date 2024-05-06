package com.example.sawitprotest.mvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sawitprotest.mvvm.data.Ticket
import com.example.sawitprotest.mvvm.repository.TicketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TicketViewModel @Inject constructor(
    private val repository: TicketRepository
): ViewModel() {

    private val _ticketsLiveData: MutableLiveData<List<Ticket>> = MutableLiveData()
    val ticketsLiveData: LiveData<List<Ticket>> = _ticketsLiveData

    private val _ticketsLiveDataForSync: MutableLiveData<List<Ticket>> = MutableLiveData()
    val ticketsLiveDataForSync: LiveData<List<Ticket>> = _ticketsLiveDataForSync

    var isEdit = false
    var ticket : Ticket? = null
    var startDate : String? = null
    var endDate : String? = null
    var driverName : String? = null
    var licenseNumber : String? = null

    suspend fun insertTicket(ticket: Ticket) = repository.insertTicket(ticket = ticket)

    suspend fun updateTicket(ticket: Ticket) = repository.updateTicket(ticket = ticket)

    suspend fun getTicketsByDateRange(startDate: String, endDate: String) {
        repository.getTicketsByDateRange(startDate, endDate).collect { tickets ->
            _ticketsLiveData.postValue(tickets)
        }
    }

    suspend fun getTicketsByLicenseNumber(licenseNumber: String) {
        repository.getTicketsByLicenseNumber(licenseNumber).collect { tickets ->
            _ticketsLiveData.postValue(tickets)
        }
    }

    suspend fun getTicketsByDriverName(driverName: String) {
        repository.getTicketsByDriverName(driverName).collect { tickets ->
            _ticketsLiveData.postValue(tickets)
        }
    }

    suspend fun getTicketsByDateRangeAndLicense(startDate: String, endDate: String, licenseNumber: String) {
        repository.getTicketsByDateRangeAndLicense(startDate, endDate, licenseNumber).collect { tickets ->
            _ticketsLiveData.postValue(tickets)
        }
    }

    suspend fun getTicketsByDateRangeAndDriver(startDate: String, endDate: String, driverName: String) {
        repository.getTicketsByDateRangeAndDriver(startDate, endDate, driverName).collect { tickets ->
            _ticketsLiveData.postValue(tickets)
        }
    }

    suspend fun getTicketsByLicenseAndDriver(licenseNumber: String, driverName: String) {
        repository.getTicketsByLicenseAndDriver(licenseNumber, driverName).collect { tickets ->
            _ticketsLiveData.postValue(tickets)
        }
    }

    suspend fun getTicketsByDateRangeAndLicenseAndDriver(startDate: String, endDate: String, licenseNumber: String, driverName: String) {
        repository.getTicketsByDateRangeAndLicenseAndDriver(startDate, endDate, licenseNumber, driverName).collect { tickets ->
            _ticketsLiveData.postValue(tickets)
        }
    }

    suspend fun getAllTickets() {
        repository.getAllTickets().collect { tickets ->
            _ticketsLiveData.postValue(tickets)
        }
    }

    suspend fun getAllTicketsAndSync() {
        repository.getAllTickets().collect { tickets ->
            _ticketsLiveDataForSync.postValue(tickets)
        }
    }

}