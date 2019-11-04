package dev.nehal.insane.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.nehal.insane.modules.login.User


class PeopleAdapter(private val list: ArrayList<User>)
    : RecyclerView.Adapter<PeopleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PeopleViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        val user: User = list[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = list.size

}
