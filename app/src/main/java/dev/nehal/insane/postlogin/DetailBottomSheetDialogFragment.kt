package dev.nehal.insane.postlogin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.nehal.insane.R
import dev.nehal.insane.databinding.BottomSheetDetailBinding

class DetailBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var mBottomSheetListener: BottomSheetListener? = null
    private lateinit var binding: BottomSheetDetailBinding

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater, @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_detail, container, false)
        binding.tvDetBotClose.setOnClickListener {
            mBottomSheetListener!!.onOptionClick("close")
            dismiss() //dismiss bottom sheet when item click
        }
        binding.tvDetBotShare.setOnClickListener {
            mBottomSheetListener!!.onOptionClick("share")
            dismiss() //dismiss bottom sheet when item click

        }

        binding.tvDetBotDownload.setOnClickListener {
            mBottomSheetListener!!.onOptionClick("download")
            dismiss() //dismiss bottom sheet when item click

        }


        return binding.root
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            mBottomSheetListener = context as BottomSheetListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString())
        }

    }

    override fun onDetach() {
        super.onDetach()
    }


    interface BottomSheetListener {
        fun onOptionClick(text: String)
    }

    companion object {

        val TAG = "DetailBottomSheetDialogFragment"

        fun newInstance(): DetailBottomSheetDialogFragment {
            return DetailBottomSheetDialogFragment()
        }
    }
}