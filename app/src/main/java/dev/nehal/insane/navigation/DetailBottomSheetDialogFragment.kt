package dev.nehal.insane.navigation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Nullable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.nehal.insane.R
import kotlinx.android.synthetic.main.bottom_sheet_detail.*

class DetailBottomSheetDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private var mListener: ItemClickListener? = null

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater, @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_detail, container, false)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvDetBotClose.setOnClickListener(this)
        tvDetBotShare.setOnClickListener(this)
        tvDetBotDownload.setOnClickListener(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ItemClickListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement ItemClickListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }


    override fun onClick(view: View) {
        val tvSelected = view as TextView
        mListener!!.onItemClick(tvSelected.getText().toString())
        dismiss()
    }

    interface ItemClickListener {
        fun onItemClick(item: String)
    }

    companion object {

        val TAG = "DetailBottomSheetDialogFragment"

        fun newInstance(): DetailBottomSheetDialogFragment {
            return DetailBottomSheetDialogFragment()
        }
    }
}