package com.robinrezwan.jupyterreader.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.robinrezwan.jupyterreader.data.model.StarredFile
import kotlinx.coroutines.flow.Flow

@Dao
interface StarredFileDao {
    @Query("SELECT * FROM starred_file_table ORDER BY time_starred DESC")
    fun getAll(): Flow<List<StarredFile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(starredFile: StarredFile)

    @Query("DELETE FROM starred_file_table")
    suspend fun deleteAll()
}
