package com.tafatalkstudent.Shared
import android.app.Activity
import androidx.paging.PagingSource
import androidx.paging.PagingState

class SmsPagingSource(private val viewModel: MyViewModel, private val activity: Activity) : PagingSource<Int, SmsDetail>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SmsDetail> {

        fun invalidate() {
            invalidate()
        }

        try {
            val nextPageNumber = params.key ?: 1 // Initial page number is 1
            val pageSize = params.loadSize
            val smsMessages = viewModel.getLatestPagedSmsList(nextPageNumber, pageSize, activity)
            return LoadResult.Page(
                data = smsMessages,
                prevKey = if (nextPageNumber == 1) null else nextPageNumber - 1,
                nextKey = if (smsMessages.isEmpty()) null else nextPageNumber + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, SmsDetail>): Int? {
        return null
    }

}
