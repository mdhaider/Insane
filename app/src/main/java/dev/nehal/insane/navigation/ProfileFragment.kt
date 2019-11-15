package dev.nehal.insane.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.R
import dev.nehal.insane.databinding.ProfileFragmentBinding


class ProfileFragment : Fragment() {
    private lateinit var binding: ProfileFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()
    }

    private fun getData() {
        FirebaseFirestore.getInstance()
            .collection("signup").get().addOnSuccessListener { result ->
                binding.rvProgress.visibility=View.GONE
                for (document in result) {
                    Log.d("PeopleFrag", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                binding.rvProgress.visibility=View.GONE
                Log.d("PeopleFrag", "Error getting documents: ", exception)
            }
    }
}