package com.robinrezwan.jupyterreader.data.repository

import java.io.File

class AllFilesRepository(private val externalFilesDirs: Array<File>) {
    fun getAllFiles(): List<File> {
        return getFilesFromStorageDevices()
    }

    private fun getFilesFromStorageDevices(): List<File> {
        val filesInDirs = mutableListOf<File>()

        externalFilesDirs.forEach { externalFilesDir ->
            val path = externalFilesDir.canonicalPath
            val rootPath = path.substringBefore("/Android")
            val rootDirectory = File(rootPath)
            val allDirs = rootDirectory.walk().filter { it.isDirectory }

            allDirs.forEach { dir ->
                val filesInDir = getFilesInDir(dir)

                if (filesInDir != null) {
                    filesInDirs.add(dir)
                    filesInDirs.addAll(filesInDir)
                }
            }
        }

        return filesInDirs
    }

    private fun getFilesInDir(dir: File): List<File>? {
        val filesInDir = mutableListOf<File>()

        dir.listFiles()?.forEach { file ->
            if (file.isFile && file.extension.equals("ipynb", ignoreCase = true)) {
                filesInDir.add(file)
            }
        }

        return filesInDir.ifEmpty { null }
    }

    fun deleteFile(file: File) {
        file.delete()
    }
}
