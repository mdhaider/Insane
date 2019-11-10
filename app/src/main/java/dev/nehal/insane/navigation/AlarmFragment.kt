package dev.nehal.insane.navigation

import android.os.Bundle
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


class AlarmFragment : Fragment() {

    private lateinit var binding: FragmentAlarmBinding
    private lateinit var adapter: AlarmAdapter
    private lateinit var alarmList: ArrayList<AlarmDTO>
    var alarmSnapshot: ListenerRegistration? = null

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

        alarmList = ArrayList()
        adapter = AlarmAdapter(alarmList)
        binding.rvAlarm.adapter = adapter
        binding.rvAlarm.layoutManager = LinearLayoutManager(activity)

        getData()
    }

    private fun getData() {
        alarmSnapshot=FirebaseFirestore.getInstance()
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
}