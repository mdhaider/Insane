package dev.nehal.insane.navigation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.model.SignUp

class PeopleViewModel : ViewModel() {
    var users: MutableLiveData<ArrayList<SignUp>> = MutableLiveData()
    var list: ArrayList<SignUp> = ArrayList()

    fun getUsers(): LiveData<java.util.ArrayList<SignUp>> {
        if (users.value == null) {
            FirebaseFirestore.getInstance()
                .collection("signup").get().addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d("PeopleFrag", "${document.id} => ${document.data}")
                        list.add(document.toObject(SignUp::class.java))
                    }
                    list.sortByDescending { it.accCreationReqDate }
                    users.postValue(list)

                }
                .addOnFailureListener { exception ->
                    Log.d("PeopleFrag", "Error getting documents: ", exception)
                }


        }
        return users
    }
}
