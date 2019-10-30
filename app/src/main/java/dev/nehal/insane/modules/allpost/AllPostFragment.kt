package dev.nehal.insane.modules.allpost

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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.*
import dev.nehal.insane.R
import dev.nehal.insane.databinding.AllPostFragmentBinding
import dev.nehal.insane.modules.newpost.Post

class AllPostFragment : Fragment() {
    private val TAG = "MainActivity"

    private var adapter: FirestoreRecyclerAdapter<Post, AllPostViewHolder>? = null
    private lateinit var binding:AllPostFragmentBinding
    private var firestoreListener: ListenerRegistration? = null
    private var notesList = mutableListOf<Post>()
    private var firestoreDB: FirebaseFirestore? = null


    companion object {
        fun newInstance() = AllPostFragment()
    }

    private lateinit var viewModel: AllPostViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.title = "All Posts"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater, R.layout.all_post_fragment,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AllPostViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestoreDB = FirebaseFirestore.getInstance()

        val mLayoutManager = LinearLayoutManager(activity)
        binding.rvPosts.layoutManager = mLayoutManager
        binding.rvPosts.itemAnimator = DefaultItemAnimator()

        loadNotesList()

        firestoreListener = firestoreDB!!.collection("posts").orderBy("timestamp",Query.Direction.DESCENDING)
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed!", e)
                    return@EventListener
                }

                notesList = mutableListOf()

                for (doc in documentSnapshots!!) {
                    val note = doc.toObject(Post::class.java)
                   // note.user = doc.i
                    notesList.add(note)
                }

                adapter!!.notifyDataSetChanged()
                binding.rvPosts.adapter = adapter
            })
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

    private fun loadNotesList() {

        val query = firestoreDB!!.collection("posts").orderBy("timestamp",Query.Direction.DESCENDING)

        val response = FirestoreRecyclerOptions.Builder<Post>()
            .setQuery(query, Post::class.java)
            .build()

        adapter = object : FirestoreRecyclerAdapter<Post, AllPostViewHolder>(response) {
            override fun onBindViewHolder(holder: AllPostViewHolder, position: Int, model: Post) {
                val note = notesList[position]

                holder.user.text = note.user
                holder.time.text = note.timestamp.toString()
                holder.post.text=note.caption
                Glide.with(activity!!).load(note.imageUri).
                    diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.img)

              //  holder.edit.setOnClickListener { updateNote(note) }

              //  holder.delete.setOnClickListener { deleteNote(note.id!!) }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllPostViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_post_all, parent, false)

                return AllPostViewHolder(view)
            }

            override fun onError(e: FirebaseFirestoreException) {
                Log.e("error", e!!.message)
            }
        }

        adapter!!.notifyDataSetChanged()
        binding.rvPosts.adapter = adapter
    }

}
