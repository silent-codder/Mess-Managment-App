package com.ccptl.messmanagment.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.ccptl.messmanagment.room.DatabaseDao
import com.ccptl.messmanagment.room.database.MessDatabase
import com.ccptl.messmanagment.room.roomModel.MemberData
import com.ccptl.messmanagment.room.roomModel.MessData
import com.huawei.todolist.utils.subscribeOnBackground

class RoomRepository(application: Application) {

    private var messDao: DatabaseDao
    private var allMemberData: LiveData<List<MemberData>>
    private val database = MessDatabase.getInstance(application)

    init {
        messDao = database.messDao()
        allMemberData = messDao.getAllMemberData()
    }

    fun insertMember(memberData: MemberData) {
        subscribeOnBackground {
            messDao.insertMember(memberData)
        }
    }

    fun insertMessData(messData: MessData) {
        subscribeOnBackground {
            messDao.insertMessData(messData)
        }
    }

    fun deleteAllMemberData() {
        subscribeOnBackground {
            messDao.deleteAllMemberData()
        }
    }

    fun deleteAllMessData() {
        subscribeOnBackground {
            messDao.deleteAllMessData()
        }
    }

    fun getAllMemberData(): LiveData<List<MemberData>> {
        return allMemberData
    }

    fun getMemberById(messMemberId: String): LiveData<List<MemberData>> {
        return messDao.getMemberById(messMemberId)
    }

    fun getMessDataById(messMemberId: String): LiveData<List<MessData>> {
        return messDao.getMessDataByMemberId(messMemberId)
    }

    fun updatePaymentStatus(messId: String, paymentStatus: String) {
        subscribeOnBackground {
            messDao.updatePaymentStatus(messId, paymentStatus)
        }
    }

    fun updateMessStatus(messId: String, messStatus: String) {
        subscribeOnBackground {
            messDao.updateMessStatus(messId, messStatus)
        }
    }
}