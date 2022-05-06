package com.robinrezwan.jupyterreader.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.robinrezwan.jupyterreader.data.model.RecentFile
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentFileDao {
    @Query("SELECT * FROM recent_file_table ORDER BY last_viewed DESC")
    fun getAll(): Flow<List<RecentFile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recentFile: RecentFile)

    @Query("DELETE FROM recent_file_table")
    suspend fun deleteAll()
}
