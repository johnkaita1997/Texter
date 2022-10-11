package paita.stream_app_final.Tafa.Shared

import androidx.room.*
import paita.stream_app_final.Tafa.Adapters.ContinueWatchingVideo

@Dao
interface ContinueWatchingDao {

    @Insert
    suspend fun addContinueWatchingVideo(continueWatchingVideo: ContinueWatchingVideo)

    @Query("SELECT * FROM continuewatchingvideo ORDER BY id DESC")
    suspend fun getAllContinueWatchingVideo(): List<ContinueWatchingVideo>

    @Insert
    suspend fun addMultipleNotes(vararg continuewatchingvideo:ContinueWatchingVideo)

    @Update
    suspend fun updateNote(continueWatchingVideo: ContinueWatchingVideo)

    @Delete
    suspend fun deleteNote(continueWatchingVideo: ContinueWatchingVideo)

}