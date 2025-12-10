package com.example.exploregang.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.exploregang.R
import com.example.exploregang.data.model.Actividad
import com.example.exploregang.databinding.ItemActivityBinding
import com.example.exploregang.util.Utils
import com.example.exploregang.util.Utils.dateToStringWithHour

class ActivityAdapter(

    private val listener: OnClickActivityListener,
    private val context: Context
) : RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {
    private var activities: ArrayList<Actividad> = ArrayList()

    interface OnClickActivityListener {
        fun onClickActivity(activity: Actividad?)
        fun onLongClickActivity(activity: Actividad?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemActivityBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = activities[position]

        if (activity.address.isNullOrEmpty()) {
            holder.binding.ivLocation.isGone=true
            holder.binding.tvLocation.isGone=true

        }else{
            holder.binding.ivLocation.isVisible=true
            holder.binding.tvLocation.isVisible=true
        }
        holder.binding.tvLocation.setText(activity.address)
        if (activity.startDate == null) {
            holder.binding.tvStartDate.isGone = true
            holder.binding.ivCalendar.isGone = true
        } else {
            holder.binding.tvStartDate.setText(dateToStringWithHour(activity.startDate))
        }

        holder.binding.tvCreator.setText(activity.creator!!.name)
        holder.binding.tvActivityName.setText(activity.name)
        if (activity.description.isNullOrEmpty()) {
            holder.binding.tvDescription.isGone = true
        } else {
            holder.binding.tvDescription.setText(activity.description)
        }

        if (!activity.imageId.isNullOrEmpty()) {
            Utils.getImage(
                activity.imageId!!,
                holder.binding.ivActivityPhotoItem
            )
        } else {
            holder.binding.ivActivityPhotoItem.setImageResource(R.drawable.activities3)
        }
        holder.bind(listener, activity)
    }

    fun addActivity(activity: Actividad) {
        activities.add(activity)
        notifyItemInserted(activities.size - 1)
    }

    fun editActivity(position: Int, activity: Actividad) {
        activities[position] = activity
        notifyItemChanged(position)
    }

    fun removeActivity(position: Int) {
        activities.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, activities.size)
    }

    fun update(data: Collection<Actividad>) {
        activities.clear()
        activities.addAll(data)
         notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return activities.size
    }

    fun clear() {
        activities.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ItemActivityBinding

        init {
            binding = ItemActivityBinding.bind(itemView)
        }

        //SOLUCIÓN 1 y 2: se inicializa el listener y su dependencia a cada elemento intenView
        fun bind(listener: OnClickActivityListener, actividad: Actividad?) {
            itemView.setOnClickListener { view: View? ->
                listener.onClickActivity(actividad)
            }
            itemView.setOnLongClickListener { view:View?->
                listener.onLongClickActivity(actividad)
                true
            }
        }
    }
}
