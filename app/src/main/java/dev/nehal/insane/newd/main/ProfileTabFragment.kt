package dev.nehal.insane.newd.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.R
import dev.nehal.insane.databinding.FragmentProfileTabBinding
import dev.nehal.insane.model.Users
import dev.nehal.insane.navigation.AlarmFragment
import dev.nehal.insane.navigation.DetailFragment
import dev.nehal.insane.navigation.GridFragment

class ProfileTabFragment : Fragment() {

    private lateinit var binding: FragmentProfileTabBinding
    private lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_tab, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter =
            SectionsPagerAdapter(activity!!, childFragmentManager)
        adapter.addFragment(GridFragment(), "All")
        adapter.addFragment(DetailFragment(), "List")
        adapter.addFragment(AlarmFragment(), "Noti")
        binding.profViewPager.adapter = adapter
        binding.profTabs.setupWithViewPager(binding.profViewPager)

        db= FirebaseFirestore.getInstance()

       // setProfileImage()

        getData()

        binding.profEdit.setOnClickListener {
            goToProfile()
        }
    }

    private fun goToProfile() {
        findNavController().navigate(R.id.action_profile_edit)

    }

    private fun setProfileImage() {
        FirebaseFirestore.getInstance().collection("profileImages")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val url = task.result!!["image"]
                    Glide.with(activity!!)
                        .load(url)
                        .error(R.drawable.ic_account)
                        .placeholder(R.drawable.ic_account)
                        .apply(RequestOptions().circleCrop())
                        .into(binding.profileImage)

                }
            }
    }

    private fun getData() {
        val dbRef = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)

        dbRef.get()
            .addOnSuccessListener { document ->
                val users = document.toObject(Users::class.java)
                if (users != null) {
                    setData(users)
                }

            }.addOnFailureListener { exception ->
                Log.d("ProfileFragment", exception.toString())
            }
    }

    private fun setData(user:Users){

        binding.profileName.text=user.userName

        Glide.with(activity!!)
            .load(user.profImageUri)
            .error(R.drawable.ic_account)
            .placeholder(R.drawable.ic_account)
            .apply(RequestOptions().circleCrop())
            .into(binding.profileImage)
    }
}
