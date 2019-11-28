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
import dev.nehal.insane.postlogin.ProfGridFragment
import dev.nehal.insane.shared.Const

class ProfileTabFragment : Fragment() {

    private lateinit var binding: FragmentProfileTabBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var uid: String
    private  var passedUid: String=""
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
        adapter.addFragment(ProfGridFragment(), "")
        adapter.addFragment(DetailFragment(), "")
        adapter.addFragment(AlarmFragment(), "")
        binding.profViewPager.adapter = adapter
        binding.profTabs.setupWithViewPager(binding.profViewPager)

        db = FirebaseFirestore.getInstance()

        arguments?.apply {
            passedUid = getString(Const.USER_UID, "")

        }

        if(passedUid.isEmpty() || passedUid==FirebaseAuth.getInstance().currentUser!!.uid){

            getData(FirebaseAuth.getInstance().currentUser!!.uid)
            binding.imgCancel.visibility=View.GONE
            binding.imgEdit.visibility=View.VISIBLE

        }else{
            getData(passedUid)
            binding.imgCancel.visibility=View.VISIBLE
            binding.imgEdit.visibility=View.GONE
        }

        binding.imgEdit.setOnClickListener {
            goToProfile()
        }

        binding.imgCancel.setOnClickListener {
            goToHome()
        }

        setTab()
    }

    private fun goToProfile() {
        findNavController().navigate(R.id.action_profile_edit)

    }

    private fun goToHome() {
        findNavController().navigate(R.id.action_profile_cancel)

    }


    private fun getData(uid: String) {
        val dbRef = db.collection("users").document(uid)

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

    private fun setData(user: Users) {

        binding.profileName.text = user.userName

        Glide.with(activity!!)
            .load(user.profImageUri)
            .error(R.drawable.ic_account)
            .placeholder(R.drawable.ic_account)
            .apply(RequestOptions().circleCrop())
            .into(binding.profileImage)
    }

    private fun setTab() {
        val tabIcons = intArrayOf(R.drawable.ic_tab_2, R.drawable.ic_1_tab, R.drawable.ic_favorite_border_black_24dp)
        for (i in 0 until binding.profTabs.tabCount) {
            if (binding.profTabs.getTabAt(i) != null) {
                binding.profTabs.getTabAt(i)!!.setIcon(tabIcons[i])
            }
        }
    }
}

