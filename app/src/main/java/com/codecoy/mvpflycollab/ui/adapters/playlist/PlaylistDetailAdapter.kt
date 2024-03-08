package com.codecoy.mvpflycollab.ui.adapters.playlist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.callbacks.PlaylistDetailCallback
import com.codecoy.mvpflycollab.databinding.PlaylistDetailVideoItemBinding
import com.codecoy.mvpflycollab.databinding.PlaylistSubItemViewBinding
import com.codecoy.mvpflycollab.datamodels.AllPlaylistDetailVideos
import com.codecoy.mvpflycollab.datamodels.AllPlaylistDetailsData
import com.codecoy.mvpflycollab.utils.Constant

class PlaylistDetailAdapter (
    private val playList: MutableList<AllPlaylistDetailsData>,
    private var context: Context,
    private var playlistDetailCallback: PlaylistDetailCallback
) : RecyclerView.Adapter<PlaylistDetailAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            PlaylistSubItemViewBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            ),
            context, playlistDetailCallback
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val playListData = playList[position]
        holder.bind(playListData)

        holder.mBinding.tvTitle.text = playListData.title
        holder.mBinding.tvDes.text = playListData.description
        holder.mBinding.tvTime.text = playListData.date

    }

    override fun getItemCount(): Int {
        return playList.size
    }

    class ViewHolder(
        val mBinding: PlaylistSubItemViewBinding,
        val context: Context,
        private val playlistDetailCallback: PlaylistDetailCallback
    ) : RecyclerView.ViewHolder(mBinding.root){
        fun bind(playListData: AllPlaylistDetailsData) {
            // Initialize inner RecyclerView
            val innerAdapter =
                PlaylistDetailVideosAdapter(playListData.videos, context, playlistDetailCallback)
            mBinding.rvSubItems.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = innerAdapter
            }
        }
    }
}

class PlaylistDetailVideosAdapter(
    private val playlistDetailVideos: MutableList<AllPlaylistDetailVideos>,
    var context: Context,
    val playlistDetailCallback: PlaylistDetailCallback
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
        holder.mBinding.videoView.setVideoPath(Constant.MEDIA_BASE_URL + videoData.video)

        holder.itemView.setOnClickListener {
            playlistDetailCallback.onVideoUrlClick(Constant.MEDIA_BASE_URL + videoData.video)
        }
    }

    override fun getItemCount(): Int {
        return playlistDetailVideos.size
    }

    class ViewHolder(val mBinding: PlaylistDetailVideoItemBinding) :
        RecyclerView.ViewHolder(mBinding.root)
}
