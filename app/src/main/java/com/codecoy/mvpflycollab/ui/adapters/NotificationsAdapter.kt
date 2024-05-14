package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.NotificationCallback
import com.codecoy.mvpflycollab.databinding.NotificationItemViewBinding
import com.codecoy.mvpflycollab.datamodels.NotificationListData
import com.codecoy.mvpflycollab.utils.Constant

class NotificationsAdapter(
    private val notList: MutableList<NotificationListData>,
    var context: Context,
    var notificationCallback: NotificationCallback

    ) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(NotificationItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val notData = notList[position]

        Glide
            .with(context)
            .load(Constant.MEDIA_BASE_URL + notData.user?.profileImg)
            .placeholder(R.drawable.loading_svg)
            .into(holder.mBinding.ivProfile)

        holder.mBinding.tvName.text = notData.likedBy
        holder.mBinding.tvTime.text = "${notData.time}  ${notData.date}"
        when(notData.type.toString()){
            "like" -> {
                holder.mBinding.tvSms.text = "Liked your post at"

                Glide
                    .with(context)
                    .load(Constant.MEDIA_BASE_URL + notData.postImg)
                    .placeholder(R.drawable.loading_svg)
                    .into(holder.mBinding.ivUserPost)
            }
            "comment" -> holder.mBinding.tvSms.text = "Commented on your post at"
        }




        holder.itemView.setOnClickListener{
            notificationCallback.onNotificationClick(notData)
        }
    }

    override fun getItemCount(): Int {
        return notList.size
    }

    class ViewHolder(val mBinding: NotificationItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)
}