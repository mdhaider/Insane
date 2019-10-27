package dev.nehal.insane.modules.login

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.MainActivity

import dev.nehal.insane.R
import dev.nehal.insane.databinding.EnterPinFragmentBinding
import dev.nehal.insane.shared.*


class EnterPinFragment : Fragment() {
    private lateinit var binding: EnterPinFragmentBinding
    private lateinit var db:FirebaseFirestore
    private lateinit var phNum:String

    companion object {
        fun newInstance() = EnterPinFragment()
    }

    private lateinit var viewModel: EnterPinViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        arguments?.apply {
            phNum = getString(SignUpReqFragment.KEY_MOBILE, "")

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.enter_pin_fragment,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EnterPinViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db= FirebaseFirestore.getInstance()


        binding.mPIN.onChange {
            if(it.length==4){
                login()
            }
        }
    }

    private fun login(){

        val ref= db.collection("users").document(phNum)

        ref.get()
            .addOnSuccessListener { document->

                if (document != null) {
                    Log.d(ReqStatusFragment.TAG, "DocumentSnapshot data: ${document.data}")
                    if(document.getString("pin")!!.contentEquals(binding.mPIN.text.toString())) {

                        activity?.let{
                            val intent = Intent (it, MainActivity::class.java)
                            it.startActivity(intent)
                            it.finish()

                        }

                    } else{
                       Toast.makeText(activity,"Passwword Wrong", Toast.LENGTH_LONG).show()
                    }

                } else {
                    Log.d(ReqStatusFragment.TAG, "No such document")
                }
            }.addOnFailureListener{ exception->
                Log.d(ReqStatusFragment.TAG, "get failed with ", exception)
            }
    }
}

