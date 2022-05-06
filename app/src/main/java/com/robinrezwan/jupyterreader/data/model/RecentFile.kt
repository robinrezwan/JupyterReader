package com.robinrezwan.jupyterreader.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File

@Entity(tableName = "recent_file_table")
data class RecentFile(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "file_path")
    val file: File,
    @ColumnInfo(name = "last_viewed")
    val lastViewed: Long,
)
