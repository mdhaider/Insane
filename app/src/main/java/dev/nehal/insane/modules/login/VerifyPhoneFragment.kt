package dev.nehal.insane.modules.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.R
import dev.nehal.insane.databinding.VerifyPhoneFragmentBinding
import dev.nehal.insane.modules.MainActivity
import dev.nehal.insane.shared.AppPreferences
import dev.nehal.insane.shared.Const
import dev.nehal.insane.shared.onChange
import java.util.concurrent.TimeUnit

class VerifyPhoneFragment : Fragment() {
    private lateinit var binding: VerifyPhoneFragmentBinding
    private lateinit var phNum: String
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var mVerificationId: String? = null
    private var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var smsOTP: String
    private lateinit var mName: String

    companion object {
        const val TAG = "VerifyPhoneFragment"
    }

    private lateinit var viewModel: VerifyPhoneViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            phNum = "+91" + getString(Const.PHONE_NUM, "")
            mName = getString(Const.USER_NAME, "")

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.verify_phone_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(VerifyPhoneViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvOTP.setOnClickListener {
            binding.mOTP.visibility = View.VISIBLE
        }

        binding.mOTP.onChange {
            if (it.length == 6) {

                val credential = PhoneAuthProvider.getCredential(mVerificationId!!, it)
                signInWithPhoneAuthCredential(credential)
            }
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onCodeAutoRetrievalTimeOut(verificationId: String) {

                Log.d(TAG, "Time Out :( failed.Retry again!")
            }

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.e("onVerificationCompleted", "onVerificationCompleted:$credential")
                val smsMessageSent: String = credential.smsCode.toString()
                Log.e("the message is ----- ", smsMessageSent)
                smsOTP = smsMessageSent

                binding.mOTP.setText(smsMessageSent)
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.e("+++2", "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.e("Exception:", "FirebaseAuthInvalidCredentialsException", e)
                    Log.e("=========:", "FirebaseAuthInvalidCredentialsException " + e.message)


                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.e("Exception:", "FirebaseTooManyRequestsException", e)
                }

                // Show a message and update the UI
                Log.d(TAG, "Your Phone Number might be wrong or connection error.Retry again!")

            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                //for low level version which doesn't do auto verification save the verification code and the token

                // Save verification ID and resending token so we can use them later
                Log.e("onCodeSent===", "onCodeSent:$p0")

                mVerificationId = p0
                mResendToken = p1
            }

        }

        startPhoneNumberVerification(phNum)
    }

    // This method will send a code to a given phone number as an SMS
    private fun startPhoneNumberVerification(phoneNumber: String) {
        activity?.let {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,      // Phone number to verify
                60,               // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                it,            // Activity (for callback binding)
                callbacks
            )
        } // OnVerificationStateChangedCallbacks

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val mAuth = FirebaseAuth.getInstance()
        activity?.let {
            mAuth.signInWithCredential(credential)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        Log.e("Sign in with phone auth", "Success ${user.toString()}")
                        updateProf()
                    } else {

                        Log.d(TAG, "Your Phone Number Verification is failed.Retry again!")
                    }
                }
        }
    }

    private fun showHomeActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity!!.finish()

    }

    private fun updateProf() {
        val userName = AppPreferences.userName
        val user = FirebaseAuth.getInstance().currentUser

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(userName)
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SignUpFragment", "User profile updated.")
                    updateUsers(user.uid)
                    showHomeActivity()
                }
            }
    }

    private fun updateUsers(uid: String) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        try {
            val user = User(phNum.takeLast(10))
            user.UID = uid

            db.collection("signup").document(phNum).update("uid", uid)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with UID: $documentReference")
                }.addOnFailureListener { e ->
                Log.d(TAG, "UID failed")
            }
        } catch (e: Exception) {
            Log.d(TAG, "UID failed")
        }
    }
}
