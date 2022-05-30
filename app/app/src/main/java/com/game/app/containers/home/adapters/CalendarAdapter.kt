package com.game.app.containers.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.game.app.R
import com.game.app.containers.base.BaseAdapter
import com.game.app.databinding.AdapterCalendarItemBinding
import com.game.app.models.calendar.CalendarItemDataModel

class CalendarAdapter (
    private val context: Context,
    private val days: ArrayList<CalendarItemDataModel>
) : BaseAdapter<CalendarItemDataModel, AdapterCalendarItemBinding>() {

    private var currentPosition: Int? = null

    public fun clearPosition(){
        currentPosition = null
        notifyDataSetChanged()
    }

    public fun setCurrentPosition(position: Int){
        currentPosition = position
        notifyDataSetChanged()
    }

    override fun getAdapterBinding(parent: ViewGroup, viewType: Int): BaseViewHolder<AdapterCalendarItemBinding> {
        val binding = AdapterCalendarItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AdapterCalendarItemBinding>, position: Int) {
        val data = days[position]

        holder.binding.textViewNumberDay.text =
            if(data.number > 10) data.number.toString() else "0" + data.number.toString()
        holder.binding.textViewStringDay.text = data.dayWeek
        holder.binding.textViewCountNew.text = data.countNew

        if((currentPosition != null) && (currentPosition == position)){
            holder.binding.constraintLayoutItemAdapter.setBackgroundResource(R.drawable.vector_calendar_item)
        }else{
            holder.binding.constraintLayoutItemAdapter.setBackgroundResource(R.drawable.vector_calendar_black_item)
        }

        /*holder.binding.idAdapter.setOnClickListener {
            holder.binding.constraintLayoutItemAdapter.setBackgroundResource(R.drawable.vector_calendar_item)
        }*/
    }

    override fun getItemCount(): Int {
        return days.size
    }
}