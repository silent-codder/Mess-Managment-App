package com.ccptl.messmanagment.room.roomModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mess_data")
data class MessData(
    val memberId: String,
    val messId: String,
    val fromDate: String,
    val fromDateTimeStamp: String,
    val toDate: String,
    val toDateTimeStamp: String,
    val payment: String,
    val messType: String,
    val paymentType: String,
    val createdBy: String,
    val createdOn: Long,
    val updatedBy: String,
    val updatedOn: Long,
    val status: String,
    val isDeleted: Boolean,
    val deletedBy: String,
    @PrimaryKey(autoGenerate = false) val id: Int? = null
) {
    constructor() : this("","", "", "", "", "", "", "", "", "", 0, "", 0, "", false, "")
}

