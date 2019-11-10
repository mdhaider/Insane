package dev.nehal.insane.prelogin

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
import dev.nehal.insane.databinding.EnterMobileFragmentBinding
import dev.nehal.insane.modules.login.ReqStatusFragment
import dev.nehal.insane.shared.AppPreferences
import dev.nehal.insane.shared.Const
import dev.nehal.insane.shared.hideKeyboard
import dev.nehal.insane.shared.onChange

class EnterMobileFragment : Fragment() {
    private lateinit var binding: EnterMobileFragmentBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var phNumb: String

    companion object {
        const val TAG = "EnterMobileFragment"
    }

    private lateinit var viewModel: EnterMobileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                dev.nehal.insane.R.layout.enter_mobile_fragment,
                container,
                false
            )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EnterMobileViewModel::class.java)
        // TODO: Use the ViewModel

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()

        binding.mNumber.onChange {
            if (it.length == 10) checkUserStatus(it)
        }
    }

    private fun checkUserStatus(phNum: String) {
        Log.d(TAG, "checking status $phNum")
        phNumb = phNum
        hideKeyboard()

        val dbRef = db.collection("signup").document(phNumb)

        dbRef.get()
            .addOnSuccessListener { document ->
                AppPreferences.userid = binding.mNumber.text.toString()

                binding.mNumber.text = null

                if (document.data != null) {
                    Log.d(ReqStatusFragment.TAG, "DocumentSnapshot data: ${document.data}")
                    if (document.getBoolean("approved")!!) {
                        Log.d(TAG, document.getBoolean("approved")!!.toString())
                        goToVerifyPhone()
                    } else {
                        Log.d(TAG, "not approved yet")
                        goToReqStatus()
                    }
                } else {
                    Log.d(TAG, "user not reg")
                    goToSignUp()
                }

            }.addOnFailureListener { exception ->

                Log.d(TAG, exception.toString())
            }
    }

    private fun goToEnterPin() {
        val bundle = Bundle().apply {
            putString(Const.PHONE_NUM, phNumb)

        }

        findNavController().navigate(dev.nehal.insane.R.id.action_entermobile_enter_pin, bundle)
    }

    private fun goToCreatePin() {
        val bundle = Bundle().apply {
            putString(Const.PHONE_NUM, phNumb)
        }

        findNavController().navigate(dev.nehal.insane.R.id.action_entermobile_create_pin, bundle)
    }


    private fun goToSignUp() {
        val bundle = Bundle().apply {
            putString(Const.PHONE_NUM, phNumb)
        }

        findNavController().navigate(dev.nehal.insane.R.id.action_entermobile_signupreq, bundle)
    }

    private fun goToReqStatus() {
        val bundle = Bundle().apply {
            putString(Const.PHONE_NUM, phNumb)
        }

        findNavController().navigate(dev.nehal.insane.R.id.action_entermobile_req_status, bundle)
    }

    private fun goToVerifyPhone() {
        val bundle = Bundle().apply {
            putString(Const.PHONE_NUM, phNumb)
        }

        findNavController().navigate(dev.nehal.insane.R.id.action_entermobile_verify_phone, bundle)
    }
}
