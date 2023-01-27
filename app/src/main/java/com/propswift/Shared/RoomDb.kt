package com.propswift.Shared;

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Gender::class], version = 1)
abstract class RoomDb : RoomDatabase() {

    abstract fun getRoomDAO(): RoomDAO

    companion object {

        @Volatile
        private var instance: RoomDb? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }
        private fun buildDatabase(context: Context) = Room.databaseBuilder(context.applicationContext, RoomDb::class.java, "watchingdatabase").build()

    }

}


@Dao
interface RoomDAO {

}


