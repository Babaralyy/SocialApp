package com.codecoy.mvpflycollab.ui.adapters.playlist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.databinding.JourneyDetailImageItemBinding
import com.codecoy.mvpflycollab.databinding.NewPlaylistItemViewBinding
import com.codecoy.mvpflycollab.databinding.PlaylistDetailVideoItemBinding
import com.codecoy.mvpflycollab.databinding.PlaylistSubItemViewBinding
import com.codecoy.mvpflycollab.datamodels.AllPlaylistDetailVideos
import com.codecoy.mvpflycollab.datamodels.AllPlaylistDetailsData

class PlaylistDetailAdapter (
    private val playList: MutableList<AllPlaylistDetailsData>,
    private var context: Context
) : RecyclerView.Adapter<PlaylistDetailAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            PlaylistSubItemViewBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            ),
            context
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val playListData = playList[position]
        holder.bind(playListData)

    }

    override fun getItemCount(): Int {
        return playList.size
    }

    class ViewHolder(val mBinding: PlaylistSubItemViewBinding, val context: Context) : RecyclerView.ViewHolder(mBinding.root){
        fun bind(playListData: AllPlaylistDetailsData) {
            // Initialize inner RecyclerView
            val innerAdapter =
                PlaylistDetailVideosAdapter(playListData.videos, context)
            mBinding.rvSubItems.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = innerAdapter
            }
        }
    }

}

class PlaylistDetailVideosAdapter(
    private val playlistDetailVideos: MutableList<AllPlaylistDetailVideos>,
    var context: Context
) : RecyclerView.Adapter<PlaylistDetailVideosAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            PlaylistDetailVideoItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val videoData = playlistDetailVideos[position]
        holder.mBinding.videoView.setVideoPath(videoData.video)

    }

    override fun getItemCount(): Int {
        return playlistDetailVideos.size
    }

    class ViewHolder(val mBinding: PlaylistDetailVideoItemBinding) :
        RecyclerView.ViewHolder(mBinding.root)
}
