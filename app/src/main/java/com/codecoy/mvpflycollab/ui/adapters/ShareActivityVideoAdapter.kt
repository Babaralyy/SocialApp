package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.callbacks.ToShareVideoClickCallback
import com.codecoy.mvpflycollab.databinding.ActivityVideoItemBinding
import com.codecoy.mvpflycollab.datamodels.ActivityDetails
import com.codecoy.mvpflycollab.utils.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShareActivityVideoAdapter(
    private val mediaList: MutableList<ActivityDetails>,
    var context: Context,
    private var shareVideoClickCallback: ToShareVideoClickCallback
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

    /*    if (!video.videoUrl.isNullOrEmpty()){
            holder.mBinding.videoView.setVideoPath(Constant.MEDIA_BASE_URL + video.videoUrl)
        }*/

        holder.itemView.setOnClickListener {
            shareVideoClickCallback.onShareVideoClick(Constant.MEDIA_BASE_URL + video.videoUrl)
        }
    }

    override fun getItemCount(): Int = mediaList.size
    class ViewHolder(val mBinding: ActivityVideoItemBinding) :
        RecyclerView.ViewHolder(mBinding.root)

}