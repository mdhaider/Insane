package dev.nehal.insane.model

data class SignUp(
    var phoneNumber: String = "",
    var userName: String = "",
    var isApproved: Boolean = false,
    var isAdmin: Boolean = false,
    var accCreationReqDate: Long = 0L
)


