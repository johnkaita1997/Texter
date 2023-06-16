package com.tafatalkstudent.Shared;

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update

@Database(entities = [Gender::class, Login::class], version = 1)
abstract class RoomDb : RoomDatabase() {

    abstract fun getRoomDAO(): RoomDAO
    abstract fun loginDao(): LoginDao

    companion object {

        @Volatile
        private var instance: RoomDb? = null
        private val LOCK = Any()
//        private val LOCK = Any()

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

@Entity(tableName = "login")
data class Login(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "student_id") val studentId: String,
    @ColumnInfo(name = "login_timestamp") val loginTimestamp: Long,
    @ColumnInfo(name = "logout_timestamp") var logoutTimestamp: Long?
)

@Dao
interface LoginDao {
    @Insert
    suspend fun insert(login: Login)
    @Update
    suspend fun update(login: Login)

    @Query("SELECT * FROM login")
    suspend fun getAllLogins(): List<Login>

    @Query("SELECT * FROM login WHERE login_timestamp <= :targetTimestamp AND (logout_timestamp IS NULL OR logout_timestamp >= :targetTimestamp)")
    suspend fun findLoginForTimestamp(targetTimestamp: Long): List<Login>

}



