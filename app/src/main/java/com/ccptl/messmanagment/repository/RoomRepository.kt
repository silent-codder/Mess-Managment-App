package com.ccptl.messmanagment.repository

import android.app.Application
import com.ccptl.messmanagment.room.DatabaseDao
import com.ccptl.messmanagment.room.database.MessDatabase
import com.ccptl.messmanagment.room.roomModel.MemberData
import com.huawei.todolist.utils.subscribeOnBackground

class RoomRepository(application: Application) {

    private var messDao: DatabaseDao
//    private var allNotes: LiveData<List<MemberData>>

    private val database = MessDatabase.getInstance(application)

    init {
        messDao = database.messDao()
//        allNotes = demoDao.getAllNotes()
    }

    fun insertMember(memberData: MemberData) {
        subscribeOnBackground {
            messDao.insertMember(memberData)
        }
    }

    fun deleteAll() {
        subscribeOnBackground {
            messDao.deleteAll()
        }
    }

//    fun insert(note: MemberData) {
//        subscribeOnBackground {
//            demoDao.insert(note)
//        }
//    }
//
//    fun update(note: MemberData) {
//        subscribeOnBackground {
//            demoDao.update(note)
//        }
//    }
//
//    fun delete(note: MemberData) {
//        subscribeOnBackground {
//            demoDao.delete(note)
//        }
//    }
//
//    fun deleteAllNotes() {
//        subscribeOnBackground {
//            demoDao.deleteAllNotes()
//        }
//    }
//
//    fun getAllMemberData(): LiveData<List<MemberData>> {
//        return allNotes
//    }
//
//    fun getMemberById(messMemberId: String): LiveData<MemberData> {
//        return demoDao.getMemberById(messMemberId)
//    }
//
//    fun updatePaymentStatus(messMemberId: String, paymentMode: String) {
//        subscribeOnBackground {
//            demoDao.updatePaymentStatus(messMemberId, paymentMode)
//        }
//    }
//
//    //mess history
//
//    fun insertHistory(messHistoryData: MessHistoryData) {
//        subscribeOnBackground {
//            demoDao.insertHistory(messHistoryData)
//        }
//    }
//
//    fun getHistoryByMemberIdData(messMemberId: String): LiveData<List<MessHistoryData>> {
//        return demoDao.getHistoryByMemberId(messMemberId)
//    }
//
//    fun getActiveMessMember(messMemberId: String,): LiveData<List<MessHistoryData>> {
//        return demoDao.getActiveMess(messMemberId)
//    }
//
//    fun updateMessStatus(messMemberId: String, status: String) {
//        subscribeOnBackground {
//            demoDao.updateMessStatus(messMemberId, status)
//        }
//    }
}