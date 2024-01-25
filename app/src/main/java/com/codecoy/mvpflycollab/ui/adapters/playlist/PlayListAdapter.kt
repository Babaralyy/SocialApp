package com.codecoy.mvpflycollab.ui.adapters.playlist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.callbacks.PlaylistCallback
import com.codecoy.mvpflycollab.databinding.NewPlaylistItemViewBinding

class PlayListAdapter(
    private val playList: MutableList<String>,
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

        holder.itemView.setOnClickListener {
            playlistCallback.onPlaylistClick()
        }
    }

    override fun getItemCount(): Int {
        return playList.size
    }

    class ViewHolder(val mBinding: NewPlaylistItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)

}

