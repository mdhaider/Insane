package dev.nehal.insane.modules.allpost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import dev.nehal.insane.R

class AllPostFragment : Fragment() {

    companion object {
        fun newInstance() = AllPostFragment()
    }

    private lateinit var viewModel: AllPostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.all_post_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AllPostViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
