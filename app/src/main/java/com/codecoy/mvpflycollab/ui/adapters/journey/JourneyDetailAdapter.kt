package com.codecoy.mvpflycollab.ui.adapters.journey

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
import com.codecoy.mvpflycollab.callbacks.ToShareVideoClickCallback
import com.codecoy.mvpflycollab.databinding.ActivityVideoItemBinding
import com.codecoy.mvpflycollab.databinding.JourneyDetailImageItemBinding
import com.codecoy.mvpflycollab.databinding.JourneyDetailItemViewBinding
import com.codecoy.mvpflycollab.datamodels.ActivityDetails
import com.codecoy.mvpflycollab.datamodels.JourneyDetailImages
import com.codecoy.mvpflycollab.datamodels.JourneyDetailsData
import com.codecoy.mvpflycollab.utils.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class JourneyDetailAdapter(
    private val journeyDetailList: MutableList<JourneyDetailsData>,
    var context: Context,
    private val journeyDetailCallback: JourneyDetailCallback
) : RecyclerView.Adapter<JourneyDetailAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            JourneyDetailItemViewBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            ),
            context, journeyDetailCallback
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val journeyDetails = journeyDetailList[position]

        holder.imageBind(journeyDetails)
        holder.videoBind(journeyDetails)

        holder.mBinding.tvTitle.text = journeyDetails.title
        holder.mBinding.tvDescription.text = journeyDetails.description
        holder.mBinding.tvDate.text = journeyDetails.date
    }

    override fun getItemCount(): Int {
        return journeyDetailList.size
    }

    class ViewHolder(
        val mBinding: JourneyDetailItemViewBinding,
        private val mContext: Context,
        private val journeyDetailCallback: JourneyDetailCallback
    ) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun imageBind(journeyDetails: JourneyDetailsData) {

            val imagesList = journeyDetails.journeyDetailImages.filter {
                it.imgUrl != null
            } as MutableList<JourneyDetailImages>

            // Initialize inner RecyclerView
            val innerAdapter =
                JourneyDetailImagesAdapter(imagesList, mContext, journeyDetailCallback)
            mBinding.rvDetailsImages.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = innerAdapter
            }
        }

        fun videoBind(journeyDetails: JourneyDetailsData) {

            val videosList = journeyDetails.journeyDetailImages.filter {
                it.videoUrl != null
            } as MutableList<JourneyDetailImages>

            // Initialize inner RecyclerView
            val innerAdapter =
                ShareActivityVideoAdapter(videosList, mContext, journeyDetailCallback)
            mBinding.rvDetailsVideos.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = innerAdapter
            }
        }

    }
}

class JourneyDetailImagesAdapter(
    private val journeyDetailImages: MutableList<JourneyDetailImages>,
    var context: Context,
    private var journeyDetailCallback: JourneyDetailCallback
) : RecyclerView.Adapter<JourneyDetailImagesAdapter.ViewHolder>() {

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
        val imageData = journeyDetailImages[position]

        holder.mBinding.ivRemove.visibility = View.GONE

        Glide
            .with(context)
            .load(Constant.MEDIA_BASE_URL + imageData.imgUrl)
            .placeholder(R.drawable.img)
            .into(holder.mBinding.ivActivityImg)

        holder.itemView.setOnClickListener {
            journeyDetailCallback.onImgClick(Constant.MEDIA_BASE_URL + imageData.imgUrl)
        }
    }

    override fun getItemCount(): Int {
        return journeyDetailImages.size
    }

    class ViewHolder(val mBinding: JourneyDetailImageItemBinding) :
        RecyclerView.ViewHolder(mBinding.root)
}

class ShareActivityVideoAdapter(
    private val journeyDetailImages: MutableList<JourneyDetailImages>,
    var context: Context,
    private var journeyDetailCallback: JourneyDetailCallback
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
        val video = journeyDetailImages[position]

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
            journeyDetailCallback.onVidClick(Constant.MEDIA_BASE_URL + video.videoUrl)
        }
    }

    override fun getItemCount(): Int = journeyDetailImages.size
    class ViewHolder(val mBinding: ActivityVideoItemBinding) :
        RecyclerView.ViewHolder(mBinding.root)

}