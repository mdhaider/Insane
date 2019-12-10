package dev.nehal.imgupload


import android.net.Uri
import dev.nehal.insane.model.ContentDTO

interface DataSource {

    suspend fun getDeals(): Result<List<Deal>>

    suspend fun newImage(contentDTO: ContentDTO): Result<Boolean>

    fun uploadImageWithUri(uri: Uri, block: ((Result<Uri>, Int) -> Unit)? = null)
}