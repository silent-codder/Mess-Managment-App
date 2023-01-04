package com.ccptl.messmanagment.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ccptl.messmanagment.room.roomModel.MemberData


@Dao
interface DatabaseDao {
//    @Insert
//    fun insert(note: MemberData)
//
//    @Update
//    fun update(note: MemberData)
//
//    @Delete
//    fun delete(note: MemberData)
//
//    @Query("delete from member_table")
//    fun deleteAllNotes()
//
//    @Query("select * from member_table order by mess_member_from_date desc")
//    fun getAllNotes(): LiveData<List<MemberData>>
//
//    @Query("select * from member_table where mess_member_id = :messMemberId")
//    fun getMemberById(messMemberId: String): LiveData<MemberData>
//
//    //update payment status
//    @Query("update member_table set mess_member_payment_mode = :paymentMode where mess_member_id = :messMemberId")
//    fun updatePaymentStatus(messMemberId: String, paymentMode: String)
//
//
//    //mess history
//
//    @Insert
//    fun insertHistory(messHistoryData: MessHistoryData)
//
//    @Query("select * from mess_history_table where mess_history_member_id = :messMemberId order by mess_member_from_date desc ")
//    fun getHistoryByMemberId(messMemberId: String): LiveData<List<MessHistoryData>>
//
//    //get active status history
//    @Query("select * from mess_history_table where mess_history_member_id = :messMemberId and mess_member_status =='active' order by mess_member_from_date desc ")
//    fun getActiveMess(messMemberId: String): LiveData<List<MessHistoryData>>
//
//    //update mess status
//    @Query("update mess_history_table set mess_member_status = :messStatus where mess_history_member_id = :messMemberId")
//    fun updateMessStatus(messMemberId: String, messStatus: String)

    /** new dao **/

    @Insert
    fun insertMember(memberData: MemberData)

    @Query("delete from member_table")
    fun deleteAll()
}