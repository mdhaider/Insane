package dev.nehal.insane.navigation


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import dev.nehal.insane.R
import dev.nehal.insane.databinding.FragmentDetailBinding
import dev.nehal.insane.model.AlarmDTO
import dev.nehal.insane.model.ContentDTO
import dev.nehal.insane.navigation.DetailAdapter.ItemClickListener
import dev.nehal.insane.newd.main.MainActivity1
import dev.nehal.insane.shared.Const
import dev.nehal.insane.util.FcmPush
import okhttp3.OkHttpClient
import java.util.*


class DetailFragment : Fragment() {

    var user: FirebaseUser? = null
    var firestore: FirebaseFirestore? = null
    var imagesSnapshot: ListenerRegistration? = null
    var okHttpClient: OkHttpClient? = null
    var fcmPush: FcmPush? = null
    private lateinit var adapter: DetailAdapter
    private lateinit var uidSet: MutableSet<String>
    private lateinit var contentDTO: ArrayList<ContentDTO>
    private lateinit var contentUidList: ArrayList<String>
    private lateinit var binding: FragmentDetailBinding
    private lateinit var url: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            dev.nehal.insane.R.layout.fragment_detail,
            container,
            false
        )
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        imagesSnapshot?.remove()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = FirebaseAuth.getInstance().currentUser
        firestore = FirebaseFirestore.getInstance()
        okHttpClient = OkHttpClient()
        fcmPush = FcmPush()

        val itemOnClick = object : ItemClickListener {
            override fun goToProfile(userUid: String) {
                val bundle = Bundle().apply {
                    putString(Const.USER_UID, userUid)
                }

                findNavController().navigate(R.id.action_profileimage_to_profile, bundle)
            }

            override fun getMore() {
                val mainActivity = activity as MainActivity1?
                mainActivity!!.showBottomSheet()

            }

            override fun goToDetailPost(contentId: String, contentDTO: ContentDTO) {
                val dialogFragment = SingleDetailFragment() //here MyDialog is my custom dialog
                val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
                val bundle = Bundle()

                bundle.putSerializable("CONTENT_DTO", contentDTO)
                bundle.putString("CONTENT_UID", contentId)

                dialogFragment.arguments = bundle
                dialogFragment.show(fragmentTransaction, "dialog")

            }

            override fun setfav(contentUid: String, destUid: String) {
                favoriteEvent(contentUid, destUid)
            }

            override fun goToLikes(uidList: List<String>) {

                val arr: Array<String> = uidList.toTypedArray()

                val intent = Intent(activity, LikesActivity::class.java)

                val b = Bundle()
                b.putStringArray("uidlist", arr)
                intent.putExtras(b)
                startActivity(intent)
            }

            override fun goToComments(imageUri: String, contentUid: String, userUid: String) {
                val intent = Intent(activity, CommentActivity::class.java)
                intent.putExtra("contentUid", contentUid)
                intent.putExtra("imageUri", imageUri)
                intent.putExtra("destinationUid", userUid)
                startActivity(intent)
            }
        }

        url = ""
        contentDTO = ArrayList()
        contentUidList = ArrayList()
        uidSet = mutableSetOf()
        binding.rvDet.setHasFixedSize(true)
        adapter = DetailAdapter(contentDTO, contentUidList, listener = itemOnClick)
        binding.rvDet.adapter = adapter
        binding.rvDet.layoutManager = LinearLayoutManager(activity)

        getData()
    }


    private fun getData() {
        imagesSnapshot =
            firestore?.collection("uploadedImages")
                ?.orderBy("imgUploadDate", Query.Direction.DESCENDING)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTO.clear()
                    contentUidList.clear()
                    if (querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)!!
                        Log.d("fdt", item.favorites.keys.toString())
                        uidSet.addAll(item.favorites.keys)
                        contentDTO.add(item)
                        contentUidList.add(snapshot.id)

                    }
                    adapter.notifyDataSetChanged()
                }
    }

    fun favoriteAlarm(destinationUid: String, contentUid: String, imgUrl: String) {

        val alarmDTO = AlarmDTO()
        alarmDTO.contentUid = contentUid
        alarmDTO.imageUri = imgUrl
        alarmDTO.destinationUid = destinationUid
        alarmDTO.uid = user?.uid
        alarmDTO.kind = 0
        alarmDTO.activityDate = System.currentTimeMillis()

        FirebaseFirestore.getInstance().collection("userActivities").document().set(alarmDTO)
        var message = user?.displayName + " " + getString(dev.nehal.insane.R.string.alarm_favorite)
        fcmPush?.sendMessage(destinationUid, "You have received a message", message)
    }

    private fun favoriteEvent(contentUid: String, destUid: String) {
        var tsDoc = firestore?.collection("uploadedImages")?.document(contentUid)
        firestore?.runTransaction { transaction ->

            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

            if (contentDTO!!.favorites.containsKey(uid)) {
                // Unstar the post and remove self from stars
                contentDTO.favoriteCount = contentDTO?.favoriteCount - 1
                contentDTO.favorites.remove(uid)

            } else {
                // Star the post and add self to stars
                contentDTO?.favoriteCount = contentDTO?.favoriteCount!! + 1
                contentDTO?.favorites[uid] = true
                favoriteAlarm(destUid, contentUid, contentDTO.imgUrl!!)
            }
            transaction.set(tsDoc, contentDTO)
        }
    }
}