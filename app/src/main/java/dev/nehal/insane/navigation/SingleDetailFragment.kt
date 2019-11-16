package dev.nehal.insane.navigation

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.R
import dev.nehal.insane.databinding.SingleDetailFragmentBinding
import dev.nehal.insane.model.AlarmDTO
import dev.nehal.insane.model.ContentDTO
import dev.nehal.insane.model.Users
import dev.nehal.insane.shared.Const
import dev.nehal.insane.shared.ModelPreferences
import dev.nehal.insane.shared.ShareImage
import dev.nehal.insane.shared.TimeAgo
import dev.nehal.insane.util.FcmPush
import okhttp3.OkHttpClient
import java.io.File


class SingleDetailFragment : DialogFragment() {
    private var db: FirebaseFirestore? = null
    var okHttpClient: OkHttpClient? = null
    var fcmPush: FcmPush? = null
    private lateinit var binding: SingleDetailFragmentBinding
    private lateinit var newContentDTO: ContentDTO
    private var contentUid: String? = null
    private lateinit var user:Users

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
        db = FirebaseFirestore.getInstance()
        okHttpClient = OkHttpClient()
        fcmPush = FcmPush()

         user = ModelPreferences(activity!!.application).getObject(Const.PROF_USER, Users::class.java)!!

        setValue(newContentDTO)
        setProfile(newContentDTO)
        getCommCount()
    }


    private fun setValue(item: ContentDTO) {
        Glide.with(binding.imgPost.context)
            .load(item.imgUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter()
            .into(binding.imgPost)

        binding.imgfav.setOnClickListener { favoriteEvent(contentUid!!) }

        binding.crossImg.setOnClickListener { dismiss() }


        if (item.favorites.containsKey(user.userUID)) {
            binding.imgfav.setImageResource(R.drawable.ic_favorite_black_24dp)

        } else {

            binding.imgfav.setImageResource(R.drawable.ic_favorite_border_black_24dp)
        }

        binding.imgComment.setOnClickListener {
            val intent = Intent(activity, CommentActivity::class.java)
            intent.putExtra("contentUid", contentUid)
            intent.putExtra("destinationUid", item.uid)
            intent.putExtra("imageUri", item.imgUrl)
            startActivity(intent)
        }

        binding.tvComments.setOnClickListener {
            val intent = Intent(activity, CommentActivity::class.java)
            intent.putExtra("contentUid", contentUid)
            intent.putExtra("destinationUid", item.uid)
            intent.putExtra("imageUri", item.imgUrl)
            startActivity(intent)
        }

        binding.tvLikes?.setOnClickListener { goToLikes(newContentDTO.favorites.keys.toList()) }


        binding.tvAgo.text = TimeAgo.getTimeAgo(item.imgUploadDate!!)

        binding.tvCaption.text = item.imgCaption

        binding.tvLikes.text =
            getString(R.string.likes_count, item.favoriteCount.toString())

        binding.share.setOnClickListener {
            ShareImage.shareImageWith(activity!!, binding.imgPost.drawable)
        }

        binding.downalod.setOnClickListener {
            downloadImage(item.imgUrl!!, item.imgUploadDate.toString())

        }
    }

    private fun downloadImage(url: String,time: String) {
        val folder = File(Environment.getExternalStorageDirectory().toString() + "/" + "Insane")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val fileName = "$time.jpg"

        Toast.makeText(
            activity!!,
            "Download started, Check Downloads under Home page",
            Toast.LENGTH_LONG
        ).show()

        FileDownloadManager.initDownload(
            activity!!,
            url,
            folder.absolutePath,
            fileName
        )
    }


    private fun setProfile(item: ContentDTO) {
        val dbRef = db!!.collection("users").document(item.uid!!)

        dbRef.get()
            .addOnSuccessListener { document ->
                val users = document.toObject(Users::class.java)
                if (users != null) {
                    binding.tvProfName.text = users.userName
                    Glide.with(binding.imgProf.context)
                        .load(users.profImageUri)
                        .error(R.drawable.ic_account)
                        .placeholder(R.drawable.ic_account)
                        .apply(RequestOptions().circleCrop())
                        .into(binding.imgProf)
                }

            }.addOnFailureListener { exception ->

                Log.d("ProfileFragment", exception.toString())
            }
    }

    private fun favoriteAlarm(destinationUid: String) {
        val alarmDTO = AlarmDTO()
        alarmDTO.contentUid = contentUid
        alarmDTO.destinationUid = destinationUid
        alarmDTO.uid = user.userUID
        alarmDTO.imageUri = newContentDTO.imgUrl
        alarmDTO.kind = 0
        alarmDTO.activityDate = System.currentTimeMillis()

        FirebaseFirestore.getInstance().collection("userActivities").document().set(alarmDTO)
        val message = user.userName + " " + getString(dev.nehal.insane.R.string.alarm_favorite)
        fcmPush?.sendMessage(destinationUid, "You have received a message", message)
    }


    private fun favoriteEvent(contenUid: String) {
        val tsDoc = db!!.collection("uploadedImages").document(contenUid)
        db?.runTransaction { transaction ->

            val uid:String = FirebaseAuth.getInstance().currentUser!!.uid
            val contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

            if (contentDTO!!.favorites.containsKey(uid)) {
                contentDTO.favoriteCount = contentDTO.favoriteCount - 1
                contentDTO.favorites.remove(uid)
                binding.imgfav.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                binding.tvLikes.text =
                    getString(
                        dev.nehal.insane.R.string.likes_count,
                        contentDTO.favoriteCount.toString()
                    )

            } else {
                // Star the post and add self to stars
                contentDTO.favoriteCount = contentDTO.favoriteCount + 1
                contentDTO.favorites[uid] = true
                favoriteAlarm(newContentDTO.uid!!)
                binding.imgfav.setImageResource(R.drawable.ic_favorite_black_24dp)

                binding.tvLikes.text =
                    getString(
                        dev.nehal.insane.R.string.likes_count,
                        contentDTO.favoriteCount.toString()
                    )

            }
            transaction.set(tsDoc, contentDTO)
        }
    }

    private fun getCommCount() {
        FirebaseFirestore.getInstance()
            .collection("uploadedImages").document(contentUid!!).collection("comments").get()
            .addOnSuccessListener { result ->

                Log.d("rtg", result.size().toString())
                binding.tvComments.text =
                    getString(R.string.comments_count, result.size().toString())

            }
            .addOnFailureListener { exception ->
                Log.d("DetailFrag", "Error getting documents: ", exception)

            }
    }

    private fun goToLikes(uidList: List<String>) {
        val arr: Array<String> = uidList.toTypedArray()
        val intent = Intent(activity, LikesActivity::class.java)
        val b = Bundle()
        b.putStringArray("uidlist", arr)
        intent.putExtras(b)
        startActivity(intent)
    }
}
