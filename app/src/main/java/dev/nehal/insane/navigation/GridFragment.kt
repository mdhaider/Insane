package dev.nehal.insane.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import dev.nehal.insane.R
import dev.nehal.insane.model.ContentDTO
import kotlinx.android.synthetic.main.fragment_grid.view.*


class GridFragment : Fragment() {

    var mainView: View? = null
    var imagesSnapshot  : ListenerRegistration? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainView = inflater.inflate(R.layout.fragment_grid, container, false)

        return mainView
    }

    override fun onResume() {
        super.onResume()
        mainView?.gridfragment_recyclerview?.adapter = GridFragmentRecyclerViewAdatper()
        mainView?.gridfragment_recyclerview?.layoutManager = GridLayoutManager(activity, 3)
    }

    override fun onStop() {
        super.onStop()
        imagesSnapshot?.remove()
    }


    inner class GridFragmentRecyclerViewAdatper : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var contentDTOs: ArrayList<ContentDTO>
       var contentUID: ArrayList<String>

        init {
            contentDTOs = ArrayList()
            contentUID= ArrayList()
            imagesSnapshot = FirebaseFirestore
                    .getInstance().collection("images").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        contentDTOs.clear()
                        if (querySnapshot == null) return@addSnapshotListener
                        for (snapshot in querySnapshot!!.documents) {
                            contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                            contentUID.add(snapshot.id)
                        }
                        notifyDataSetChanged()
                    }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            //현재 사이즈 뷰 화면 크기의 가로 크기의 1/3값을 가지고 오기
            val width = resources.displayMetrics.widthPixels / 3

            val imageView = ImageView(parent.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width, width)

            return CustomViewHolder(imageView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            var imageView = (holder as CustomViewHolder).imageView

            Glide.with(holder.itemView.context)
                    .load(contentDTOs[position].imageUrl)
                    .apply(RequestOptions().centerCrop())
                    .into(imageView)

            imageView.setOnClickListener {
                val fragment = SingleDetailFragment()
                val bundle = Bundle()

                val obj = contentDTOs[position]
                bundle.putSerializable("CONTENT_DTO", obj)
                bundle.putString("CONTENT_UID",contentUID[position])

                fragment.arguments = bundle
                activity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.main_content, fragment)
                        .commit()
            }
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)
    }
}