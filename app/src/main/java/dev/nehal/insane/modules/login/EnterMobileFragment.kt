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

import dev.nehal.insane.R
import dev.nehal.insane.databinding.EnterMobileFragmentBinding
import dev.nehal.insane.shared.onChange

class EnterMobileFragment : Fragment() {

    private lateinit var binding: EnterMobileFragmentBinding

    companion object {
        fun newInstance() = EnterMobileFragment()
        const val TAG= "EnterMobileFragment"
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

        binding.mNumber.onChange {
            if (it.length == 10) checkUserStatus(it)
        }
    }

    private fun checkUserStatus(phNumb: String) {
        Log.d(TAG, "checking status $phNumb")

        val bundle = Bundle().apply {
            putString(SignUpReqFragment.KEY_MOBILE, phNumb)

        }

        findNavController().navigate(R.id.action_entermobile_signupreq, bundle)
    }


}
