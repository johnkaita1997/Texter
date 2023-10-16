package com.tafatalkstudent.Shared;

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SmsDetail::class], version = 1)
abstract class RoomDb : RoomDatabase() {

    abstract fun getSmsDao(): SmsDao

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
interface SmsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSmsDetail(smsDetail: SmsDetail): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(objects: List<SmsDetail>)



    @Query("SELECT * FROM smsdetail")
    suspend fun getAllSmsDetails(): List<SmsDetail>

    @Query("SELECT * FROM smsdetail INNER JOIN (SELECT phoneNumber, MAX(timestamp) AS maxTimestamp FROM smsdetail GROUP BY phoneNumber) AS latestSms ON smsdetail.phoneNumber = latestSms.phoneNumber AND smsdetail.timestamp = latestSms.maxTimestamp")
    suspend fun getLatestSmsList(): List<SmsDetail>



    @Query("SELECT * FROM smsdetail WHERE phoneNumber = :phoneNumber ORDER BY timestamp DESC")
    suspend fun getMessagesByPhoneNumber(phoneNumber: String): List<SmsDetail>

    @Query("SELECT * FROM smsdetail WHERE timestamp = :timestamp")
    suspend fun getSmsDetailByTimestamp(timestamp: Long): SmsDetail

    @Query("SELECT COUNT(*) FROM smsdetail WHERE timestamp = :timestamp")
    suspend fun doesMessageExist(timestamp: Long): Int



    // Delete a message by timestamp
    @Query("DELETE FROM smsdetail WHERE timestamp = :timestamp")
    suspend fun deleteMessageByTimestamp(timestamp: Long)

    @Query("UPDATE smsdetail SET status = :newStatus WHERE timestamp = :targetTimestamp")
    suspend fun updateStatusByTimestamp(targetTimestamp: Long, newStatus: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSmsDetailIgnore(smsDetail: SmsDetail): Long


    @Query("SELECT COUNT(*) FROM smsdetail")
    suspend fun getTotalSmsDetailCount(): Int

    @Query("SELECT COUNT(*) FROM smsdetail WHERE state = 'draft'")
    suspend fun getDraftSmsCount(): Int


}



