package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.databinding.NotificationItemViewBinding
import com.codecoy.mvpflycollab.datamodels.AllActivitiesData
import com.codecoy.mvpflycollab.datamodels.NotificationListData

class NotificationsAdapter(
    private val notList: MutableList<NotificationListData>,
    var context: Context,

    ) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(NotificationItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val notData = notList[position]

        holder.itemView.setOnClickListener{

        }
    }

    override fun getItemCount(): Int {
        return notList.size
    }

    class ViewHolder(val mBinding: NotificationItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)
}