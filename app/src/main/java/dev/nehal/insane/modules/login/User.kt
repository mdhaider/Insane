package dev.nehal.insane.modules.login

data class User(
    var phonNum: String = "",
    var userID: String = "",
    var mName: String = "",
    var isApproved: Boolean = false,
    var timseStamp: Long = 0L,
    var pin: String = "",
    var isAdmin: Boolean = false,
    var imageuri: String = "",
    var UID: String = ""
)


