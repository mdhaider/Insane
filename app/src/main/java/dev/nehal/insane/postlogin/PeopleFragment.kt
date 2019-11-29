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
import dev.nehal.insane.databinding.PeopleFragmentBinding
import dev.nehal.insane.model.AlarmDTO
import dev.nehal.insane.model.ContentDTO
import dev.nehal.insane.model.Users
import dev.nehal.insane.modules.login.VerifyPhoneFragment
import dev.nehal.insane.shared.Const
import dev.nehal.insane.shared.ModelPreferences


class PeopleFragment : Fragment() {
    private lateinit var binding: PeopleFragmentBinding
    private lateinit var adapter: PeopleAdapter
    private lateinit var list: ArrayList<Users>
    private lateinit var commentList: ArrayList<AlarmDTO>
    private lateinit var contentList: ArrayList<ContentDTO>

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

        binding.rvProgress.visibility = View.VISIBLE

        val itemOnClick: (Int) -> Unit = { position ->
            goToProfile(position)
        }

        val itemOnClick1 = object : PeopleAdapter.OnItemClickListener {
            override fun approveUser(users: Users) {
                saveDataChangeForApprove(users)
            }

            override fun blockUser(users: Users) {
                saveDataChangeForBlock(users)
            }
        }


        list = ArrayList()
        contentList = ArrayList()
        commentList= ArrayList()
        val user: Users =
            ModelPreferences(activity!!).getObject(Const.PROF_USER, Users::class.java)!!
        adapter = PeopleAdapter(list, contentList, commentList,user.isAdmin, itemOnClick, itemOnClick1)

        binding.rvUsers.adapter = adapter

        getData()
    }

    private fun goToProfile(position: Int) {

        val bundle = Bundle().apply {
            putString(Const.USER_UID, list[position].userUID)
        }

        findNavController().navigate(R.id.action_people_profile, bundle)

    }

    private fun getData() {
        FirebaseFirestore.getInstance()
            .collection("users").get().addOnSuccessListener { result ->

                for (document in result) {
                    Log.d("PeopleFrag", "${document.id} => ${document.data}")
                    list.add(document.toObject(Users::class.java))
                }
                list.sortByDescending { it.accCreationDate }

                getUploadCount()

            }
            .addOnFailureListener { exception ->
                binding.rvProgress.visibility = View.GONE
                Log.d("PeopleFrag", "Error getting documents: ", exception)
            }
    }

    private fun getUploadCount() {
        FirebaseFirestore.getInstance()
            .collection("uploadedImages").get().addOnSuccessListener { result ->

                for (document in result) {
                    Log.d("PeopleFrag", "${document.id} => ${document.data}")
                    contentList.add(document.toObject(ContentDTO::class.java))
                }
                contentList.sortByDescending { it.uid }
                getCommentCount()

            }
            .addOnFailureListener { exception ->
                binding.rvProgress.visibility = View.GONE
                Log.d("PeopleFrag", "Error getting documents: ", exception)
            }
    }

    private fun getCommentCount() {
        FirebaseFirestore.getInstance()
            .collection("userActivities").get().addOnSuccessListener { result ->
                binding.rvProgress.visibility = View.GONE

                for (document in result) {
                    Log.d("PeopleFrag", "${document.id} => ${document.data}")
                    commentList.add(document.toObject(AlarmDTO::class.java))
                }
                contentList.sortByDescending { it.uid }

                binding.guestCount.text = getString(R.string.guest_count, list.size.toString())
                adapter.notifyDataSetChanged()

            }
            .addOnFailureListener { exception ->
                binding.rvProgress.visibility = View.GONE
                Log.d("PeopleFrag", "Error getting documents: ", exception)
            }
    }


    private fun saveDataChangeForApprove(users: Users) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        try {
            db.collection("users")
                .document(users.userUID)
                .update("isApproved", true)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with UID: $documentReference")
                    adapter.notifyDataSetChanged()
                }.addOnFailureListener { e ->
                    Log.d(VerifyPhoneFragment.TAG, "prof failed")
                }
        } catch (e: Exception) {
            Log.d(VerifyPhoneFragment.TAG, "prof failed")
        }
    }

    private fun saveDataChangeForBlock(users: Users) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        try {
            db.collection("users")
                .document(users.userUID)
                .update("isBlocked", true)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with UID: $documentReference")
                    adapter.notifyDataSetChanged()
                }.addOnFailureListener { e ->
                    Log.d(VerifyPhoneFragment.TAG, "prof failed")
                }
        } catch (e: Exception) {
            Log.d(VerifyPhoneFragment.TAG, "prof failed")
        }
    }
}