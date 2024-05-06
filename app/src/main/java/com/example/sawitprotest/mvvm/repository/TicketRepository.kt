package com.example.sawitprotest.mvvm.repository

import com.example.sawitprotest.mvvm.database.TaskDatabase
import com.example.sawitprotest.mvvm.data.Ticket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TicketRepository @Inject constructor (private val taskDatabase: TaskDatabase) {

    suspend fun insertTicket(ticket: Ticket) {
        withContext(Dispatchers.IO) {
            taskDatabase.ticketDao().insertTicket(ticket)
        }
    }

    suspend fun updateTicket(ticket: Ticket) {
        withContext(Dispatchers.IO) {
            taskDatabase.ticketDao().updateTicket(ticket)
        }
    }

    suspend fun getTicketsByDateRange(startDate: String, endDate: String): Flow<List<Ticket>> {
        return withContext(Dispatchers.IO) {
            flow {
                val filteredTickets = taskDatabase.ticketDao().getTicketsByDateRange(startDate, endDate)
                emit(filteredTickets)
            }
        }
    }

    suspend fun getTicketsByLicenseNumber(licenseNumber: String): Flow<List<Ticket>> {
        return withContext(Dispatchers.IO) {
            flow {
                val filteredTickets = taskDatabase.ticketDao().getTicketsByLicenseNumber(licenseNumber)
                emit(filteredTickets)
            }
        }
    }

    suspend fun getTicketsByDriverName(driverName: String): Flow<List<Ticket>> {
        return withContext(Dispatchers.IO) {
            flow {
                val filteredTickets = taskDatabase.ticketDao().getTicketsByDriverName(driverName)
                emit(filteredTickets)
            }
        }
    }

    suspend fun getTicketsByDateRangeAndLicense(startDate: String, endDate: String, licenseNumber: String): Flow<List<Ticket>> {
        return withContext(Dispatchers.IO) {
            flow {
                val filteredTickets = taskDatabase.ticketDao().getTicketsByDateRangeAndLicense(startDate, endDate, licenseNumber)
                emit(filteredTickets)
            }
        }
    }

    suspend fun getTicketsByDateRangeAndDriver(startDate: String, endDate: String, driverName: String): Flow<List<Ticket>> {
        return withContext(Dispatchers.IO) {
            flow {
                val filteredTickets = taskDatabase.ticketDao().getTicketsByDateRangeAndDriver(startDate, endDate, driverName)
                emit(filteredTickets)
            }
        }
    }

    suspend fun getTicketsByLicenseAndDriver(licenseNumber: String, driverName: String): Flow<List<Ticket>> {
        return withContext(Dispatchers.IO) {
            flow {
                val filteredTickets = taskDatabase.ticketDao().getTicketsByLicenseAndDriver(licenseNumber, driverName)
                emit(filteredTickets)
            }
        }
    }

    suspend fun getTicketsByDateRangeAndLicenseAndDriver(startDate: String, endDate: String, licenseNumber: String, driverName: String): Flow<List<Ticket>> {
        return withContext(Dispatchers.IO) {
            flow {
                val filteredTickets = taskDatabase.ticketDao().getTicketsByDateRangeAndLicenseAndDriver(startDate, endDate, licenseNumber, driverName)
                emit(filteredTickets)
            }
        }
    }

    suspend fun getAllTickets(): Flow<List<Ticket>> {
        return withContext(Dispatchers.IO) {
            flow {
                val filteredTickets = taskDatabase.ticketDao().getAllTickets()
                emit(filteredTickets)
            }
        }
    }
}