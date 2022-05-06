package com.robinrezwan.jupyterreader.data.database

import androidx.room.TypeConverter
import java.io.File

class Converters {
    @TypeConverter
    fun pathToFile(path: String): File {
        return File(path)
    }

    @TypeConverter
    fun fileToPath(file: File): String {
        return file.canonicalPath
    }
}
