package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.callbacks.ShareActivityCallback
import com.codecoy.mvpflycollab.databinding.CalendarItemViewBinding
import com.codecoy.mvpflycollab.datamodels.AllActivitiesData


class CalendarAdapter(
    private val eventsList: MutableList<AllActivitiesData>,
    var context: Context,
    private var shareActivityCallback: ShareActivityCallback
) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CalendarItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val event = eventsList[position]
        holder.mBinding.tvCalendarTime.text = "${event.startTime}-${event.endTime}"
        holder.mBinding.tvCalendarName.text = event.activityName
        holder.mBinding.tvCalendarStart.text = event.activityDescription

        holder.itemView.setOnClickListener{
            shareActivityCallback.onShareActivityClick(event)
        }
    }

    override fun getItemCount(): Int {
        return eventsList.size
    }

    class ViewHolder(val mBinding: CalendarItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)
}