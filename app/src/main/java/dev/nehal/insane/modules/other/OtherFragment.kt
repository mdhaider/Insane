package dev.nehal.insane.modules.other

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import dev.nehal.insane.R

class OtherFragment : Fragment() {

    private lateinit var otherViewModel: OtherViewModel
    private lateinit var textView:TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.title = "Other"
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        otherViewModel =
            ViewModelProviders.of(this).get(OtherViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_other, container, false)
         textView = root.findViewById(R.id.text_notifications)
        otherViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView.setOnClickListener{
           goToUserList()
        }
    }

    private fun goToUserList() {
        findNavController().navigate(R.id.action_other_user, null)
    }
}