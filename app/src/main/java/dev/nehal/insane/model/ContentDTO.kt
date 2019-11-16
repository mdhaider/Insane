package dev.nehal.insane.model

import java.io.Serializable
import java.util.*

data class ContentDTO(
    var uid: String? = null,
    var imgUrl: String? = null,
    var imgCaption: String? = null,
    var imgUploadDate: Long? = null,
    var favoriteCount: Int = 0,
    var favorites: MutableMap<String, Boolean> = HashMap()
) : Serializable {

    data class Comment(
        var uid: String? = null,
        var comment: String? = null,
        var commentDate: Long? = null
    )
}
