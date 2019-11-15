package dev.nehal.insane.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.nehal.insane.model.Users


class PeopleAdapter(private val list: ArrayList<Users>)
    : RecyclerView.Adapter<PeopleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PeopleViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        val users: Users = list[position]
        holder.bind(users)
    }

    override fun getItemCount(): Int = list.size

}
