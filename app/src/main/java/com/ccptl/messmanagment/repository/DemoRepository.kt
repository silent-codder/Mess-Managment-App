package com.ccptl.messmanagment.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.ccptl.messmanagment.room.DemoDao
import com.ccptl.messmanagment.room.DemoData
import com.ccptl.messmanagment.room.DemoDatabase
import com.huawei.todolist.utils.subscribeOnBackground

class DemoRepository(application: Application) {

    private var demoDao: DemoDao
    private var allNotes: LiveData<List<DemoData>>

    private val database = DemoDatabase.getInstance(application)

    init {
        demoDao = database.noteDao()
        allNotes = demoDao.getAllNotes()
    }

    fun insert(note: DemoData) {
//        Single.just(noteDao.insert(note))
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe()
        subscribeOnBackground {
            demoDao.insert(note)
        }
    }

    fun update(note: DemoData) {
        subscribeOnBackground {
            demoDao.update(note)
        }
    }

    fun delete(note: DemoData) {
        subscribeOnBackground {
            demoDao.delete(note)
        }
    }

    fun deleteAllNotes() {
        subscribeOnBackground {
            demoDao.deleteAllNotes()
        }
    }

    fun getAllNotes(): LiveData<List<DemoData>> {
        return allNotes
    }



}