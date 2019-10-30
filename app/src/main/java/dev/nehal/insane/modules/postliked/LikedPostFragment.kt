package dev.nehal.insane.modules.postliked

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import dev.nehal.insane.R

class LikedPostFragment : Fragment() {

    companion object {
        fun newInstance() = LikedPostFragment()
    }

    private lateinit var viewModel: LikedPostViewModel


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.title = "Favorite Posts"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.liked_post_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LikedPostViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
