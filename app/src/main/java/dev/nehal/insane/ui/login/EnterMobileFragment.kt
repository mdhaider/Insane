package dev.nehal.insane.ui.login

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import dev.nehal.insane.R
import dev.nehal.insane.databinding.EnterMobileFragmentBinding

class EnterMobileFragment : Fragment() {

    private lateinit var binding: EnterMobileFragmentBinding

    companion object {
        fun newInstance() = EnterMobileFragment()
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

}
