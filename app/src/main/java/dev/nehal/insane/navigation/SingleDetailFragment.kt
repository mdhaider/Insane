package dev.nehal.insane.navigation

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.downloadservice.filedownloadservice.manager.FileDownloadManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.databinding.SingleDetailFragmentBinding
import dev.nehal.insane.model.AlarmDTO
import dev.nehal.insane.model.ContentDTO
import dev.nehal.insane.shared.ShareImage
import dev.nehal.insane.shared.TimeAgo
import dev.nehal.insane.util.FcmPush
import okhttp3.OkHttpClient
import java.io.File


class SingleDetailFragment : DialogFragment() {
    var firestore: FirebaseFirestore? = null
    var okHttpClient: OkHttpClient? = null
    var fcmPush: FcmPush? = null
    private lateinit var binding: SingleDetailFragmentBinding
    var user: FirebaseUser? = null
    private lateinit var newContentDTO: ContentDTO
    private var contentUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            android.R.style.Theme_Material_Light_NoActionBar_Fullscreen
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val bundle = arguments
        newContentDTO = bundle!!.getSerializable("CONTENT_DTO") as ContentDTO
        contentUid = bundle.getString("CONTENT_UID")

        binding =
            DataBindingUtil.inflate(
                inflater,
                dev.nehal.insane.R.layout.single_detail_fragment,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = FirebaseAuth.getInstance().currentUser
        firestore = FirebaseFirestore.getInstance()
        okHttpClient = OkHttpClient()
        fcmPush = FcmPush()

        setValue(newContentDTO)
        setProfileImage(newContentDTO)
    }


    private fun setValue(item: ContentDTO) {

        binding.tvProfName.text = item.userName

        Glide.with(binding.imgPost.context)
            .load(item.imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter()
            .into(binding.imgPost)

        binding.imgfav.setOnClickListener { favoriteEvent(contentUid.toString()) }

        binding.crossImg.setOnClickListener { dismiss() }


        if (item.favorites.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {
            binding.imgfav.setImageResource(dev.nehal.insane.R.drawable.ic_favorite_black_24dp)

        } else {

            binding.imgfav.setImageResource(dev.nehal.insane.R.drawable.ic_favorite_border_black_24dp)
        }

        binding.imgComment.setOnClickListener {
            val intent = Intent(activity, CommentActivity::class.java)
            intent.putExtra("contentUid", contentUid)
            intent.putExtra("destinationUid", item.uid)
            startActivity(intent)
        }

        binding.tvAgo.text = TimeAgo.getTimeAgo(item.timestamp!!)

        binding.tvCaption.text = item.explain

        binding.tvLikes.text =
            getString(dev.nehal.insane.R.string.likes_count, item.favoriteCount.toString())

        binding.share.setOnClickListener {
            ShareImage.shareImageWith(activity!!, binding.imgPost.drawable)
        }

        binding.downalod.setOnClickListener {
           downloadImage(item.imageUrl!!, item.userName!!,item.timestamp.toString())

        }
    }

    private fun downloadImage(url:String, username_: String, time: String){
        val folder = File(Environment.getExternalStorageDirectory().toString() + "/" + "Insane")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val fileName = "$username_$time.jpg"

        Toast.makeText(activity!!,"Download started, Check Downloads under Home page", Toast.LENGTH_LONG).show()

        FileDownloadManager.initDownload(
            activity!!,
            url,
            folder.absolutePath,
            fileName
        )
    }

    private fun setProfileImage(item: ContentDTO) {
        firestore?.collection("profileImages")?.document(item.uid!!)
            ?.get()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val url = task.result!!["image"]
                    Glide.with(binding.imgProf.context)
                        .load(url)
                        .error(dev.nehal.insane.R.drawable.ic_account)
                        .placeholder(dev.nehal.insane.R.drawable.ic_account)
                        .apply(RequestOptions().circleCrop())
                        .into(binding.imgProf)
                }
            }
    }

    private fun favoriteAlarm(destinationUid: String) {
        val alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = user!!.phoneNumber
        alarmDTO.contentId = contentUid
        alarmDTO.uid = user?.uid
        alarmDTO.kind = 0
        alarmDTO.username = user?.displayName
        alarmDTO.timestamp = System.currentTimeMillis()
        alarmDTO.imageUri = newContentDTO.imageUrl

        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)
        val message = user?.displayName + " " + getString(dev.nehal.insane.R.string.alarm_favorite)
        fcmPush?.sendMessage(destinationUid, "You have received a message", message)
    }


    private fun favoriteEvent(contenUid: String) {
        val tsDoc = firestore!!.collection("images")?.document(contenUid)
        firestore?.runTransaction { transaction ->

            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

            if (contentDTO!!.favorites.containsKey(uid)) {
                contentDTO?.favoriteCount = contentDTO?.favoriteCount!! - 1
                contentDTO?.favorites.remove(uid)
                binding.imgfav.setImageResource(dev.nehal.insane.R.drawable.ic_favorite_border_black_24dp)
            } else {
                // Star the post and add self to stars
                contentDTO?.favoriteCount = contentDTO?.favoriteCount!! + 1
                contentDTO?.favorites[uid] = true
                favoriteAlarm(newContentDTO.uid!!)
                binding.imgfav.setImageResource(dev.nehal.insane.R.drawable.ic_favorite_black_24dp)

            }
            transaction.set(tsDoc, contentDTO)
        }
    }

}
