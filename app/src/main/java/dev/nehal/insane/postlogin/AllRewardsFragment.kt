package dev.nehal.insane.postlogin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.R
import dev.nehal.insane.databinding.AllRewardsFragmentBinding
import dev.nehal.insane.model.Rewards


class AllRewardsFragment : Fragment() {
    private lateinit var binding: AllRewardsFragmentBinding
    private lateinit var adapter: AllRewardsAdapter
    private lateinit var list: ArrayList<Rewards>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.all_rewards_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mLayoutManager = LinearLayoutManager(activity)
        binding.rvRewards.layoutManager = mLayoutManager
        binding.rvRewards.itemAnimator = DefaultItemAnimator()

        list = ArrayList()

        adapter = AllRewardsAdapter(list)

        binding.rvRewards.adapter = adapter
        binding.rvProgress.visibility = View.VISIBLE

        binding.imgBack.setOnClickListener {
            findNavController().navigate(R.id.action_all_rewards)
        }

        getData()
    }

    private fun getData() {
        FirebaseFirestore.getInstance()
            .collection("rewards").get()
            .addOnSuccessListener { result ->
                binding.rvProgress.visibility = View.GONE
                for (document in result) {
                    Log.d("PeopleFrag", "${document.id} => ${document.data}")
                    list.add(document.toObject(Rewards::class.java))
                }
                list.sortByDescending { it.revealDate }
                adapter.notifyDataSetChanged()

            }
            .addOnFailureListener { exception ->
                binding.rvProgress.visibility = View.GONE
                Log.d("PeopleFrag", "Error getting documents: ", exception)
            }
    }
}