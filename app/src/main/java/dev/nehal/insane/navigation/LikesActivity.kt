package dev.nehal.insane.navigation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import dev.nehal.insane.R
import dev.nehal.insane.databinding.ActivityLikesBinding


class LikesActivity : AppCompatActivity() {
    private lateinit var likesList: ArrayList<String>
    private lateinit var adapter: LikesAdapter
    private lateinit var binding: ActivityLikesBinding
    private lateinit var uidArray: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_likes)

        getIntents()
        setData()

        binding.imgBack.setOnClickListener{
            finish()
        }

    }

    private fun getIntents() {
        likesList = ArrayList()
        val b = intent.extras
        if (b != null) {
            uidArray = b.getStringArray("uidlist") as Array<String>
        }

        uidArray.forEach {
            Log.d("dfg", it)
            likesList.add(it)
        }
    }

    private fun setData() {
        binding.rvLikes.setHasFixedSize(true)
        adapter = LikesAdapter(likesList)
        binding.rvLikes.adapter = adapter
        binding.rvLikes.layoutManager = LinearLayoutManager(this)

        binding.tvLikeCount.text = getString(R.string.likes_count, likesList.size.toString())
    }
}

