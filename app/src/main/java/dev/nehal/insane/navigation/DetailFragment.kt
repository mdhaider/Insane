package dev.nehal.insane.navigation


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import dev.nehal.insane.databinding.FragmentDetailBinding
import dev.nehal.insane.model.AlarmDTO
import dev.nehal.insane.model.ContentDTO
import dev.nehal.insane.navigation.DetailAdapter.ItemClickListener
import dev.nehal.insane.newd.main.MainActivity1
import dev.nehal.insane.util.FcmPush
import okhttp3.OkHttpClient
import java.util.*


class DetailFragment : Fragment(), DetailBottomSheetDialogFragment.ItemClickListener {
    override fun onItemClick(item: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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
            override fun goToprofile() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getMore() {
                val mainActivity = activity as MainActivity1?
                mainActivity!!.showBottomSheet()

            }

            override fun goToDetailPost() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun setfav() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun goToComment(contentUid: String, userUid: String) {
                val intent = Intent(activity, CommentActivity::class.java)
                intent.putExtra("contentUid", contentUid)
                intent.putExtra("destinationUid", userUid)
                startActivity(intent)
            }

            override fun goToLikes(uidList: List<String>) {

               val arr: Array<String> =uidList.toTypedArray()

                val intent = Intent(activity, LikesActivity::class.java)

                val b = Bundle()
                b.putStringArray("uidlist", arr)
                intent.putExtras(b)
                startActivity(intent)
            }

            override fun goToComments(contentUid: String, userUid: String) {
                val intent = Intent(activity, CommentActivity::class.java)
                intent.putExtra("contentUid", contentUid)
                intent.putExtra("destinationUid", userUid)
                startActivity(intent)
            }

        }

        url = ""
        contentDTO = ArrayList()
        contentUidList = ArrayList()
        uidSet = mutableSetOf()
        binding.rvDet.setHasFixedSize(true)
        adapter = DetailAdapter(contentDTO, contentUidList, uidSet, listener = itemOnClick)
        binding.rvDet.adapter = adapter
        binding.rvDet.layoutManager = LinearLayoutManager(activity)

        getData()
    }


    private fun getData() {
        imagesSnapshot =
            firestore?.collection("images")?.orderBy("timestamp", Query.Direction.DESCENDING)
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

    fun favoriteAlarm(destinationUid: String) {

        val alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = user?.phoneNumber
        alarmDTO.uid = user?.uid
        alarmDTO.kind = 0
        alarmDTO.username = user?.displayName
        alarmDTO.timestamp = System.currentTimeMillis()

        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)
        var message = user?.displayName + " " + getString(dev.nehal.insane.R.string.alarm_favorite)
        fcmPush?.sendMessage(destinationUid, "You have received a message", message)
    }

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
                // favoriteAlarm(contentDTO[position].uid!!)
            }
            transaction.set(tsDoc, contentDTO)
        }
    }

}