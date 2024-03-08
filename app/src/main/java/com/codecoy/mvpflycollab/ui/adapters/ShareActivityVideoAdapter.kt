package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.callbacks.ToShareVideoClickCallback
import com.codecoy.mvpflycollab.databinding.ActivityVideoItemBinding
import com.codecoy.mvpflycollab.datamodels.ActivityDetails
import com.codecoy.mvpflycollab.utils.Constant

class ShareActivityVideoAdapter(
    private val mediaList: MutableList<ActivityDetails>,
    var context: Context,
    var shareVideoClickCallback: ToShareVideoClickCallback
) : RecyclerView.Adapter<ShareActivityVideoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ActivityVideoItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = mediaList[position]
        if (!video.videoUrl.isNullOrEmpty()){
            holder.mBinding.videoView.setVideoPath(Constant.MEDIA_BASE_URL + video.videoUrl)
        }

        holder.itemView.setOnClickListener {
            shareVideoClickCallback.onShareVideoClick(Constant.MEDIA_BASE_URL + video.videoUrl)
        }
    }

    override fun getItemCount(): Int = mediaList.size
    class ViewHolder(val mBinding: ActivityVideoItemBinding) :
        RecyclerView.ViewHolder(mBinding.root)

}