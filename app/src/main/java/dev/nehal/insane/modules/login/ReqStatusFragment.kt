package dev.nehal.insane.modules.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.R
import dev.nehal.insane.databinding.ReqStatusFragmentBinding
import dev.nehal.insane.shared.AppPreferences

class ReqStatusFragment : Fragment() {

    private lateinit var binding: ReqStatusFragmentBinding
    private lateinit var phNum: String
    private lateinit var mName: String
    private lateinit var db: FirebaseFirestore

    companion object {
        fun newInstance() = ReqStatusFragment()
        const val TAG = "ReqStatusFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        phNum=AppPreferences.phone!!
        mName=AppPreferences.userName!!

    }

    private lateinit var viewModel: ReqStatusViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.req_status_fragment, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ReqStatusViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvReqNum.text=AppPreferences.phone!!
        binding.tvReqName.text=AppPreferences.userName!!

        db = FirebaseFirestore.getInstance()

        binding.checkStatus.setOnClickListener {
            checkStatus()
        }

        binding.tvChangeDet.setOnClickListener {
            AppPreferences.phone=""
            AppPreferences.userName=""
            goToEnterPhone()
            AppPreferences.signUpState=0

        }
    }

    private fun checkStatus() {
        val ref = db.collection("signup").document(phNum)

        ref.get()
            .addOnSuccessListener { document ->

                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    if (document.getBoolean("approved")!!) {
                        goToVerifyPhone()
                    } else {
                        binding.txtStatus.text = "Oh Sorry, Not Approved yet."
                    }

                } else {
                    Log.d(TAG, "No such document")
                }
            }.addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun goToVerifyPhone() {

        findNavController().navigate(R.id.action_reqstatus_verifyphone)
    }

    private fun goToEnterPhone() {

        findNavController().navigate(R.id.action_reqstatus_enteryphone)
    }
}
