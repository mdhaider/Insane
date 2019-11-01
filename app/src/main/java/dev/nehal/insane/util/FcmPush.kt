package dev.nehal.insane.util

import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dev.nehal.insane.model.PushDTO
import okhttp3.*
import java.io.IOException

class FcmPush() {
    val JSON = MediaType.parse("application/json; charset=utf-8")
    val url = "https://fcm.googleapis.com/fcm/send"
    val serverKey = "AAAAw7x5jKA:APA91bELIbLldNqaPXWPDzXpPd8Rrb5rHiH_3Fs2r5WBRac1kiSBDW5C4_vjmHISTPWgRzVEwkd4niftpBhp-k_xV0aMT7lFT65hHR3XdetACJrj9G2R_YI1OUWsO8WxOt2Nk1b4_ooh"

    var okHttpClient: OkHttpClient? = null
    var gson: Gson? = null
    init {
        gson = Gson()
        okHttpClient = OkHttpClient()
    }

    fun sendMessage(destinationUid: String, title: String, message: String) {
        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var token = task.result!!["pushtoken"].toString()
                println(token)
                var pushDTO = PushDTO()
                pushDTO.to = token
                pushDTO.notification?.title = title
                pushDTO.notification?.body = message

                var body = RequestBody.create(JSON, gson?.toJson(pushDTO))
                var request = Request
                        .Builder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "key=" + serverKey)
                        .url(url)
                        .post(body)
                        .build()
                okHttpClient?.newCall(request)?.enqueue(object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                    }
                    override fun onResponse(call: Call?, response: Response?) {
                        println(response?.body()?.string())
                    }
                })
            }
        }
    }
}