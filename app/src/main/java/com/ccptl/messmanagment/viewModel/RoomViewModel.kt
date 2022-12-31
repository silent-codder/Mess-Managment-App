package com.ccptl.messmanagment.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ccptl.messmanagment.repository.RoomRepository
import com.ccptl.messmanagment.room.MemberData

class RoomViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = RoomRepository(app)
    private val allNotes = repository.getAllMemberData()
    val getMemberByIdData: MutableLiveData<MemberData> = MutableLiveData()

    fun insert(note: MemberData) {
        repository.insert(note)
    }

    fun update(note: MemberData) {
        repository.update(note)
    }

    fun delete(note: MemberData) {
        repository.delete(note)
    }

    fun deleteAllNotes() {
        repository.deleteAllNotes()
    }

    fun getAllNotes(): LiveData<List<MemberData>> {
        return allNotes
    }

    fun getMemberById(messMemberId: String) {
        repository.getMemberById(messMemberId).observeForever {
            getMemberByIdData.value = it
        }
    }
}