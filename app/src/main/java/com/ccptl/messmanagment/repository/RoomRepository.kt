package com.ccptl.messmanagment.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.ccptl.messmanagment.room.DatabaseDao
import com.ccptl.messmanagment.room.MemberData
import com.ccptl.messmanagment.room.MessDatabase
import com.ccptl.messmanagment.room.MessHistoryData
import com.huawei.todolist.utils.subscribeOnBackground

class RoomRepository(application: Application) {

    private var demoDao: DatabaseDao
    private var allNotes: LiveData<List<MemberData>>
    private var allMessHistoryData: LiveData<List<MessHistoryData>>

    private val database = MessDatabase.getInstance(application)

    init {
        demoDao = database.noteDao()
        allNotes = demoDao.getAllNotes()
        allMessHistoryData = demoDao.getAllMessHistoryData()
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

    fun updatePaymentStatus(messMemberId: String, paymentMode: String) {
        subscribeOnBackground {
            demoDao.updatePaymentStatus(messMemberId, paymentMode)
        }
    }

    //mess history

    fun insertHistory(messHistoryData: MessHistoryData) {
        subscribeOnBackground {
            demoDao.insertHistory(messHistoryData)
        }
    }

    fun getAllMessHistoryData(): LiveData<List<MessHistoryData>> {
        return allMessHistoryData
    }
}