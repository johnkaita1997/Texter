package com.tafatalkstudent.Shared

import android.app.Activity
import androidx.paging.PagingSource

import androidx.paging.PagingState

/*
class SmsPagingSource(private val pageSize: Int, private val activity: Activity, private val resetState: Boolean = false) : PagingSource<Int, SmsDetail>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SmsDetail> {
        try {

            val pageNumber = params.key ?: 1
            val database = RoomDb(activity).getSmsDao()
            val smslist = database.getLatestPagedSmsList(pageNumber, pageSize)
            val modifiedSmsList = smslist.map { smsDetail ->
                smsDetail.copy(phoneNumber = smsDetail.phoneNumber?.replace("^\\+254".toRegex(), "0"))
            }
            val uniqueModifiedSmsMap = mutableMapOf<String, SmsDetail>()
            modifiedSmsList.sortedByDescending { it.timestamp }.forEach { smsDetail ->
                val phoneNumber = smsDetail.phoneNumber.orEmpty()
                if (!uniqueModifiedSmsMap.containsKey(phoneNumber)) {
                    uniqueModifiedSmsMap[phoneNumber] = smsDetail
                }
            }
            val thelist = uniqueModifiedSmsMap.values.toList()

            val prevKey = if (pageNumber > 1) pageNumber - 1 else null
            val nextKey = if (thelist.isNotEmpty()) pageNumber + 1 else null

            return LoadResult.Page(data = thelist, prevKey = prevKey, nextKey = nextKey)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, SmsDetail>): Int? {
        return null
    }

}

*/








class SmsPagingSource(private val pageSize: Int, private val activity: Activity, private val resetState: Boolean = false) : PagingSource<Int, SmsDetail>() {
    private var dataHash: Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SmsDetail> {
        try {
            val pageNumber = params.key ?: 1
            val database = RoomDb(activity).getSmsDao()
            val smslist = database.getLatestPagedSmsList(pageNumber, pageSize)

            // Calculate the hash of the data
            val newHash = smslist.hashCode()

            // Check if the data has changed, if not, return no data loaded
            if (newHash == dataHash) {
                return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
            }

            // Update the data hash
            dataHash = newHash

            // Your data processing logic...
            val modifiedSmsList = smslist.map { smsDetail ->
                smsDetail.copy(phoneNumber = smsDetail.phoneNumber?.replace("^\\+254".toRegex(), "0"))
            }
            val uniqueModifiedSmsMap = mutableMapOf<String, SmsDetail>()
            modifiedSmsList.sortedByDescending { it.timestamp }.forEach { smsDetail ->
                val phoneNumber = smsDetail.phoneNumber.orEmpty()
                if (!uniqueModifiedSmsMap.containsKey(phoneNumber)) {
                    uniqueModifiedSmsMap[phoneNumber] = smsDetail
                }
            }
            val thelist = uniqueModifiedSmsMap.values.toList()

            // Calculate prevKey and nextKey
            val prevKey = if (pageNumber > 1) pageNumber - 1 else null
            val nextKey = if (thelist.isNotEmpty()) pageNumber + 1 else null

            return LoadResult.Page(data = thelist, prevKey = prevKey, nextKey = nextKey)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, SmsDetail>): Int? {
        return null
    }
}


