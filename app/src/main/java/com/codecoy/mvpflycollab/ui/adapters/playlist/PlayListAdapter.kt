package com.codecoy.mvpflycollab.ui.adapters.playlist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.PlaylistCallback
import com.codecoy.mvpflycollab.databinding.NewPlaylistItemViewBinding
import com.codecoy.mvpflycollab.datamodels.AllPlaylistData
import com.codecoy.mvpflycollab.utils.Constant

class PlayListAdapter(
    private val playList: MutableList<AllPlaylistData>,
    private var context: Context,
    private var playlistCallback: PlaylistCallback
) : RecyclerView.Adapter<PlayListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            NewPlaylistItemViewBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val playData = playList[position]

        Glide
            .with(context)
            .load(Constant.MEDIA_BASE_URL + playData.playlistImg)
            .placeholder(R.drawable.img)
            .into(holder.mBinding.imageView2)

        holder.mBinding.tvName.text = playData.title
        holder.mBinding.tvDes.text = playData.description

        holder.itemView.setOnClickListener {
            playlistCallback.onPlaylistClick(playData)
        }
    }

    override fun getItemCount(): Int {
        return playList.size
    }

    class ViewHolder(val mBinding: NewPlaylistItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)

}

