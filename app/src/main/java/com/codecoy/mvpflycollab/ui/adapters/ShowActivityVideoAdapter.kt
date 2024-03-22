package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.callbacks.playlistdetailsvideo.VideoClickCallback
import com.codecoy.mvpflycollab.databinding.ActivityVideoItemBinding

class ShowActivityVideoAdapter(
    private val mediaList: MutableList<Uri>,
    var context: Context,
    private var videoClickCallback: VideoClickCallback
) : RecyclerView.Adapter<ShowActivityVideoAdapter.ViewHolder>() {

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

        val videoUri = Uri.parse(video.toString())
        holder.mBinding.videoView.setVideoURI(videoUri)
        holder.mBinding.videoView.start()

        holder.mBinding.videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.setVolume(0f, 0f)
        }

        holder.mBinding.videoView.setOnClickListener {
            videoClickCallback.onVideoClick(video)
        }
        holder.mBinding.ivRemove.setOnClickListener {
            videoClickCallback.onVidRemove(position)
        }

    }
    override fun getItemCount(): Int = mediaList.size
    class ViewHolder(val mBinding: ActivityVideoItemBinding) :
        RecyclerView.ViewHolder(mBinding.root)

}