package com.ccptl.messmanagment.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.ccptl.messmanagment.room.DatabaseDao
import com.ccptl.messmanagment.room.MemberData
import com.ccptl.messmanagment.room.MessDatabase
import com.huawei.todolist.utils.subscribeOnBackground

class RoomRepository(application: Application) {

    private var demoDao: DatabaseDao
    private var allNotes: LiveData<List<MemberData>>

    private val database = MessDatabase.getInstance(application)

    init {
        demoDao = database.noteDao()
        allNotes = demoDao.getAllNotes()
    }

    fun insert(note: MemberData) {
        subscribeOnBackground {
            demoDao.insert(note)
        }
    }

    fun update(note: MemberData) {
        subscribeOnBackground {
            demoDao.update(note)
        }
    }

    fun delete(note: MemberData) {
        subscribeOnBackground {
            demoDao.delete(note)
        }
    }

    fun deleteAllNotes() {
        subscribeOnBackground {
            demoDao.deleteAllNotes()
        }
    }

    fun getAllMemberData(): LiveData<List<MemberData>> {
        return allNotes
    }

    fun getMemberById(messMemberId: String): LiveData<MemberData> {
        return demoDao.getMemberById(messMemberId)
    }

}