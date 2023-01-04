package com.ccptl.messmanagment.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ccptl.messmanagment.room.DatabaseDao
import com.ccptl.messmanagment.room.roomModel.MemberData
import com.ccptl.messmanagment.room.roomModel.MessData

@Database(entities = [MemberData::class,MessData::class], version = 1, exportSchema = false)
abstract class MessDatabase : RoomDatabase() {

    abstract fun messDao(): DatabaseDao

    companion object {
        private var instance: MessDatabase? = null
        @Synchronized
        fun getInstance(ctx: Context): MessDatabase {
            if(instance == null)
                instance = Room.databaseBuilder(ctx.applicationContext, MessDatabase::class.java,
                    "mess_database")
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

        private fun populateDatabase(db: MessDatabase) {
//            val noteDao = db.noteDao()
//            val firebaseFirestore = FirebaseFirestore.getInstance()
//
//            subscribeOnBackground {
//                firebaseFirestore.collection(Constants.FIREBASE_MESS_MEMBER_DATA).get().addOnSuccessListener {
//                    for (document in it) {
//                        val data = document.toObject(MemberData::class.java)
//                        noteDao.insert(data)
//                    }
//                }
//            }
        }
    }



}