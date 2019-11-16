package dev.nehal.insane.model

data class AlarmDTO(
    var destinationUid: String? = null,
    var userId: String? = null,
    var contentId: String? = null,
    var imageUri: String? = null,
    var uid: String? = null,
    var kind: Int = 0,
    var message: String? = null,
    var timestamp: Long? = null,
    var username: String? = null
)