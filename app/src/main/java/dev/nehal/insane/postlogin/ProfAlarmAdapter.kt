package dev.nehal.insane.postlogin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.nehal.insane.model.AlarmDTO


class ProfAlarmAdapter(private val list: ArrayList<AlarmDTO>, private val itemClickListener: (Int) -> Unit)
    : RecyclerView.Adapter<ProfAlarmViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfAlarmViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ProfAlarmViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ProfAlarmViewHolder, position: Int) {
        val alarmDTO: AlarmDTO = list[position]
        holder.bind(alarmDTO, itemClickListener)
    }

    override fun getItemCount(): Int = list.size

}
