package dev.nehal.insane.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dev.nehal.insane.model.AlarmDTO
import dev.nehal.insane.model.ContentDTO
import dev.nehal.insane.util.FcmPush
import kotlinx.android.synthetic.main.single_detail_fragment.*
import okhttp3.OkHttpClient


class SingleDetailFragment : DialogFragment() {
    var firestore: FirebaseFirestore? = null
    var imagesSnapshot: ListenerRegistration? = null
    var okHttpClient: OkHttpClient? = null
    var fcmPush: FcmPush? = null
    var mainView: View? = null
    var user: FirebaseUser? = null
    private lateinit var newContentDTO: ContentDTO
    private var contentUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,
            android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        user = FirebaseAuth.getInstance().currentUser
        firestore = FirebaseFirestore.getInstance()
        okHttpClient = OkHttpClient()
        fcmPush = FcmPush()

        val bundle = arguments
        newContentDTO = bundle!!.getSerializable("CONTENT_DTO") as ContentDTO
        contentUid = bundle.getString("CONTENT_UID")

        //Linking Recycler View to Adapter
        mainView =
            inflater.inflate(dev.nehal.insane.R.layout.single_detail_fragment, container, false)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setValue(newContentDTO)
    }

    fun setValue(item: ContentDTO) {

        firestore?.collection("profileImages")?.document(item.uid!!)
            ?.get()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val url = task.result!!["image"]
                    Glide.with(detailviewitem_profile_image.context)
                        .load(url)
                        .error(dev.nehal.insane.R.drawable.ic_account)
                        .placeholder(dev.nehal.insane.R.drawable.ic_account)
                        .apply(RequestOptions().circleCrop())
                        .into(detailviewitem_profile_image)

                }
            }

        detailviewitem_profile_textview.text = item.userName

        // 가운데 이미지
        Glide.with(detailviewitem_imageview_content.context)
            .load(item.imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter()
            .into(detailviewitem_imageview_content)

        detailviewitem_explain_textview.text = item.explain
        // 좋아요 이벤트
        detailviewitem_favorite_imageview.setOnClickListener { favoriteEvent(contentUid.toString()) }

        //좋아요 버튼 설정
        if (item.favorites.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {
            detailviewitem_favorite_imageview.setImageResource(dev.nehal.insane.R.drawable.ic_favorite_black_24dp)

        } else {

            detailviewitem_favorite_imageview.setImageResource(dev.nehal.insane.R.drawable.ic_favorite_border_black_24dp)
        }

        detailviewitem_comment_imageview.setOnClickListener {
            val intent = Intent(activity, CommentActivity::class.java)
            intent.putExtra("contentUid", contentUid)
            intent.putExtra("destinationUid", item.uid)
            startActivity(intent)
        }
    }

    fun favoriteAlarm(destinationUid: String) {
        val alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = user!!.phoneNumber
        alarmDTO.uid = user?.uid
        alarmDTO.kind = 0
        alarmDTO.username = user?.displayName
        alarmDTO.timestamp = System.currentTimeMillis()

        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)
        var message = user?.displayName + " " + getString(dev.nehal.insane.R.string.alarm_favorite)
        fcmPush?.sendMessage(destinationUid, "You have received a message", message)
    }


    private fun favoriteEvent(contenUid: String) {
        var tsDoc = firestore!!.collection("images")?.document(contenUid)
        firestore?.runTransaction { transaction ->

            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

            if (contentDTO!!.favorites.containsKey(uid)) {
                // Unstar the post and remove self from stars
                contentDTO?.favoriteCount = contentDTO?.favoriteCount!! - 1
                contentDTO?.favorites.remove(uid)
                detailviewitem_favorite_imageview.setImageResource(dev.nehal.insane.R.drawable.ic_favorite_border_black_24dp)
                detailviewitem_favoritecounter_textview.text =
                    contentDTO?.favoriteCount.toString() + " " + "Likes"
            } else {
                // Star the post and add self to stars
                contentDTO?.favoriteCount = contentDTO?.favoriteCount!! + 1
                contentDTO?.favorites[uid] = true
                favoriteAlarm(newContentDTO.uid!!)
                detailviewitem_favoritecounter_textview.text =
                    (contentDTO?.favoriteCount).toString() + " " + "Likes"
                detailviewitem_favorite_imageview.setImageResource(dev.nehal.insane.R.drawable.ic_favorite_black_24dp)

            }
            transaction.set(tsDoc, contentDTO)
        }
    }

}
