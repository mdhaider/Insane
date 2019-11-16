package dev.nehal.insane.model

data class AlarmDTO(
    var contentUid: String? = null,
    var destinationUid: String? = null,
    var uid: String? = null,
    var imageUri: String? = null,
    var kind: Int = 0,
    var message: String? = null,
    var activityDate: Long? = null
)