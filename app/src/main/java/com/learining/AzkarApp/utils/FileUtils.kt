package com.learining.AzkarApp.utils

import android.content.Context
import android.net.Uri
import java.io.File

fun uriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)!!
    val file = File.createTempFile("upload", ".jpg", context.cacheDir)

    file.outputStream().use {
        inputStream.copyTo(it)
    }

    return file
}