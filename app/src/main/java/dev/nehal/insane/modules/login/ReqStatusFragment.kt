package dev.nehal.insane.modules.login

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.FirebaseFirestore

import dev.nehal.insane.R
import dev.nehal.insane.databinding.ReqStatusFragmentBinding

class ReqStatusFragment : Fragment() {

    private lateinit var binding: ReqStatusFragmentBinding
    private lateinit var phNum:String
    private lateinit var mName:String
    private lateinit var db:FirebaseFirestore
    companion object {
        fun newInstance() = ReqStatusFragment()
        const val KEY_NUMBER="number"
        const val KEY_NAME="name"
        const val TAG="ReqStatusFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.apply {
            phNum= getString(KEY_NUMBER,"")
            mName= getString(KEY_NAME,"")

        }
    }

    private lateinit var viewModel: ReqStatusViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(inflater, R.layout.req_status_fragment, container,false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ReqStatusViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mPh.text=phNum
        binding.mName.text=mName

        db= FirebaseFirestore.getInstance()

        binding.checkStatus.setOnClickListener{
            checkStatus()
        }
    }

    private fun checkStatus(){

       val ref= db.collection("users").document(phNum)

        ref.get()
            .addOnSuccessListener { document->

                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    if(document.getBoolean("approved")!!) {
                        binding.txtStatus.text="Wow, approved."
                    } else{
                        binding.txtStatus.text="Oh Sorry, Not Approved yet."
                    }

                } else {
                    Log.d(TAG, "No such document")
                }
            }.addOnFailureListener{ exception->
                Log.d(TAG, "get failed with ", exception)
            }
    }
}
