package com.codecoy.mvpflycollab.ui.adapters.playlist

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.JourneyDetailCallback
import com.codecoy.mvpflycollab.callbacks.PlaylistDetailCallback
import com.codecoy.mvpflycollab.databinding.JourneyDetailImageItemBinding
import com.codecoy.mvpflycollab.databinding.PlaylistDetailVideoItemBinding
import com.codecoy.mvpflycollab.databinding.PlaylistSubItemViewBinding
import com.codecoy.mvpflycollab.datamodels.AllPlaylistDetailVideos
import com.codecoy.mvpflycollab.datamodels.AllPlaylistDetailsData
import com.codecoy.mvpflycollab.datamodels.JourneyDetailImages
import com.codecoy.mvpflycollab.utils.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        holder.imgBind(playListData)
        holder.videoBind(playListData)

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

        fun imgBind(playListData: AllPlaylistDetailsData) {

            val imagesList = playListData.videos.filter {
                it.imgUrl != null
            } as MutableList<AllPlaylistDetailVideos>

            // Initialize inner RecyclerView
            val innerAdapter =
                PlaylistDetailImagesAdapter(imagesList, context, playlistDetailCallback)
            mBinding.rvSubImages.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = innerAdapter
            }
        }
        fun videoBind(playListData: AllPlaylistDetailsData) {

            val videosList = playListData.videos.filter {
                it.videoUrl != null
            } as MutableList<AllPlaylistDetailVideos>

            // Initialize inner RecyclerView
            val innerAdapter =
                PlaylistDetailVideosAdapter(videosList, context, playlistDetailCallback)
            mBinding.rvSubItems.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = innerAdapter
            }
        }
    }
}


class PlaylistDetailImagesAdapter(
    private val playlistDetailImages: MutableList<AllPlaylistDetailVideos>,
    var context: Context,
    private val playlistDetailCallback: PlaylistDetailCallback
) : RecyclerView.Adapter<PlaylistDetailImagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            JourneyDetailImageItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val videoData = playlistDetailImages[position]

        holder.mBinding.ivRemove.visibility = View.GONE

        Glide
            .with(context)
            .load(Constant.MEDIA_BASE_URL + videoData.imgUrl)
            .placeholder(R.drawable.img)
            .into(holder.mBinding.ivActivityImg)

        holder.itemView.setOnClickListener {
            playlistDetailCallback.onImageUrlClick(Constant.MEDIA_BASE_URL + videoData.imgUrl)
        }
    }

    override fun getItemCount(): Int {
        return playlistDetailImages.size
    }

    class ViewHolder(val mBinding: JourneyDetailImageItemBinding) :
        RecyclerView.ViewHolder(mBinding.root)
}

class PlaylistDetailVideosAdapter(
    private val playlistDetailVideos: MutableList<AllPlaylistDetailVideos>,
    var context: Context,
    private val playlistDetailCallback: PlaylistDetailCallback
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
        val video = playlistDetailVideos[position]

        holder.mBinding.videoView.visibility = View.GONE
        holder.mBinding.ivRemove.visibility = View.GONE
        holder.mBinding.ivPlay.visibility = View.VISIBLE
        holder.mBinding.videoPlayer.visibility = View.VISIBLE

        val player = ExoPlayer.Builder(context).build()
        holder.mBinding.videoPlayer.player = player

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val mediaItem = withContext(Dispatchers.IO) {
                    MediaItem.fromUri(Constant.MEDIA_BASE_URL + video.videoUrl)
                }
                withContext(Dispatchers.Main) {
                    player.setMediaItem(mediaItem)
                    player.repeatMode = Player.REPEAT_MODE_ONE
                    player.prepare()
                }
            } catch (e: Exception) {
                Log.e(Constant.TAG, "Error preparing video playback: ${e.message}")
            }
        }

        holder.itemView.setOnClickListener {
            playlistDetailCallback.onVideoUrlClick(Constant.MEDIA_BASE_URL + video.videoUrl)
        }
    }

    override fun getItemCount(): Int {
        return playlistDetailVideos.size
    }

    class ViewHolder(val mBinding: PlaylistDetailVideoItemBinding) :
        RecyclerView.ViewHolder(mBinding.root)
}
