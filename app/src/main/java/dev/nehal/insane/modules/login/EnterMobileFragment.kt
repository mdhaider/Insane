package dev.nehal.insane.modules.login

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore

import dev.nehal.insane.R
import dev.nehal.insane.databinding.EnterMobileFragmentBinding
import dev.nehal.insane.shared.onChange

class EnterMobileFragment : Fragment() {

    private lateinit var binding: EnterMobileFragmentBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var phNumb: String

    companion object {
        fun newInstance() = EnterMobileFragment()
        const val TAG = "EnterMobileFragment"
    }

    private lateinit var viewModel: EnterMobileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.enter_mobile_fragment, container, false)
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
        phNumb=phNum

        val dbRef = db.collection("users").document(phNumb)

        dbRef.get()
            .addOnSuccessListener { document ->

                if (document.data != null) {
                    Log.d(ReqStatusFragment.TAG, "DocumentSnapshot data: ${document.data}")
                    if (document.getBoolean("approved")!!) {
                        // binding.txtStatus.text="Wow, approved."
                        Log.d(TAG, document.getBoolean("approved")!!.toString())

                        if(document.getString("pin")!!.length==4){
                            goToEnterPin()
                        } else{
                            goToCreatePin()
                        }
                    } else {
                        //  binding.txtStatus.text="Oh Sorry, Not Approved yet."
                        Log.d(TAG, "not approved yet")
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
            putString(SignUpReqFragment.KEY_MOBILE, phNumb)

        }

        findNavController().navigate(R.id.action_entermobile_enter_pin, bundle)
    }

    private fun goToCreatePin() {
        val bundle = Bundle().apply {
            putString(SignUpReqFragment.KEY_MOBILE, phNumb )

        }

        findNavController().navigate(R.id.action_entermobile_create_pin, bundle)
    }


    private fun goToSignUp() {
        val bundle = Bundle().apply {
            putString(SignUpReqFragment.KEY_MOBILE, phNumb)

        }

        findNavController().navigate(R.id.action_entermobile_signupreq, bundle)
    }
}
