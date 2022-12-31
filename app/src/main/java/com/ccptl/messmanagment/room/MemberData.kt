package com.ccptl.messmanagment.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "member_table")
data class MemberData(
    val mess_member_close_at : String,
    val mess_member_created_at :String,
    val mess_member_delete_at :String,
    val mess_member_id :String,
    val mess_member_updated_at :String,
    val user_id :String,
    val mess_member_name :String,
    val mess_member_mobile :String,
    val mess_type :String,
    val mess_member_from_date :String,
    val mess_member_to_date :String,
    val mess_member_payment :String,
    val mess_member_payment_mode :String,
    @PrimaryKey(autoGenerate = false) val id: Int? = null
){
    constructor(): this("", "","","","","","","","","","","","")
}
