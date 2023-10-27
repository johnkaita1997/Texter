package com.tafatalkstudent.Shared;

import android.content.Context
import androidx.constraintlayout.widget.Group
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverters
import androidx.room.Update

@TypeConverters(Converters::class)
@Database(entities = [SmsDetail::class, SimCard::class, Groups::class, GroupSmsDetail::class], version = 1)
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

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBatch(objects: List<SmsDetail>)


    /*@Query("SELECT * FROM smsdetail INNER JOIN (SELECT phoneNumber, MAX(timestamp) AS maxTimestamp FROM smsdetail GROUP BY phoneNumber ORDER BY maxTimestamp DESC LIMIT :pageSize OFFSET (:pageNumber - 1) * :pageSize) AS latestSms ON smsdetail.phoneNumber = latestSms.phoneNumber AND smsdetail.timestamp = latestSms.maxTimestamp")
    suspend fun getLatestPagedSmsList(pageNumber: Int, pageSize: Int): MutableList<SmsDetail>*/

    @Query("SELECT * FROM smsdetail INNER JOIN (SELECT phoneNumber, MAX(timestamp) AS maxTimestamp FROM smsdetail GROUP BY phoneNumber) AS latestSms ON smsdetail.phoneNumber = latestSms.phoneNumber AND smsdetail.timestamp = latestSms.maxTimestamp ORDER BY latestSms.maxTimestamp DESC LIMIT :pageSize OFFSET (:pageNumber - 1) * :pageSize")
    suspend fun getLatestPagedSmsList(pageNumber: Int, pageSize: Int): List<SmsDetail>

    @Query("SELECT * FROM smsdetail INNER JOIN (SELECT phoneNumber, MAX(timestamp) AS maxTimestamp FROM smsdetail GROUP BY phoneNumber) AS latestSms ON smsdetail.phoneNumber = latestSms.phoneNumber AND smsdetail.timestamp = latestSms.maxTimestamp")
    suspend fun getLatestSmsList(): List<SmsDetail>


    @Query("SELECT * FROM smsdetail WHERE phoneNumber = :phoneNumber ORDER BY timestamp DESC")
    suspend fun getMessagesByPhoneNumber(phoneNumber: String): MutableList<SmsDetail>

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

    @Query("SELECT COUNT(*) FROM smsdetail WHERE state = 'Draft'")
    suspend fun getDraftSmsCount(): Int


    @Query("DELETE FROM smsdetail WHERE status LIKE 'Delivered -%' OR status LIKE 'Sent -%'")
    suspend fun deleteMessagesWithPattern()

    @Query("SELECT * FROM smsdetail WHERE state = 'Draft'")
    suspend fun getDraftMessage(): SmsDetail?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActiveSimCard(simCard: SimCard): Long

    @Query("SELECT * FROM simcard")
    suspend fun getActiveSimCard(): SimCard

    @Query("UPDATE smsdetail SET isRead = 1 WHERE phoneNumber = :phoneNumber")
    suspend fun markMessagesAsRead(phoneNumber: String)



    @Update
    suspend fun updateGroup(group: Groups)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(groupId: Groups) : Long

    // Delete a group by ID
    @Query("DELETE FROM groups WHERE id = :groupId")
    suspend fun deleteGroupById(groupId: Long)

    @Query("SELECT * FROM groups")
    fun getAllGroups(): MutableList<Groups>

    // Fetch a group by ID
    @Query("SELECT * FROM groups WHERE id = :groupId")
    suspend fun getGroupById(groupId: Long): Groups


    @Query("SELECT * FROM smsdetail ORDER BY timestamp DESC")
    suspend fun getAllSmsDetails(): MutableList<SmsDetail>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupSmsDetail(smsDetail: GroupSmsDetail): Long

    @Query("SELECT * FROM groupsmsdetail WHERE timestamp = :timestamp")
    suspend fun getGroupSmsDetailByTimestamp(timestamp: Long): GroupSmsDetail

    // Delete a message by timestamp
    @Query("DELETE FROM groupsmsdetail WHERE timestamp = :timestamp")
    suspend fun deleteGroupMessageByTimestamp(timestamp: Long)

    // Delete a message by timestamp
    @Query("DELETE FROM groupsmsdetail WHERE timestamp = :timestamp AND state = 'Draft'")
    suspend fun deleteGroupMessageByTimestampAndDraft(timestamp: Long)

    @Query("UPDATE groupsmsdetail SET isRead = 1 WHERE groupId = :groupId AND isRead = 0")
    suspend fun markAllGroupMessagesAsRead(groupId: Long)

    @Query("SELECT * FROM groupsmsdetail WHERE state = 'Draft' ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestGroupDraftMessage(): GroupSmsDetail?

    @Query("SELECT * FROM groupsmsdetail WHERE groupId = :groupId ORDER BY timestamp ASC")
    suspend fun getGroupSmsDetailById(groupId: Long): MutableList<GroupSmsDetail>

    @Query("SELECT * FROM groupsmsdetail WHERE groupId = :groupId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestGroupMessage(groupId: Long): GroupSmsDetail?

    @Query("SELECT * FROM groupsmsdetail WHERE groupId = :groupId GROUP BY codeStamp HAVING MAX(timestamp) ORDER BY timestamp ASC")
    suspend fun getGroupSmsDetailByIdUniqueCodeStamp(groupId: Long): MutableList<GroupSmsDetail>


    @Query("SELECT * FROM groupsmsdetail WHERE groupId = :groupId AND timestamp = 999999999 LIMIT 1")
    suspend fun getSpecificTimestampGroupMessage(groupId: Long): GroupSmsDetail?

    @Query("SELECT * FROM groupsmsdetail WHERE state = 'Failed'")
    suspend fun getFailedGroupMessages(): List<GroupSmsDetail>

    @Query("SELECT * FROM groupsmsdetail ORDER BY timestamp DESC LIMIT 2")
    suspend fun getAllGroupSmsDetails(): MutableList<GroupSmsDetail>

}


