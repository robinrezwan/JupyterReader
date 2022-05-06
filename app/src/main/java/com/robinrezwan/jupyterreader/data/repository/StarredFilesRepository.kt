package com.robinrezwan.jupyterreader.data.repository

import androidx.annotation.WorkerThread
import com.robinrezwan.jupyterreader.data.dao.StarredFileDao
import com.robinrezwan.jupyterreader.data.model.StarredFile
import kotlinx.coroutines.flow.Flow

class StarredFilesRepository(private val starredFileDao: StarredFileDao) {
    fun getAllStarredFiles(): Flow<List<StarredFile>> {
        return starredFileDao.getAll()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertFile(starredFile: StarredFile) {
        starredFileDao.insert(starredFile)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllFiles() {
        starredFileDao.deleteAll()
    }
}
