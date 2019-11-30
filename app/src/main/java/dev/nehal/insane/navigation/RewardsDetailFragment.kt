package dev.nehal.insane.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import dev.nehal.insane.R
import dev.nehal.insane.databinding.RewardsDetailFragmentBinding


class RewardsDetailFragment : DialogFragment() {
    private lateinit var binding: RewardsDetailFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
           android.R.style.Theme_Material_NoActionBar_TranslucentDecor
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.rewards_detail_fragment,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.scratchCard?.setOnScratchListener { scratchCard, visiblePercent ->
            if (visiblePercent > 0.3) {
                scratchCard.visibility=View.GONE
                Toast.makeText(scratchCard.context, "Content Visible", Toast.LENGTH_SHORT).show();
            }
        }

        binding.crossImg.setOnClickListener { dismiss() }

    }


}