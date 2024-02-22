package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.callbacks.playlistdetailsvideo.VideoClickCallback
import com.codecoy.mvpflycollab.databinding.PlaylistDetailVideoItemBinding


class PlaylistDetailVideoAdapter(
    private val videoList: MutableList<Uri>,
    var context: Context,
    private val videoClickCallback: VideoClickCallback
) : RecyclerView.Adapter<PlaylistDetailVideoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PlaylistDetailVideoItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val videoPath = videoList[position]
        holder.mBinding.videoView.setVideoURI(videoPath)

        holder.itemView.setOnClickListener {
            videoClickCallback.onVideoClick(videoPath)
        }

    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    class ViewHolder(val mBinding: PlaylistDetailVideoItemBinding) : RecyclerView.ViewHolder(mBinding.root)
}