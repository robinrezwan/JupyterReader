package com.robinrezwan.jupyterreader.util

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object FileUnzipper {
    fun unzip(zipInStream: ZipInputStream, destination: File) {
        if (destination.exists() && !destination.isDirectory) {
            throw IllegalStateException("Destination file must be a directory or non existent")
        }

        if (!destination.isDirectory) {
            destination.mkdirs()
        }

        val locationPath = destination.absolutePath.let {
            if (!it.endsWith(File.separator)) {
                "$it${File.separator}"
            } else {
                it
            }
        }

        var zipEntry: ZipEntry?
        var unzipFile: File
        var unzipParentDir: File?

        while (zipInStream.nextEntry.also { zipEntry = it } != null) {
            unzipFile = File(locationPath + zipEntry!!.name)

            if (zipEntry!!.isDirectory) {
                if (!unzipFile.isDirectory) {
                    unzipFile.mkdirs()
                }
            } else {
                unzipParentDir = unzipFile.parentFile

                if (unzipParentDir != null && !unzipParentDir.isDirectory) {
                    unzipParentDir.mkdirs()
                }

                BufferedOutputStream(FileOutputStream(unzipFile)).use { outStream ->
                    zipInStream.copyTo(outStream)
                }
            }
        }
    }
}
