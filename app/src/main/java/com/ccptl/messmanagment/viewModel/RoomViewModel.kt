package com.ccptl.messmanagment.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ccptl.messmanagment.repository.RoomRepository
import com.ccptl.messmanagment.room.roomModel.MemberData
import com.ccptl.messmanagment.room.roomModel.MessData

class RoomViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = RoomRepository(app)
    private val allMemberData = repository.getAllMemberData()
    var getMemberByIdData: LiveData<List<MemberData>> = MutableLiveData()
    val getMessDataByMemberId: MutableLiveData<List<MessData>> = MutableLiveData()
//    val getActiveMess: MutableLiveData<List<MessHistoryData>> = MutableLiveData()


    fun insertMember(memberData: MemberData) {
        repository.insertMember(memberData)
    }

    fun insertMessData(messData: MessData) {
        repository.insertMessData(messData)
    }

    fun deleteAllMemberData() {
        repository.deleteAllMemberData()
    }

    fun deleteAllMessData() {
        repository.deleteAllMessData()
    }

    fun getAllMemberData(): LiveData<List<MemberData>> {
        return allMemberData
    }

    fun getMemberById(messMemberId: String) {
        getMemberByIdData = repository.getMemberById(messMemberId)
    }

    fun getMessDataById(messMemberId: String) {
        repository.getMessDataById(messMemberId).observeForever {
            getMessDataByMemberId.value = it
        }
    }

    fun updatePaymentStatus(messId: String, paymentStatus: String) {
        repository.updatePaymentStatus(messId, paymentStatus)
    }

    fun updateMessStatus(messId: String, messStatus: String) {
        repository.updateMessStatus(messId, messStatus)
    }
}