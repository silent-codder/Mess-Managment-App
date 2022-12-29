package com.ccptl.messmanagment.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DemoDao {
    @Insert
    fun insert(note: DemoData)

    @Update
    fun update(note: DemoData)

    @Delete
    fun delete(note: DemoData)

    @Query("delete from member_table")
    fun deleteAllNotes()

    @Query("select * from member_table order by mess_member_from_date desc")
    fun getAllNotes(): LiveData<List<DemoData>>
}