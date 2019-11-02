package dev.nehal.insane.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.R
import dev.nehal.insane.modules.login.User
import kotlinx.android.synthetic.main.item_all_users.view.*
import kotlinx.android.synthetic.main.people_fragment.view.*
import java.util.*

class PeopleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.people_fragment, container, false)
        view.users_recyclerview.adapter = AlarmRecyclerViewAdapter()
        view.users_recyclerview.layoutManager = LinearLayoutManager(activity)

        return view
    }

    inner class AlarmRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val userList = ArrayList<User>()

        val db= FirebaseFirestore.getInstance()

        init {db.collection("signup")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("PeopleFrag", "${document.id} => ${document.data}")
                    userList.add(document.toObject(User::class.java))
                }
                userList.sortByDescending { it.timseStamp }
                notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("PeopleFrag", "Error getting documents: ", exception)
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_all_users, parent, false)
            return CustomViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val profileImage = holder.itemView.user_imageview_profile
            val commentTextView = holder.itemView.user_textview_profile

            commentTextView.text=userList[position].mName

            Glide.with(activity!!)
                .load(userList[position].imageuri)
                .apply(RequestOptions().circleCrop())
                .into(profileImage)
        }

        override fun getItemCount(): Int {

            return userList.size
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    }
}