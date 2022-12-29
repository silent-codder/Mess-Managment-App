package com.ccptl.messmanagment.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ccptl.messmanagment.repository.DemoRepository
import com.ccptl.messmanagment.room.DemoData

class DemoViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = DemoRepository(app)
    private val allNotes = repository.getAllNotes()

    fun insert(note: DemoData) {
        repository.insert(note)
    }

    fun update(note: DemoData) {
        repository.update(note)
    }

    fun delete(note: DemoData) {
        repository.delete(note)
    }

    fun deleteAllNotes() {
        repository.deleteAllNotes()
    }

    fun getAllNotes(): LiveData<List<DemoData>> {
        return allNotes
    }


}