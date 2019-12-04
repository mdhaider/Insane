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
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.R
import dev.nehal.insane.databinding.RewardsFragmentBinding
import dev.nehal.insane.model.Rewards
import dev.nehal.insane.model.Users
import dev.nehal.insane.modules.login.VerifyPhoneFragment
import dev.nehal.insane.navigation.RewardsDetailFragment
import dev.nehal.insane.shared.AppPreferences
import dev.nehal.insane.shared.Const
import dev.nehal.insane.shared.ModelPreferences
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class RewardsFragment : Fragment() {
    private lateinit var binding: RewardsFragmentBinding
    private lateinit var adapter: RewardsAdapter
    private lateinit var list: ArrayList<Rewards>
    private lateinit var user: Users

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.rewards_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mLayoutManager = GridLayoutManager(activity, 2)
        binding.rvRewards.layoutManager = mLayoutManager
        binding.rvRewards.itemAnimator = DefaultItemAnimator()

        val itemOnClick: (Int) -> Unit = { position ->
            Log.d("pos", position.toString())
            goToDetail(position)
        }

        binding.tvViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_rewards_all)
        }


        list = ArrayList()
        user = ModelPreferences(activity!!).getObject(Const.PROF_USER, Users::class.java)!!

        adapter = RewardsAdapter(list, itemOnClick)

        binding.rvRewards.adapter = adapter

        binding.rvProgress.visibility = View.VISIBLE

        createRewards()
    }

    private fun createRewards() {
        val currentDateTime: Long = System.currentTimeMillis()

        var savedDate = AppPreferences.todaysDate

        if (savedDate == 0L) {
            val beforeDate = currentDateTime - 86400000
            AppPreferences.todaysDate = beforeDate
            savedDate = AppPreferences.todaysDate
        }
        //creating Date from millisecond
        val currentDate = Date(currentDateTime)
        var savedDate1 = Date(savedDate)

        Log.d("current Date:", currentDate.toString())

        var df: DateFormat = SimpleDateFormat("dd:MM:yy", Locale.getDefault())


        var date1: String = df.format(currentDate)
        var date2: String = df.format(savedDate1)

        val d1 = df.parse(date1)
        val d2 = df.parse(date2)

        if (d1.after(d2)) {
            Log.d("show", d1.after(d2).toString())
            AppPreferences.todaysDate = currentDateTime
            setData()
        } else {
            getData()
        }
    }

    private fun setData() {

        val db = FirebaseFirestore.getInstance()

        try {
            val rewards = Rewards(user.userUID)
            rewards.docId = ""
            rewards.uid = user.userUID
            rewards.creationDate = System.currentTimeMillis()
            rewards.revealDate = 0L
            rewards.coinValue = (0..50).random()
            rewards.isRevealed = false
            rewards.coinReason = "Surprise me"

            db.collection("rewards").add(rewards)
                .addOnSuccessListener { documentReference ->
                    Log.d("RewardsFragment", "DocumentSnapshot added with ID: $documentReference")
                    setDocId(documentReference.id)
                }.addOnFailureListener { e ->
                    binding.rvProgress.visibility = View.GONE

                }
        } catch (e: Exception) {
            binding.rvProgress.visibility = View.GONE
        }
    }

    private fun setDocId(docId: String) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        try {
            db.collection("rewards")
                .document(docId)
                .update("docId", docId)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with UID: $documentReference")
                    getData()
                }.addOnFailureListener { e ->
                    Log.d(VerifyPhoneFragment.TAG, "prof failed")
                }
        } catch (e: Exception) {
            Log.d(VerifyPhoneFragment.TAG, "prof failed")
        }
    }

    private fun getData() {
        FirebaseFirestore.getInstance()
            .collection("rewards").whereEqualTo("uid", user.userUID).get()
            .addOnSuccessListener { result ->
                binding.rvProgress.visibility = View.GONE
                for (document in result) {
                    Log.d("PeopleFrag", "${document.id} => ${document.data}")
                    list.add(document.toObject(Rewards::class.java))
                }
                list.sortByDescending { it.creationDate }

                var coin = 0

                for (item in list) {
                    if (item.isRevealed) {
                        coin = coin + item.coinValue
                    }
                }
                binding.rewardsCount.text = coin.toString()
                adapter.notifyDataSetChanged()

            }
            .addOnFailureListener { exception ->
                binding.rvProgress.visibility = View.GONE
                Log.d("PeopleFrag", "Error getting documents: ", exception)
            }
    }

    private fun goToDetail(position: Int) {
        val dialogFragment = RewardsDetailFragment()
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        val obj = list[position]
        bundle.putSerializable("REWARDS", obj)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(fragmentTransaction, "dialog")
    }
}