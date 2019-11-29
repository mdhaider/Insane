package dev.nehal.insane.prelogin

import android.app.Dialog
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
import dev.nehal.insane.databinding.EnterMobileFragmentBinding
import dev.nehal.insane.model.SignUp
import dev.nehal.insane.model.Users
import dev.nehal.insane.modules.login.ReqStatusFragment
import dev.nehal.insane.modules.login.VerifyPhoneFragment
import dev.nehal.insane.shared.hideKeyboard
import dev.nehal.insane.shared.onChange
import java.util.concurrent.TimeUnit

class EnterMobileFragment : Fragment() {
    private lateinit var binding: EnterMobileFragmentBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var userPhone: String
    private lateinit var userName: String
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var mVerificationId: String? = null
    private var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var smsOTP: String
    private lateinit var dialog: Dialog
    private var isUserSignedUp: Boolean = false

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
        dialog = ProgressDialog.progressDialog(activity!!)

        binding.mNumber.onChange {
            if (it.length == 10) {
                hideKeyboard()
                showProgress(true)
                userPhone = it
                checkUserStatus()
            }
        }

        binding.mName.onChange {
            if (it.length > 3) {
                userName = it
                binding.btnReq.isEnabled = true
                binding.btnReq.setTextColor(resources.getColor(R.color.white))
                binding.check2.visibility = View.VISIBLE
            } else {
                binding.btnReq.isEnabled = false
                binding.check2.visibility = View.GONE
            }
        }

        binding.btnReq.setOnClickListener {
            hideKeyboard()
            showProgress(true)
            requestSignUp()
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                dialog.dismiss()
                Log.d(VerifyPhoneFragment.TAG, "Time Out :( failed.Retry again!")
            }

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.e("onVerificationCompleted", "onVerificationCompleted:$credential")
                val smsMessageSent: String = credential.smsCode.toString()
                Log.e("the message is ----- ", smsMessageSent)
                smsOTP = smsMessageSent
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                dialog.dismiss()
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
                Log.d(
                    VerifyPhoneFragment.TAG,
                    "Your Phone Number might be wrong or connection error.Retry again!"
                )

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
    }

    private fun showProgress(shouldShow: Boolean) {
        if (shouldShow) {
            binding.prEnterPh.visibility = View.VISIBLE
        } else {
            binding.prEnterPh.visibility = View.GONE
        }
    }


    private fun checkUserStatus() {
        Log.d(TAG, "checking status $userPhone")

        val dbRef = db.collection("signUp").document(userPhone)

        dbRef.get()
            .addOnSuccessListener { document ->
                showProgress(false)
                if (document.data != null) {
                    Log.d(ReqStatusFragment.TAG, "DocumentSnapshot data: ${document.data}")
                    dialog.show()
                    isUserSignedUp = true
                    startPhoneNumberVerification()
                } else {
                    Log.d(TAG, "user not reg")
                    showSignUp()
                }

            }.addOnFailureListener { exception ->
                showProgress(false)
                binding.mNumber.text = null
                Log.d(TAG, exception.toString())
            }
    }

    private fun startPhoneNumberVerification() {
        val reqPhoneNumber = "+91$userPhone"
        Log.d("ghh", reqPhoneNumber)
        activity?.let {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                reqPhoneNumber,      // Phone number to verify
                60,               // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                it,            // Activity (for callback binding)
                callbacks
            )
        } // OnVerificationStateChangedCallbacks

    }

    private fun showSignUp() {
        binding.tilView2.visibility = View.VISIBLE
        binding.btnReq.visibility = View.VISIBLE
        binding.check1.visibility = View.VISIBLE
        binding.tilNumber.isEnabled = false

    }

    private fun requestSignUp() {
        db = FirebaseFirestore.getInstance()

        try {
            val name: String = userName.capitalize()
            val user = SignUp(userPhone)
            user.phoneNumber = userPhone
            user.userName = name
            user.isApproved = false
            user.isAdmin = false
            user.accCreationReqDate = System.currentTimeMillis()

            db.collection("signUp").document(userPhone).set(user)
                .addOnSuccessListener { documentReference ->
                    showProgress(false)
                    dialog.show()
                    startPhoneNumberVerification()
                    Log.d("TAG", "DocumentSnapshot added with ID: $documentReference")

                }.addOnFailureListener { e ->
                    showProgress(false)
                    Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
                }
        } catch (e: Exception) {
            showProgress(false)
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
        }
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
                        dialog.dismiss()
                        Log.d(
                            VerifyPhoneFragment.TAG,
                            "Your Phone Number Verification is failed.Retry again!"
                        )
                    }
                }
        }
    }

    private fun goToNext(uid: String) {
        if (isUserSignedUp) {
            showHomeActivity()
        } else {
            requestUsers(uid)
        }
    }

    private fun requestUsers(uid: String) {
        val db = FirebaseFirestore.getInstance()

        try {
            val user = Users(userPhone)
            user.phoneNumber = userPhone
            user.userName = userName
            user.userUID = uid
            user.isAdmin=false
            user.isApproved=false
            user.isBlocked=false
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
            dialog.dismiss()
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun showHomeActivity() {
        dialog.dismiss()
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity!!.finish()
    }
}