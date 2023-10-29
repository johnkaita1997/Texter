package com.tafatalkstudent.Shared

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable


@Entity(indices = [Index(value = ["timestamp"])], primaryKeys = ["timestamp"])
data class SmsDetail(
    val body: String?,
    val phoneNumber: String?,
    var timestamp: Long?,
    val state: String?,  // Formatted timestamp, e.g., "8:20 PM"
    val type: Int?,  // Message type: 1 for received, 2 for sent
    val formattedTimestamp: String?,  // Delivery status of the message, e.g., "Delivered"
    val status: String?,  // Indicates whether the message has been read
    val name: String?,  // Indicates whether the message has been read
    val isRead: Boolean?,  // Indicates whether the message has been read
)

@Entity(indices = [Index(value = ["timestamp"])])
data class GroupSmsDetail(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = 0,
    val body: String?,
    val phoneNumber: String?,
    var timestamp: Long?,
    val state: String?,
    val type: Int?,
    val formattedTimestamp: String?,
    val status: String?,
    val isRead: Boolean?,
    val groupId: Long?,
    val groupName: String?,
    val senderName:String?,
    val senderNumber: String?,
    val codeStamp: Long?
)

@Entity
data class SimCard(
    @PrimaryKey
    val id: Int? = 0,
    val body: Int?,
)


data class Contact(
    val name: String?,
    val phoneNumber: String?
): Serializable


@Entity
data class Groups(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = 0,
    val name: String?,
    val description: String?,
    val members: MutableList<Contact>
)


class Converters {

    @TypeConverter
    fun fromJson(value: String): MutableList<Contact> {
        val listType = object : TypeToken<MutableList<Contact>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toJson(value: MutableList<Contact>): String {
        return Gson().toJson(value)
    }


}

data class LoginBody(var phone: String?, var password: String?)
data class SuccessLogin(
    val access: String?,
    val refresh: String?
)
data class SuccessLoginWithoutRefreshToken(
    val token: String?,
)

data class User(
    val email: String?,
    val password: String?,
    val first_name: String?,
    val last_name: String?,
    val phone: String?,
)

data class PostSmsBody(
    val body: String,
    val name: String,
    val phoneNumber: String,
    val state: String,
    val status: String,
    val time: String,
    val timestamp: String,
    val type: String
)

data class PostGroup(
    val description: String,
    val groupId: String,
    val members: MutableList<Contact>,
    val name: String
)



class GetScheduledSms : ArrayList<GetScheduledSmsItem>()
data class GetScheduledSmsItem(
    val body: String?,
    val client: Client?,
    val date_created: String?,
    val formattedTimestamp: String?,
    val groupId: Int?,
    val id: String?,
    val is_sent: Boolean?,
    val timestamp: Long?,
    val to: List<String>?,
    val type: Int?
)

data class Client(
    val date_created: String,
    val email: String,
    val first_name: String,
    val id: String,
    val is_active: String,
    val is_suspended: String,
    val last_name: String,
    val phone: String,
    val user_groups: List<UserGroup>
)

data class UserGroup(
    val id: String,
    val name: String
)

data class PutScheduleSms(
    val to: List<String>?,
    val body: String?,
    val groupId: Int?,
    val type: Int?,
    val sent_id: String?,
)