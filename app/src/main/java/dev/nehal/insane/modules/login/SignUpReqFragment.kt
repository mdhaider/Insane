package dev.nehal.insane.modules.login

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController

import dev.nehal.insane.R
import dev.nehal.insane.databinding.SignUpReqFragmentBinding

class SignUpReqFragment : Fragment() {
    private lateinit var phNum:String
    private lateinit var binding:SignUpReqFragmentBinding

    companion object {
        fun newInstance() = SignUpReqFragment()
        const val KEY_MOBILE= "mobile"
    }

    private lateinit var viewModel: SignUpReqViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.apply {
            phNum = getString(KEY_MOBILE, "")

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(inflater,R.layout.sign_up_req_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SignUpReqViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnReq.setOnClickListener{
            val bundle = Bundle().apply {
                putString(ReqStatusFragment.KEY_NUMBER, phNum)
                putString(ReqStatusFragment.KEY_NAME, binding.mName.text.toString())
            }

            findNavController().navigate(R.id.action_signupreq_reqstatus, bundle)
        }
    }


}
