package com.ccptl.messmanagment.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ccptl.messmanagment.repository.RoomRepository
import com.ccptl.messmanagment.room.roomModel.MemberData

class RoomViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = RoomRepository(app)
//    private val allNotes = repository.getAllMemberData()
//    val getMemberByIdData: MutableLiveData<MemberData> = MutableLiveData()
//    val getHistoryByMemberId: MutableLiveData<List<MessHistoryData>> = MutableLiveData()
//    val getActiveMess: MutableLiveData<List<MessHistoryData>> = MutableLiveData()


    fun insertMember(memberData: MemberData) {
        repository.insertMember(memberData)
    }

    fun deleteAll() {
        repository.deleteAll()
    }

//    fun insert(note: MemberData) {
//        repository.insert(note)
//    }
//
//    fun update(note: MemberData) {
//        repository.update(note)
//    }
//

//
//    fun deleteAllNotes() {
//        repository.deleteAllNotes()
//    }
//
//    fun getAllNotes(): LiveData<List<MemberData>> {
//        return allNotes
//    }
//
//    fun getMemberById(messMemberId: String) {
//        repository.getMemberById(messMemberId).observeForever {
//            getMemberByIdData.value = it
//        }
//    }
//
//    fun updatePaymentStatus(messMemberId: String, paymentMode: String) {
//        repository.updatePaymentStatus(messMemberId, paymentMode)
//    }
//
//    //mess history
//
//    fun insertHistory(messHistoryData: MessHistoryData) {
//        repository.insertHistory(messHistoryData)
//    }
//
//    fun getHistoryByMemberIdData(messMemberId: String) {
//        repository.getHistoryByMemberIdData(messMemberId).observeForever {
//            getHistoryByMemberId.value = it
//        }
//    }
//
//    fun getActiveMess(messMemberId: String){
//        repository.getActiveMessMember(messMemberId).observeForever {
//            getActiveMess.value = it
//        }
//    }
//
//    fun updateMessStatus(messMemberId: String, status: String){
//        repository.updateMessStatus(messMemberId, status)
//    }
}