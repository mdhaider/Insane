package dev.nehal.insane.modules.postliked

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import dev.nehal.insane.R
import dev.nehal.insane.databinding.LikedPostFragmentBinding
import dev.nehal.insane.shared.AppPreferences
import java.text.SimpleDateFormat
import java.util.*

class LikedPostFragment : Fragment() {
    private val TAG = "LikedPost"
    private var adapter: FirestoreRecyclerAdapter<LikedPost, LikedPostViewHolder>? = null
    private lateinit var binding: LikedPostFragmentBinding
    private var firestoreListener: ListenerRegistration? = null
    private var notesList = mutableListOf<LikedPost>()
    private var firestoreDB: FirebaseFirestore? = null

    companion object {
        fun newInstance() = LikedPostFragment()
    }

    private lateinit var viewModel: LikedPostViewModel


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.title = "Favorite Posts"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.liked_post_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LikedPostViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestoreDB = FirebaseFirestore.getInstance()

        val mLayoutManager = LinearLayoutManager(activity)
        binding.rvPosts.layoutManager = mLayoutManager
        binding.rvPosts.itemAnimator = DefaultItemAnimator()
        binding.rvPosts.setHasFixedSize(true)

        loadNotesList()

        firestoreListener =
            firestoreDB!!.collection("favorites").document(AppPreferences.phone!!).collection("posts")
                .addSnapshotListener(EventListener { documentSnapshots, e ->
                    if (e != null) {
                        Log.e(TAG, "Listen failed!", e)
                        return@EventListener
                    }

                    notesList = mutableListOf()

                    for (doc in documentSnapshots!!) {
                        val note = doc.toObject(LikedPost::class.java)
                        // note.user = doc.i
                        notesList.add(note)
                    }

                    adapter!!.notifyDataSetChanged()
                    binding.rvPosts.adapter = adapter
                })
    }

    private fun loadNotesList() {
        Log.d("xyz", "load called")
        val query =
            firestoreDB!!.collection("favorites").document("9962232611").collection("posts")

        val response = FirestoreRecyclerOptions.Builder<LikedPost>()
            .setQuery(query, LikedPost::class.java)
            .build()

        adapter = object : FirestoreRecyclerAdapter<LikedPost, LikedPostViewHolder>(response) {
            override fun onBindViewHolder(
                holder: LikedPostViewHolder,
                position: Int,
                model: LikedPost
            ) {
                val note = notesList[position]
                holder.time.text = getTimeStamp(note.timestamp!!)
                holder.post.text = note.postId
                Log.d("xyz", note.toString())

            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikedPostViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_post_liked, parent, false)

                return LikedPostViewHolder(view)
            }

            override fun onError(e: FirebaseFirestoreException) {
                Log.e("error", e.message)
            }
        }

        adapter!!.notifyDataSetChanged()
        binding.rvPosts.adapter = adapter
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

    fun getTimeStamp(timeinMillies: Long): String? {
        var date: String? = null
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        date = formatter.format(Date(timeinMillies))
        println("Today is " + date!!)
        return date
    }


}
