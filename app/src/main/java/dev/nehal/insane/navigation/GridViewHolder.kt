package dev.nehal.insane.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import dev.nehal.insane.R
import dev.nehal.insane.model.ContentDTO


class GridViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_grid, parent, false)) {
    private var mItemImage: ImageView? = null

    init {
        mItemImage = itemView.findViewById(R.id.itemImage)
    }

    fun bind(contentDTO: ContentDTO, itemClickListener:(Int)->Unit) {

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))

        Glide.with(mItemImage!!.context)
            .load(contentDTO.imageUrl)
            .apply(requestOptions)
            .error(R.drawable.ic_broken_image_black_24dp)
            .placeholder(R.drawable.ic_broken_image_black_24dp)
            .into(mItemImage!!)

        itemView.setOnClickListener { itemClickListener(adapterPosition) }

    }
}
