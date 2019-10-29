package dev.nehal.insane.modules.login

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
import dev.nehal.insane.databinding.CreatePinFragmentBinding
import dev.nehal.insane.shared.Const
import dev.nehal.insane.shared.onChange

class CreatePinFragment : Fragment() {
    private lateinit var binding:CreatePinFragmentBinding
    private lateinit var db:FirebaseFirestore
    private lateinit var phNum: String

    companion object {
        fun newInstance() = CreatePinFragment()
        const val TAG="CreatePin"
    }

    private lateinit var viewModel: CreatePinViewModel

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
        binding= DataBindingUtil.inflate(inflater,R.layout.create_pin_fragment,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CreatePinViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      binding.confirmPin.onChange {
          if(it.length==4){
              createPIN(it)
          }
      }
    }

    private fun createPIN(pin:String) {
        db = FirebaseFirestore.getInstance()

        try {
            val ref = db.collection("users").document(phNum)
            ref.update("pin", pin)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
        } catch (e: Exception) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
        }
    }


}
