package com.tafatalkstudent.Shared

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








