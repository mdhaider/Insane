package dev.nehal.insane.model

data class Users(
    var phoneNumber: String = "",
    var userUID: String = "",
    var userName: String = "",
    var isApproved: Boolean = false,
    var isAdmin: Boolean = false,
    var isBlocked: Boolean = false,
    var accCreationDate: Long = 0L,
    var profImageUri: String = ""

)


