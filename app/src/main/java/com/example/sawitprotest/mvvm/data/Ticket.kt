package com.example.sawitprotest.mvvm.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "ticket")
@Parcelize
data class Ticket(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val date: String,
    val time: String,
    val licenseNumber: String,
    val driverName: String,
    val inboundWeight: String,
    val outBoundWeight: String,
    val netWeight: String,
    val lastUpdated: Long
) : Parcelable {
    // Add a no-argument constructor
    constructor() : this(null, "", "", "", "", "", "", "", 0)
}
