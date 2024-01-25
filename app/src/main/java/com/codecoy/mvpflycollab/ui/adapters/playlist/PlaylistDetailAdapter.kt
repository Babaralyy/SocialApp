package com.codecoy.mvpflycollab.ui.adapters.playlist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.databinding.NewPlaylistItemViewBinding
import com.codecoy.mvpflycollab.datamodels.PlaylistDetailData

class PlaylistDetailAdapter (
    private val playList: MutableList<PlaylistDetailData>,
    private var context: Context
) : RecyclerView.Adapter<PlaylistDetailAdapter.ViewHolder>() {
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


    }

    override fun getItemCount(): Int {
        return playList.size
    }

    class ViewHolder(val mBinding: NewPlaylistItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)

}
