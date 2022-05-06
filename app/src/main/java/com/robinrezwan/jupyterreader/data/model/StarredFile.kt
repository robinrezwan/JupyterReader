package com.robinrezwan.jupyterreader.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File

@Entity(tableName = "starred_file_table")
data class StarredFile(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "file_path")
    val file: File,
    @ColumnInfo(name = "time_starred")
    val timeStarred: Long,
)
