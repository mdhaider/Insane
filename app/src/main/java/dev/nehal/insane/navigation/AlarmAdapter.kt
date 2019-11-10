package dev.nehal.insane.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.nehal.insane.model.AlarmDTO


class AlarmAdapter(private val list: ArrayList<AlarmDTO>, private val itemClickListener: (Int) -> Unit)
    : RecyclerView.Adapter<AlarmViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AlarmViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarmDTO: AlarmDTO = list[position]
        holder.bind(alarmDTO, itemClickListener)
    }

    override fun getItemCount(): Int = list.size

}
