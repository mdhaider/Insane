package dev.nehal.insane.navigation

import `in`.myinnos.androidscratchcard.ScratchCard
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.R
import dev.nehal.insane.databinding.RewardsDetailFragmentBinding
import dev.nehal.insane.model.Rewards


class RewardsDetailFragment : DialogFragment() {
    private lateinit var binding: RewardsDetailFragmentBinding
    private lateinit var rewards: Rewards
    private lateinit var listener: InterfaceComm
    private var isOpened:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.D1NoTitleDim
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val bundle = arguments
        rewards = bundle!!.getSerializable("REWARDS") as Rewards

        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.rewards_detail_fragment,
                container,
                false
            )

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as InterfaceComm

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (rewards.isRevealed) {
            setDataValue()
        }
        binding.scratchCard.setOnScratchListener { scratchCard, visiblePercent ->
            if (visiblePercent > 0.3) {
                binding.rvProgress.visibility = View.VISIBLE
                setData(scratchCard)
                isOpened=true
            }
        }

        binding.crossImg.setOnClickListener {
            if(isOpened){
                listener.closeBtn(isOpened)
                dismiss()
            } else{
                dismiss()
            }
        }

    }

    private fun setData(scratchCard: ScratchCard) {

        val db = FirebaseFirestore.getInstance()

        try {

            db.collection("rewards").document(rewards.docId)
                .update("revealDate", System.currentTimeMillis())
                .addOnSuccessListener { documentReference ->
                    setData2(scratchCard)

                }.addOnFailureListener { e ->
                    binding.rvProgress.visibility = View.GONE

                }
        } catch (e: Exception) {
            binding.rvProgress.visibility = View.GONE
        }
    }

    private fun setData2(scratchCard: ScratchCard) {

        val db = FirebaseFirestore.getInstance()

        try {

            db.collection("rewards").document(rewards.docId)
                .update("revealed", true)
                .addOnSuccessListener { documentReference ->
                    Log.d("RewardsFragment", "DocumentSnapshot added with ID: $documentReference")
                    setDataValue()

                    Toast.makeText(
                        scratchCard.context,
                        "You earned" + " " + rewards.coinValue + " " + "coins",
                        Toast.LENGTH_SHORT
                    ).show()

                }.addOnFailureListener { e ->
                    binding.rvProgress.visibility = View.GONE

                }
        } catch (e: Exception) {
            binding.rvProgress.visibility = View.GONE
        }
    }

    private fun setDataValue() {
        binding.scratchCard.visibility = View.GONE
        binding.nonCardView.visibility = View.VISIBLE
        binding.rvProgress.visibility = View.GONE
        binding.coinText.text = rewards.coinValue.toString()
    }

    interface InterfaceComm {
        fun closeBtn(isRevealed: Boolean)
    }

}