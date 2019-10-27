package dev.nehal.insane.modules.login

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import dev.nehal.insane.R
import dev.nehal.insane.databinding.ReqStatusFragmentBinding

class ReqStatusFragment : Fragment() {

    private lateinit var binding: ReqStatusFragmentBinding
    private lateinit var phNum:String
    private lateinit var mName:String
    companion object {
        fun newInstance() = ReqStatusFragment()
        const val KEY_NUMBER="number"
        const val KEY_NAME="name"
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
    }
}
