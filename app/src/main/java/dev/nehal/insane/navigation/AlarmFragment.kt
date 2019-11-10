package dev.nehal.insane.navigation

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
import dev.nehal.insane.databinding.FragmentAlarmBinding
import dev.nehal.insane.model.AlarmDTO
import dev.nehal.insane.model.ContentDTO


class AlarmFragment : Fragment() {
    private lateinit var binding: FragmentAlarmBinding
    private lateinit var adapter: AlarmAdapter
    private lateinit var alarmList: ArrayList<AlarmDTO>
    private var alarmSnapshot: ListenerRegistration? = null
    private var contentDTO: ContentDTO? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemOnClick: (Int) -> Unit = { position ->
            getPostData(alarmList[position].contentId!!)
        }

        alarmList = ArrayList()
        adapter = AlarmAdapter(alarmList, itemClickListener = itemOnClick)
        binding.rvAlarm.adapter = adapter
        binding.rvAlarm.layoutManager = LinearLayoutManager(activity)

        getData()
    }

    private fun getData() {
        alarmSnapshot = FirebaseFirestore.getInstance()
            .collection("alarms")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                alarmList.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot.documents) {
                    alarmList.add(snapshot.toObject(AlarmDTO::class.java)!!)
                }
                alarmList.sortByDescending { it.timestamp }
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
            .getInstance().collection("images").document(contentId).get()
            .addOnSuccessListener { document ->
                contentDTO = document.toObject((ContentDTO::class.java))
                goToDetail(contentId, contentDTO!!)

            }.addOnFailureListener {
                Log.d("alarmfrag", "eror in getting data")
            }
    }
}
