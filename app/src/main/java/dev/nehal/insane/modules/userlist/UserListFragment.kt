package dev.nehal.insane.modules.userlist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import dev.nehal.insane.R
import dev.nehal.insane.databinding.UserListFragmentBinding
import dev.nehal.insane.modules.login.CreatePinFragment
import dev.nehal.insane.modules.login.User
import dev.nehal.insane.shared.AppPreferences
import java.text.SimpleDateFormat
import java.util.*


class UserListFragment : Fragment() {
    private val TAG = "UserListFragment"
    private var adapter: FirestoreRecyclerAdapter<User, UserListViewHolder>? = null
    private lateinit var binding: UserListFragmentBinding
    private var firestoreListener: ListenerRegistration? = null
    private var notesList = mutableListOf<User>()
    private var firestoreDB: FirebaseFirestore? = null
    private var isActive:Boolean=false

    companion object {
        fun newInstance() = UserListFragment()
    }

    private lateinit var viewModel: UserListViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.title = "All Users"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.user_list_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(UserListViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestoreDB = FirebaseFirestore.getInstance()

        val mLayoutManager = LinearLayoutManager(activity)
        binding.rvUser.layoutManager = mLayoutManager
        binding.rvUser.itemAnimator = DefaultItemAnimator()
        binding.rvUser.setHasFixedSize(true)

        loadNotesList()

        firestoreListener =
            firestoreDB!!.collection("users")
                .addSnapshotListener(EventListener { documentSnapshots, e ->
                    if (e != null) {
                        Log.e(TAG, "Listen failed!", e)
                        return@EventListener
                    }

                    notesList = mutableListOf()

                    for (doc in documentSnapshots!!) {
                        val note = doc.toObject(User::class.java)
                        // note.user = doc.i
                        notesList.add(note)
                    }

                    adapter!!.notifyDataSetChanged()
                    binding.rvUser.adapter = adapter
                })
    }

    private fun loadNotesList() {
        Log.d(TAG, "load method called")
        val query =
            firestoreDB!!.collection("users")

        val response = FirestoreRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .build()

        adapter = object : FirestoreRecyclerAdapter<User, UserListViewHolder>(response) {
            override fun onBindViewHolder(holder: UserListViewHolder, position: Int, model: User) {
                val user = notesList[position]

                holder.name.text = user.mName
                holder.time.text = getTimeStamp(user.timseStamp)
                holder.phone.text = user.phonNum

                Glide.with(activity!!).load(user.imageuri).error(R.drawable.ic_person_black_24dp)
                    .apply(RequestOptions.circleCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imgProf)

                if(AppPreferences.isAdmin){
                    holder.llAdmin.visibility=View.VISIBLE
                }

                if(user.isApproved){
                    holder.tvStatus.text=getString(R.string.deactivate)
                    isActive=false
                } else{
                    holder.tvStatus.text=getString(R.string.activate)
                    isActive=true
                }

                holder.tvStatus.setOnClickListener{
                    updateStatus(user.phonNum)
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_user_all, parent, false)

                return UserListViewHolder(view)
            }

            override fun onError(e: FirebaseFirestoreException) {
                Log.e("error", e!!.message)
            }
        }

        adapter!!.notifyDataSetChanged()
        binding.rvUser.adapter = adapter
    }

    fun getTimeStamp(timeinMillies: Long): String? {
        var date: String? = null
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        date = formatter.format(Date(timeinMillies))
        println("Today is " + date!!)
        return date
    }

    override fun onStart() {
        super.onStart()

        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()

        adapter!!.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()

        firestoreListener!!.remove()
    }

    private fun updateStatus(phoneNum:String){
        try {
            val ref = firestoreDB!!.collection("users").document(phoneNum)
            ref.update("approved", isActive)
                .addOnSuccessListener { Log.d(CreatePinFragment.TAG, "DocumentSnapshot successfully updated!") }
                .addOnFailureListener { e -> Log.w(CreatePinFragment.TAG, "Error updating document", e) }
        } catch (e: Exception) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
        }
    }



}


