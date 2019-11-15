package dev.nehal.insane.modules.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.R
import dev.nehal.insane.databinding.VerifyPhoneFragmentBinding
import dev.nehal.insane.model.Users
import dev.nehal.insane.newd.main.MainActivity1
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
    private var isSignedUpAlready: Boolean = false

    companion object {
        const val TAG = "VerifyPhoneFragment"
    }

    private lateinit var viewModel: VerifyPhoneViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        phNum = "+91" + AppPreferences.phone!!
        mName = AppPreferences.userName!!

        arguments?.apply {
            isSignedUpAlready = getBoolean(Const.IS_SIGNED_UP_ALREADY, false)
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
                        goToNext(user?.uid!!)
                    } else {
                        Log.d(TAG, "Your Phone Number Verification is failed.Retry again!")
                    }
                }
        }
    }

    private fun goToNext(uid: String) {
        if (isSignedUpAlready) {
            showHomeActivity()
        } else {
            requestSignUp(uid)
        }
    }

    private fun showHomeActivity() {
        binding.prVerify.visibility = View.GONE
        val intent = Intent(activity, MainActivity1::class.java)
        startActivity(intent)
        activity!!.finish()
    }

    private fun requestSignUp(uid: String) {
        val db = FirebaseFirestore.getInstance()

        try {
            val user = Users(AppPreferences.phone!!)
            user.phoneNumber = AppPreferences.phone!!
            user.userName = AppPreferences.userName!!
            user.userUID = uid
            user.profImageUri = ""
            user.accCreationDate = System.currentTimeMillis()

            db.collection("users").document(uid).set(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with ID: $documentReference")
                    showHomeActivity()
                }.addOnFailureListener { e ->
                    Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
                }
        } catch (e: Exception) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
        }
    }
}
