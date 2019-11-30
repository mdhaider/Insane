package dev.nehal.insane.postlogin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import dev.nehal.insane.R
import dev.nehal.insane.databinding.FragmentGridProfBinding
import dev.nehal.insane.model.ContentDTO
import dev.nehal.insane.navigation.SingleDetailFragment
import dev.nehal.insane.shared.Const


class ProfGridFragment: Fragment() {
    private lateinit var binding: FragmentGridProfBinding
    private lateinit var conDTOList: ArrayList<ContentDTO>
    private lateinit var contentUIDList: ArrayList<String>
    private lateinit var imagesSnapshot: ListenerRegistration
    private lateinit var adapter: ProfGridAdapter
    private var userUid:String?=null

    companion object {

        @JvmStatic
        fun newInstance(userid: String) = ProfGridFragment().apply {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_grid_prof, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        conDTOList = ArrayList()
        contentUIDList = ArrayList()

        val itemOnClick: (Int) -> Unit = { position ->
            goToDetail(position)
        }


        adapter =
            ProfGridAdapter(conDTOList, itemClickListener = itemOnClick)
        binding.rvGrid.adapter = adapter
        binding.rvGrid.layoutManager = GridLayoutManager(activity, 3)

        getData(userUid!!)
    }

    private fun getData(userid: String) {
        imagesSnapshot = FirebaseFirestore
            .getInstance().collection("uploadedImages").whereEqualTo("uid", userid).orderBy("imgUploadDate", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                conDTOList.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot.documents) {
                    conDTOList.add(snapshot.toObject(ContentDTO::class.java)!!)
                    contentUIDList.add(snapshot.id)
                }
                adapter.notifyDataSetChanged()
            }
    }

    override fun onStop() {
        super.onStop()
        imagesSnapshot.remove()
    }

    private fun goToDetail(position: Int) {
        val dialogFragment =
            SingleDetailFragment() //here MyDialog is my custom dialog
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        val bundle = Bundle()

        val obj = conDTOList[position]
        bundle.putSerializable("CONTENT_DTO", obj)
        bundle.putString("CONTENT_UID", contentUIDList[position])

        dialogFragment.arguments = bundle
        dialogFragment.show(fragmentTransaction, "dialog")
    }
}