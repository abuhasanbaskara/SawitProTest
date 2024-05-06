package com.example.sawitprotest.mvvm.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.sawitprotest.mvvm.data.Ticket

@Dao
interface TicketDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTicket(ticket: Ticket)

    @Update
    fun updateTicket(ticket: Ticket)

//    @Query("SELECT * FROM ticket " +
//            "WHERE (:startDate IS NULL OR :endDate IS NULL OR date BETWEEN :startDate AND :endDate) " +
//            "AND (:licenseNumber IS NULL OR licenseNumber = :licenseNumber) " +
//            "AND (:driverName IS NULL OR driverName = :driverName) " +
//            "ORDER BY time DESC")
//    fun getFilteredTickets(startDate: String?, endDate: String?, driverName: String?, licenseNumber: String?): List<Ticket>

    @Query("SELECT * FROM ticket WHERE date BETWEEN :startDate AND :endDate ORDER BY time DESC")
    fun getTicketsByDateRange(startDate: String, endDate: String): List<Ticket>

    @Query("SELECT * FROM ticket WHERE licenseNumber = :licenseNumber ORDER BY time DESC")
    fun getTicketsByLicenseNumber(licenseNumber: String): List<Ticket>

    @Query("SELECT * FROM ticket WHERE driverName = :driverName ORDER BY time DESC")
    fun getTicketsByDriverName(driverName: String): List<Ticket>

    @Query("SELECT * FROM ticket WHERE date BETWEEN :startDate AND :endDate AND licenseNumber = :licenseNumber ORDER BY time DESC")
    fun getTicketsByDateRangeAndLicense(startDate: String, endDate: String, licenseNumber: String): List<Ticket>

    @Query("SELECT * FROM ticket WHERE date BETWEEN :startDate AND :endDate AND driverName = :driverName ORDER BY time DESC")
    fun getTicketsByDateRangeAndDriver(startDate: String, endDate: String, driverName: String): List<Ticket>

    @Query("SELECT * FROM ticket WHERE licenseNumber = :licenseNumber AND driverName = :driverName ORDER BY time DESC")
    fun getTicketsByLicenseAndDriver(licenseNumber: String, driverName: String): List<Ticket>

    @Query("SELECT * FROM ticket WHERE date BETWEEN :startDate AND :endDate AND licenseNumber = :licenseNumber AND driverName = :driverName ORDER BY time DESC")
    fun getTicketsByDateRangeAndLicenseAndDriver(startDate: String, endDate: String, licenseNumber: String, driverName: String): List<Ticket>

    @Query("SELECT * FROM ticket ORDER BY time DESC LIMIT 100")
    fun getAllTickets(): List<Ticket>
}