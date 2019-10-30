package dev.nehal.insane.modules.newpost

 data class Post(
    var user: String? = null,
    var imageUri: String? = null,
    var caption: String? = null,
    var timestamp:Long?=0L)

