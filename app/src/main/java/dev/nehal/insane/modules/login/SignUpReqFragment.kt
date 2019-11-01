package dev.nehal.insane.modules.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.R
import dev.nehal.insane.databinding.SignUpReqFragmentBinding
import dev.nehal.insane.shared.Const

class SignUpReqFragment : Fragment() {
    private lateinit var phNum: String
    private lateinit var binding: SignUpReqFragmentBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var viewModel: SignUpReqViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.apply {
            phNum = getString(Const.PHONE_NUM, "")

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
            user.pin=""
            user.isAdmin=false
            user.isApproved=false
            val id:String="${name[0]}"+"-"+phNum.subSequence(0,5)
            user.userID=id
            user.timseStamp=System.currentTimeMillis()

            db.collection("signup").document(phNum).set(user).addOnSuccessListener { documentReference ->
                Log.d("TAG", "DocumentSnapshot added with ID: $documentReference")
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
