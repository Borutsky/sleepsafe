package com.dudo.sleepsafe.ui.main.history

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dudo.sleepsafe.R
import com.dudo.sleepsafe.data.model.DetectedActivity
import kotlinx.android.synthetic.main.activity_tracking.view.*
import kotlinx.android.synthetic.main.activity_tracking.view.simpleChart
import kotlinx.android.synthetic.main.item_detected_activity.view.*
import java.text.SimpleDateFormat

class DetectedActivitiesAdapter(val context: Context) : RecyclerView.Adapter<DetectedActivitiesAdapter.ViewHolder>() {

    private var items = mutableListOf<DetectedActivity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_detected_activity, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.simpleChart.update(items[position].dataset.toMutableList())
        val time = SimpleDateFormat("hh:mm a")
        val date = SimpleDateFormat("dd/MM/yyyy")
        holder.itemView.textBody.text =
            "Vibration at ${time.format(items[position].startDate)}\n${date.format(items[position].startDate)}"
    }

    fun updateList(items: MutableList<DetectedActivity>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}