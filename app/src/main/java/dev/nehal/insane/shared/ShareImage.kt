package dev.nehal.insane.shared

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import androidx.core.app.ActivityCompat


object ShareImage {
    fun shareImageWith(context: Context, drawable: Drawable) {
        val activity = context as Activity
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            val bm = (drawable as BitmapDrawable).bitmap
            val intent = Intent(Intent.ACTION_SEND)
            //   intent.putExtra(Intent.EXTRA_TEXT, "YOUR TEXT")
            val path =
                MediaStore.Images.Media.insertImage(context.contentResolver, bm, "", null)
            val screenshotUri = Uri.parse(path)

            intent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
            intent.type = "image/*"
            context.startActivity(Intent.createChooser(intent, "Share image via..."))
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }
    }
}