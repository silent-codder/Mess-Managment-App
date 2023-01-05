package com.ccptl.messmanagment.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ccptl.messmanagment.room.roomModel.MemberData
import com.ccptl.messmanagment.room.roomModel.MessData


@Dao
interface DatabaseDao {
    @Insert
    fun insertMember(memberData: MemberData)

    @Insert
    fun insertMessData(messData: MessData)

    @Query("delete from member_table")
    fun deleteAllMemberData()

    @Query("delete from mess_data")
    fun deleteAllMessData()

    @Query("select * from member_table order by createdOn desc")
    fun getAllMemberData(): LiveData<List<MemberData>>

    @Query("select * from mess_data where memberId = :messMemberId order by createdOn desc")
    fun getMessDataByMemberId(messMemberId: String): LiveData<List<MessData>>

    @Query("select * from member_table where memberId = :messMemberId")
    fun getMemberById(messMemberId: String): LiveData<List<MemberData>>

    @Query("update mess_data set paymentType = :paymentStatus where messId = :messId")
    fun updatePaymentStatus(messId: String, paymentStatus: String)

    @Query("update mess_data set status = :messStatus where messId = :messId")
    fun updateMessStatus(messId: String, messStatus: String)

}