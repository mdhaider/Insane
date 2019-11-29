package dev.nehal.insane.postlogin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.nehal.insane.model.AlarmDTO
import dev.nehal.insane.model.ContentDTO
import dev.nehal.insane.model.Users


class PeopleAdapter(private val list: ArrayList<Users>,private val contentList: ArrayList<ContentDTO>,
                    private val commentList: ArrayList<AlarmDTO>,
                    private val isAdmin:Boolean,private val itemClickListener: (Int) -> Unit,
                    private val listener: OnItemClickListener)
    : RecyclerView.Adapter<PeopleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PeopleViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        val users: Users = list[position]
        holder.bind(users,contentList,commentList,isAdmin,itemClickListener,listener )
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun blockUser(users: Users)
        fun approveUser(users: Users)

    }

}
