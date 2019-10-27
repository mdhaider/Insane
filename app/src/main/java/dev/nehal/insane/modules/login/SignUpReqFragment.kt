package dev.nehal.insane.modules.login

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

import dev.nehal.insane.R
import dev.nehal.insane.databinding.SignUpReqFragmentBinding
import java.util.*

class SignUpReqFragment : Fragment() {
    private lateinit var phNum: String
    private lateinit var binding: SignUpReqFragmentBinding
    private lateinit var db: FirebaseFirestore

    companion object {
        fun newInstance() = SignUpReqFragment()
        const val KEY_MOBILE = "mobile"
    }

    private lateinit var viewModel: SignUpReqViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.apply {
            phNum = getString(KEY_MOBILE, "")

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.sign_up_req_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SignUpReqViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnReq.setOnClickListener {
            requestSignUp()
        }
    }

    private fun requestSignUp() {
        db = FirebaseFirestore.getInstance()

        try {
            var name:String= binding.mName.text.toString().toLowerCase()
            val user = User(phNum)
            user.mName = name
            user.isApproved=false
            user.isFirstTimeUser=true
            val id:String="${name[0]}"+"-"+phNum.subSequence(0,5)
            user.userID=id

            db.collection("users").document(phNum).set(user).addOnSuccessListener { documentReference ->
                Log.d("TAG", "DocumentSnapshot added with ID: $documentReference")
                Toast.makeText(
                    activity,
                    "Successfully uploaded to the database :$documentReference)",
                    Toast.LENGTH_LONG
                ).show()
                goToNext()
            }.addOnFailureListener { e ->
                Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun goToNext() {

        val bundle = Bundle().apply {
            putString(ReqStatusFragment.KEY_NUMBER, phNum)
            putString(ReqStatusFragment.KEY_NAME, binding.mName.text.toString())
        }

        findNavController().navigate(R.id.action_signupreq_reqstatus, bundle)

    }
}
