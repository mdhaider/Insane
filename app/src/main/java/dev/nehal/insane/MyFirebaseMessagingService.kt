package dev.nehal.insane

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        //the logic here
    }

    override fun onNewToken(token: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val map = mutableMapOf<String, Any>()
        map["pushToken"] = token
        FirebaseFirestore.getInstance().collection("pushTokens").document(uid!!).set(map)
    }
}