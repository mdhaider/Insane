package dev.nehal.insane.model

import java.io.Serializable

data class Rewards(
    var docId: String = "",
    var uid: String = "",
    var creationDate: Long = 0L,
    var revealDate: Long = 0L,
    var coinValue: Int = 0,
    var isRevealed: Boolean=false,
    var coinReason: String=""
):Serializable


