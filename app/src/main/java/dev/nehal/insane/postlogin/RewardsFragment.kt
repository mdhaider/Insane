package dev.nehal.insane.postlogin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.R
import dev.nehal.insane.databinding.RewardsFragmentBinding
import dev.nehal.insane.model.Users
import dev.nehal.insane.navigation.RewardsDetailFragment
import dev.nehal.insane.shared.Const
import dev.nehal.insane.shared.ModelPreferences


class RewardsFragment : Fragment() {
    private lateinit var binding: RewardsFragmentBinding
    private lateinit var adapter: RewardsAdapter
    private lateinit var list: ArrayList<Users>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.rewards_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mLayoutManager = GridLayoutManager(activity,2)
        binding.rvRewards.layoutManager = mLayoutManager
        binding.rvRewards.itemAnimator = DefaultItemAnimator()


       // binding.rvProgress.visibility = View.VISIBLE

        val itemOnClick: (Int) -> Unit = { position ->
            Log.d("pos",position.toString())
            goToDetail(position)
        }


        list = ArrayList()
        val user: Users =
            ModelPreferences(activity!!).getObject(Const.PROF_USER, Users::class.java)!!
        adapter = RewardsAdapter(itemOnClick)

        binding.rvRewards.adapter = adapter

       // getData()
    }

    private fun getData() {
        FirebaseFirestore.getInstance()
            .collection("users").get().addOnSuccessListener { result ->
                binding.rvProgress.visibility = View.GONE
                for (document in result) {
                    Log.d("PeopleFrag", "${document.id} => ${document.data}")
                    list.add(document.toObject(Users::class.java))
                }
                list.sortByDescending { it.accCreationDate }

              //  binding.guestCount.text = getString(R.string.guest_count, list.size.toString())
                adapter.notifyDataSetChanged()

            }
            .addOnFailureListener { exception ->
                binding.rvProgress.visibility = View.GONE
                Log.d("PeopleFrag", "Error getting documents: ", exception)
            }
    }

    private fun goToDetail(position: Int) {
        val dialogFragment = RewardsDetailFragment() //here MyDialog is my custom dialog
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        dialogFragment.show(fragmentTransaction, "dialog")
    }
}