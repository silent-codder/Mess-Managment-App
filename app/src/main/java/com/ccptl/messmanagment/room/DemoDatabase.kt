package com.ccptl.messmanagment.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ccptl.messmanagment.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.huawei.todolist.utils.subscribeOnBackground

@Database(entities = [DemoData::class], version = 1)
abstract class DemoDatabase : RoomDatabase() {

    abstract fun noteDao(): DemoDao

    companion object {
        private var instance: DemoDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): DemoDatabase {
            if(instance == null)
                instance = Room.databaseBuilder(ctx.applicationContext, DemoDatabase::class.java,
                    "demo_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()

            return instance!!

        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                populateDatabase(instance!!)
            }
        }

        private fun populateDatabase(db: DemoDatabase) {
            val noteDao = db.noteDao()
            val firebaseFirestore = FirebaseFirestore.getInstance()

            subscribeOnBackground {
                firebaseFirestore.collection(Constants.FIREBASE_MESS_MEMBER_DATA).get().addOnSuccessListener {
                    for (document in it) {
                        val data = document.toObject(DemoData::class.java)
                        noteDao.insert(data)
                    }
                }
            }
        }
    }



}