package dev.nehal.insane.postlogin

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dev.nehal.insane.R
import dev.nehal.insane.databinding.FragmentAlarmProfBinding
import dev.nehal.insane.model.AlarmDTO
import dev.nehal.insane.model.ContentDTO
import dev.nehal.insane.navigation.SingleDetailFragment
import dev.nehal.insane.shared.Const


class ProfAlarmFragment : Fragment() {
    private lateinit var binding: FragmentAlarmProfBinding
    private lateinit var adapter: ProfAlarmAdapter
    private lateinit var alarmList: ArrayList<AlarmDTO>
    private var alarmSnapshot: ListenerRegistration? = null
    private var contentDTO: ContentDTO? = null
    private var userUid:String?=null

    companion object {

        @JvmStatic
        fun newInstance(userid: String) = ProfAlarmFragment().apply {
            arguments = Bundle().apply {
                putString(Const.USER_UID, userid)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        arguments?.getString(Const.USER_UID)?.let {
            userUid = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm_prof, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemOnClick: (Int) -> Unit = { position ->
            getPostData(alarmList[position].contentUid!!)
        }

        alarmList = ArrayList()
        adapter = ProfAlarmAdapter(alarmList, itemClickListener = itemOnClick)
        binding.rvAlarm.adapter = adapter
        binding.rvAlarm.layoutManager = LinearLayoutManager(activity)

        getData()
    }

    private fun getData() {
        alarmSnapshot = FirebaseFirestore.getInstance()
            .collection("userActivities").whereEqualTo("uid",userUid)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                alarmList.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot.documents) {
                    alarmList.add(snapshot.toObject(AlarmDTO::class.java)!!)
                }
                alarmList.sortByDescending { it.activityDate }
                adapter.notifyDataSetChanged()
            }
    }

    override fun onStop() {
        super.onStop()
        alarmSnapshot?.remove()
    }

    private fun goToDetail(contentId: String,contentDTO: ContentDTO) {
        val dialogFragment = SingleDetailFragment() //here MyDialog is my custom dialog
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        val bundle = Bundle()

        bundle.putSerializable("CONTENT_DTO", contentDTO)
        bundle.putString("CONTENT_UID", contentId)

        dialogFragment.arguments = bundle
        dialogFragment.show(fragmentTransaction, "dialog")
    }

    private fun getPostData(contentId: String) {
        FirebaseFirestore
            .getInstance().collection("uploadedImages").document(contentId).get()
            .addOnSuccessListener { document ->
                contentDTO = document.toObject((ContentDTO::class.java))
                goToDetail(contentId, contentDTO!!)

            }.addOnFailureListener {
                Log.d("alarmfrag", "eror in getting data")
            }
    }
}
