package com.ccptl.messmanagment.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DatabaseDao {
    @Insert
    fun insert(note: MemberData)

    @Update
    fun update(note: MemberData)

    @Delete
    fun delete(note: MemberData)

    @Query("delete from member_table")
    fun deleteAllNotes()

    @Query("select * from member_table order by mess_member_from_date desc")
    fun getAllNotes(): LiveData<List<MemberData>>

    @Query("select * from member_table where mess_member_id = :messMemberId")
    fun getMemberById(messMemberId: String): LiveData<MemberData>
}