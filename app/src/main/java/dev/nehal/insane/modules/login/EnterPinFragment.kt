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
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.R
import dev.nehal.insane.databinding.EnterPinFragmentBinding
import dev.nehal.insane.prelogin.MainActivity
import dev.nehal.insane.shared.Const
import dev.nehal.insane.shared.FireConst
import dev.nehal.insane.shared.onChange


class EnterPinFragment : Fragment() {
    private lateinit var binding: EnterPinFragmentBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var phNum: String
    private lateinit var viewModel: EnterPinViewModel

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
        binding = DataBindingUtil.inflate(inflater, R.layout.enter_pin_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EnterPinViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        binding.mPIN.onChange {
            if (it.length == 4) {
                login()
            }
        }
    }

    private fun login() {

        val ref = db.collection("users").document(phNum)

        ref.get()
            .addOnSuccessListener { document ->

                if (document != null) {
                    Log.d(ReqStatusFragment.TAG, "DocumentSnapshot data: ${document.data}")
                    if (document.getString(FireConst.PIN)!!.contentEquals(binding.mPIN.text.toString())) {

                        val pref =
                            activity!!.getSharedPreferences(Const.PREF_NAME, Const.PRIVATE_MODE)
                        val editor = pref.edit()
                        editor.putString(Const.PHONE_NUM, document.getString(FireConst.PHONE))
                        editor.apply()

                        activity?.let {
                            val intent = Intent(it, MainActivity::class.java)
                            it.startActivity(intent)
                            it.finish()
                        }

                    } else {
                        Toast.makeText(activity, "Password Wrong", Toast.LENGTH_LONG).show()
                    }

                } else {
                    Log.d(ReqStatusFragment.TAG, "No such document")
                }
            }.addOnFailureListener { exception ->
                Log.d(ReqStatusFragment.TAG, "get failed with ", exception)
            }
    }
}

