package dev.nehal.insane.navigation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.modules.login.User

class PeopleViewModel : ViewModel() {
    var users: MutableLiveData<ArrayList<User>> = MutableLiveData()
    var list: ArrayList<User> = ArrayList()

    fun getUsers(): LiveData<java.util.ArrayList<User>> {
        if (users.value == null) {
            FirebaseFirestore.getInstance()
                .collection("signup").get().addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d("PeopleFrag", "${document.id} => ${document.data}")
                        list.add(document.toObject(User::class.java))
                    }
                    list.sortByDescending { it.timseStamp }
                    users.postValue(list)

                }
                .addOnFailureListener { exception ->
                    Log.d("PeopleFrag", "Error getting documents: ", exception)
                }


        }
        return users
    }
}
