package dev.nehal.insane.prelogin

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
import dev.nehal.insane.model.SignUp
import dev.nehal.insane.shared.AppPreferences
import dev.nehal.insane.shared.Const
import dev.nehal.insane.shared.hideKeyboard
import dev.nehal.insane.shared.onChange

class SignUpReqFragment : Fragment() {
    private lateinit var phNum: String
    private lateinit var binding: SignUpReqFragmentBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var viewModel: SignUpReqViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        phNum = AppPreferences.phone!!
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

        binding.tvphoneChange.text = getString(R.string.change, AppPreferences.phone)

        binding.mName.onChange {
            binding.btnReq.isEnabled = it.length > 3
        }

        binding.btnReq.setOnClickListener {
            hideKeyboard()
            binding.prEnterName.visibility = View.VISIBLE
            binding.llParent.visibility = View.GONE
            requestSignUp()
        }

        binding.tvphoneChange.setOnClickListener {
            goToEnterPhone()
        }
    }

    private fun goToEnterPhone() {
        AppPreferences.phone = ""
        AppPreferences.signUpState=0
        findNavController().navigate(R.id.action_signup_enterphone)
    }

    private fun requestSignUp() {
        db = FirebaseFirestore.getInstance()

        try {
            val name: String = binding.mName.text.toString().capitalize()
            val user = SignUp(phNum)
            user.phoneNumber=phNum
            user.userName = name
            user.isApproved = false
            user.isAdmin = false
            user.accCreationReqDate = System.currentTimeMillis()

            db.collection("signUp").document(phNum).set(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with ID: $documentReference")
                    binding.prEnterName.visibility = View.GONE
                    goToNext(name)
                }.addOnFailureListener { e ->
                    Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
                }
        } catch (e: Exception) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
            binding.prEnterName.visibility = View.GONE
            binding.llParent.visibility = View.VISIBLE
        }
    }

    private fun goToNext(name: String) {
        AppPreferences.userName = name
        AppPreferences.signUpState = 2
        findNavController().navigate(R.id.action_signupreq_reqstatus)
    }


    private fun goToVerifyPhone() {
        val bundle = Bundle().apply {
            putString(Const.PHONE_NUM, phNum)
            putString(Const.USER_NAME, binding.mName.text.toString())
            saveUserName()
        }

        findNavController().navigate(R.id.action_reqstatus_verifyphone, bundle)
    }

    private fun saveUserName() {
        AppPreferences.userName = binding.mName.text.toString()
    }
}
