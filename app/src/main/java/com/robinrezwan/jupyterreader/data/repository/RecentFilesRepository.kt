package com.robinrezwan.jupyterreader.data.repository

import com.robinrezwan.jupyterreader.data.dao.RecentFileDao
import com.robinrezwan.jupyterreader.data.model.RecentFile
import kotlinx.coroutines.flow.Flow

class RecentFilesRepository(private val recentFileDao: RecentFileDao) {
    fun getAllRecentFiles(): Flow<List<RecentFile>> {
        return recentFileDao.getAll()
    }

    suspend fun insertFile(recentFile: RecentFile) {
        recentFileDao.insert(recentFile)
    }

    suspend fun deleteAllFiles() {
        recentFileDao.deleteAll()
    }
}
