package dev.nehal.insane.model

import java.io.Serializable
import java.util.*

data class ContentDTO(var explain: String? = null,
                      var imageUrl: String? = null,
                      var uid: String? = null,
                      var userId: String? = null,
                      var userName: String? = null,
                      var timestamp: Long? = null,
                      var favoriteCount: Int = 0,
                      var favorites: MutableMap<String, Boolean> = HashMap()):Serializable {

    data class Comment(var uid: String? = null,
                       var userId: String? = null,
                       var comment: String? = null,
                       var username:String?=null,
                       var timestamp: Long? = null)
}
