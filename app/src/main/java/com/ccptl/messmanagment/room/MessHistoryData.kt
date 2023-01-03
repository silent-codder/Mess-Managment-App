package com.ccptl.messmanagment.room

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "mess_history_table")
data class MessHistoryData(
    val mess_history_id: String,
    val mess_history_member_id: String,
    val mess_member_close_at : String,
    val mess_member_created_at :String,
    val mess_member_delete_at :String,
    val restaurant_id :String,
    val mess_member_updated_at :String,
    val mess_member_from_date :String,
    val mess_member_to_date :String,
    val mess_member_status :String,
    @PrimaryKey(autoGenerate = false) val id: Int? = null
) {
    constructor() : this("", "", "", "", "", "", "", "", "", "")
}
