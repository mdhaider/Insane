package dev.nehal.insane.navigation


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import dev.nehal.insane.R
import dev.nehal.insane.model.AlarmDTO
import dev.nehal.insane.model.ContentDTO
import dev.nehal.insane.model.FollowDTO
import dev.nehal.insane.modules.MainActivity
import dev.nehal.insane.util.FcmPush
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*
import okhttp3.OkHttpClient
import java.util.*


class DetailViewFragment : Fragment() {

    var user: FirebaseUser? = null
    var firestore: FirebaseFirestore? = null
    var imagesSnapshot: ListenerRegistration? = null
    var okHttpClient: OkHttpClient? = null
    var fcmPush: FcmPush? = null
    var mainView: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        user = FirebaseAuth.getInstance().currentUser
        firestore = FirebaseFirestore.getInstance()
        okHttpClient = OkHttpClient()
        fcmPush = FcmPush()

        //Linking Recycler View to Adapter
        mainView = inflater.inflate(R.layout.fragment_detail, container, false)


        return mainView
    }

    override fun onResume() {
        super.onResume()
        mainView?.detailviewfragment_recyclerview?.layoutManager = LinearLayoutManager(activity)
        mainView?.detailviewfragment_recyclerview?.adapter = DetailRecyclerViewAdapter()
        var mainActivity = activity as MainActivity
        mainActivity.progress_bar.visibility = View.INVISIBLE

    }

    override fun onStop() {
        super.onStop()
        imagesSnapshot?.remove()
    }

    inner class DetailRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val contentDTOs: ArrayList<ContentDTO>
        val contentUidList: ArrayList<String>

        init {
            contentDTOs = ArrayList()
            contentUidList = ArrayList()
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            firestore?.collection("users")?.document(uid!!)?.get()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var userDTO = task.result!!.toObject(FollowDTO::class.java)
                    if (userDTO?.followings != null) {
                        getCotents(userDTO?.followings, uid)
                    }
                }
            }
        }

        fun getCotents(followers: MutableMap<String, Boolean>?, uid:String) {
            imagesSnapshot = firestore?.collection("images")?.orderBy("timestamp", Query.Direction.DESCENDING)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    contentUidList.clear()
                    if (querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)!!
                        println(item.uid)
                        if (followers?.keys?.contains(item.uid)!! || (uid==item.uid)) {
                            contentDTOs.add(item)
                            contentUidList.add(snapshot.id)
                        }
                    }
                    notifyDataSetChanged()
                }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
            return CustomViewHolder(view)

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val viewHolder = (holder as CustomViewHolder).itemView

            // Profile Image 가져오기
            firestore?.collection("profileImages")?.document(contentDTOs[position].uid!!)
                ?.get()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val url = task.result!!["image"]
                        Glide.with(holder.itemView.context)
                            .load(url)
                            .error(R.drawable.ic_account)
                            .placeholder(R.drawable.ic_account)
                            .apply(RequestOptions().circleCrop())
                            .into(viewHolder.detailviewitem_profile_image)

                    }
                }

            //UserFragment로 이동
            viewHolder.detailviewitem_profile_image.setOnClickListener {

                val fragment = UserFragment()
                val bundle = Bundle()

                bundle.putString("destinationUid", contentDTOs[position].uid)
                bundle.putString("userId", contentDTOs[position].userId)
                bundle.putString("userName", contentDTOs[position].userName)

                fragment.arguments = bundle
                activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit()
            }

            // 유저 아이디
            viewHolder.detailviewitem_profile_textview.text = contentDTOs[position].userName

            // 가운데 이미지
            Glide.with(holder.itemView.context)
                .load(contentDTOs[position].imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(viewHolder.detailviewitem_imageview_content)

            // 설명 텍스트
            viewHolder.detailviewitem_explain_textview.text = contentDTOs[position].explain
            // 좋아요 이벤트
            viewHolder.detailviewitem_favorite_imageview.setOnClickListener { favoriteEvent(position) }

            //좋아요 버튼 설정
            if (contentDTOs[position].favorites.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {
                viewHolder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite_black_24dp)

            } else {

                viewHolder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite_border_black_24dp)
            }
            //Like counter settings
            viewHolder.detailviewitem_favoritecounter_textview.text =
                contentDTOs[position].favoriteCount.toString()+" "+"Likes"

            viewHolder.detailviewitem_comment_imageview.setOnClickListener {
                val intent = Intent(activity, CommentActivity::class.java)
                intent.putExtra("contentUid", contentUidList[position])
                intent.putExtra("destinationUid", contentDTOs[position].uid)
                startActivity(intent)
            }

        }

        fun favoriteAlarm(destinationUid: String) {

            val alarmDTO = AlarmDTO()
            alarmDTO.destinationUid = destinationUid
            alarmDTO.userId = user?.phoneNumber
            alarmDTO.uid = user?.uid
            alarmDTO.kind = 0
            alarmDTO.username = user?.displayName
            alarmDTO.timestamp = System.currentTimeMillis()

            FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)
            var message = user?.displayName+" "+ getString(R.string.alarm_favorite)
            fcmPush?.sendMessage(destinationUid, "You have received a message", message)
        }

        override fun getItemCount(): Int {

            return contentDTOs.size

        }

        //좋아요 이벤트 기능
        private fun favoriteEvent(position: Int) {
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction { transaction ->

                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                val contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if (contentDTO!!.favorites.containsKey(uid)) {
                    // Unstar the post and remove self from stars
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount!! - 1
                    contentDTO?.favorites.remove(uid)

                } else {
                    // Star the post and add self to stars
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount!! + 1
                    contentDTO?.favorites[uid] = true
                    favoriteAlarm(contentDTOs[position].uid!!)
                }
                transaction.set(tsDoc, contentDTO)
            }
        }
    }

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
