package paita.stream_app_final.Tafa.Shared;

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import paita.stream_app_final.Tafa.Adapters.ContinueWatchingVideo

@Database(entities = [ContinueWatchingVideo::class], version = 1)

abstract class WatchingDatabase : RoomDatabase() {


    abstract fun getVideoDao(): ContinueWatchingDao

    companion object {

        @Volatile
        private var instance: WatchingDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }
        private fun buildDatabase(context: Context) = Room.databaseBuilder(context.applicationContext, WatchingDatabase::class.java, "watchingdatabase").build()

    }

}