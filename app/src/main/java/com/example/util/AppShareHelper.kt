package com.example.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object AppShareHelper {

    suspend fun shareInstalledApk(
        context: Context,
        chooserTitle: String,
        shareMessage: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val sourceApk = File(context.applicationInfo.sourceDir)
            val shareDir = File(context.cacheDir, "shared_apks").apply { mkdirs() }
            val targetApk = File(shareDir, "Srabone.apk")

            if (!targetApk.exists() || targetApk.length() != sourceApk.length()) {
                sourceApk.inputStream().use { input ->
                    targetApk.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }

            val apkUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                targetApk
            )

            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.android.package-archive"
                putExtra(Intent.EXTRA_STREAM, apkUri)
                putExtra(Intent.EXTRA_SUBJECT, "Srabone App")
                putExtra(Intent.EXTRA_TEXT, shareMessage)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            withContext(Dispatchers.Main) {
                val chooser = Intent.createChooser(sendIntent, chooserTitle).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
            }
        }
    }
}
