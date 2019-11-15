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
import dev.nehal.insane.R
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
                R.layout.enter_mobile_fragment,
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
            if (it.length == 10) {
                hideKeyboard()
                binding.tilNumber.visibility = View.GONE
                showProgress(true)
                checkUserStatus(it)
            }
        }
    }

    private fun checkUserStatus(phNum: String) {
        Log.d(TAG, "checking status $phNum")
        phNumb = phNum

        val dbRef = db.collection("signUp").document(phNumb)

        dbRef.get()
            .addOnSuccessListener { document ->
                showProgress(false)
                binding.mNumber.text = null
                AppPreferences.phone = phNumb

                if (document.data != null) {
                    Log.d(ReqStatusFragment.TAG, "DocumentSnapshot data: ${document.data}")
                    if (document.getBoolean("approved")!!) {
                        Log.d(TAG, document.getBoolean("approved")!!.toString())
                        AppPreferences.userName = document.getString("userName")
                        goToVerifyPhone()
                    } else {
                        Log.d(TAG, "not approved yet")
                        AppPreferences.userName = document.getString("userName")
                        goToReqStatus()
                    }
                } else {
                    Log.d(TAG, "user not reg")
                    goToSignUp()

                }

            }.addOnFailureListener { exception ->
                showProgress(false)
                binding.tilNumber.visibility = View.VISIBLE
                binding.mNumber.text = null
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
        AppPreferences.signUpState = 1
        findNavController().navigate(R.id.action_entermobile_signupreq)
    }

    private fun goToReqStatus() {
        AppPreferences.signUpState = 2
        findNavController().navigate(R.id.action_entermobile_req_status)
    }

    private fun goToVerifyPhone() {
        val bundle = Bundle().apply {
            putBoolean(Const.IS_SIGNED_UP_ALREADY, true)
        }

        findNavController().navigate(R.id.action_entermobile_verify_phone, bundle)
    }

    private fun showProgress(shouldShow: Boolean) {
        if (shouldShow) {
            binding.prEnterPh.visibility = View.VISIBLE
        } else {
            binding.prEnterPh.visibility = View.GONE
        }

    }


}