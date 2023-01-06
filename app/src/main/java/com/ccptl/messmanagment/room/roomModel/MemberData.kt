package com.ccptl.messmanagment.room.roomModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "member_table")
data class MemberData(
    val memberId: String,
    val name: String,
    val mobile: String,
    val email: String,
    val createdBy: String,
    val createdOn: Long,
    val updatedBy: String,
    val updatedOn: Long,
    val isDeleted: Int,
    val deletedBy: String,
    @PrimaryKey(autoGenerate = false) val id: Int? = null
) {
    constructor() : this("", "", "", "", "", 0, "", 0, -1, "")
}
