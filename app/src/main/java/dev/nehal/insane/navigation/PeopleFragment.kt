package dev.nehal.insane.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.R
import dev.nehal.insane.databinding.PeopleFragmentBinding
import dev.nehal.insane.model.Users


class PeopleFragment : Fragment() {
    private lateinit var binding: PeopleFragmentBinding
    private lateinit var adapter: PeopleAdapter
    private lateinit var list: ArrayList<Users>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.people_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mLayoutManager = LinearLayoutManager(activity)
        binding.rvUsers.layoutManager = mLayoutManager
        binding.rvUsers.itemAnimator = DefaultItemAnimator()
        binding.rvUsers.setHasFixedSize(true)

        binding.rvProgress.visibility=View.VISIBLE

        list = ArrayList()
        adapter = PeopleAdapter(list)

        binding.rvUsers.adapter = adapter

        getData()
    }

    private fun getData() {
        FirebaseFirestore.getInstance()
            .collection("signUp").get().addOnSuccessListener { result ->
                binding.rvProgress.visibility=View.GONE
                for (document in result) {
                    Log.d("PeopleFrag", "${document.id} => ${document.data}")
                    list.add(document.toObject(Users::class.java))
                }
                list.sortByDescending { it.accCreationDate }

                binding.guestCount.text = getString(R.string.guest_count, list.size.toString())
                adapter.notifyDataSetChanged()

            }
            .addOnFailureListener { exception ->
                binding.rvProgress.visibility=View.GONE
                Log.d("PeopleFrag", "Error getting documents: ", exception)
            }

    }
}