package com.robinrezwan.jupyterreader.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.robinrezwan.jupyterreader.data.dao.RecentFileDao
import com.robinrezwan.jupyterreader.data.dao.StarredFileDao
import com.robinrezwan.jupyterreader.data.model.RecentFile
import com.robinrezwan.jupyterreader.data.model.StarredFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [RecentFile::class, StarredFile::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class JupyterToolDatabase : RoomDatabase() {

    abstract fun recentFileDao(): RecentFileDao
    abstract fun starredFileDao(): StarredFileDao

    private class JupyterToolDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var recentFileDao = database.recentFileDao()
                    var starredFileDao = database.starredFileDao()

                    // Insert files from here
                }
            }
        }
    }

    companion object {
        private const val DATABASE_NAME = "jupyter_tool_db"

        @Volatile
        private var INSTANCE: JupyterToolDatabase? = null

        fun getDatabase(
            context: Context,
            coroutineScope: CoroutineScope,
        ): JupyterToolDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JupyterToolDatabase::class.java,
                    DATABASE_NAME,
                ).addCallback(JupyterToolDatabaseCallback(coroutineScope)).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
