package com.codecoy.mvpflycollab.ui.adapters.playlist

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.callbacks.ImageClickCallback
import com.codecoy.mvpflycollab.databinding.JourneyDetailImageItemBinding


class PlaylistDetailImageAdapter (
    private val mediaList: MutableList<Uri>,
    var context: Context,
    private var imageClickCallback: ImageClickCallback
) : RecyclerView.Adapter<PlaylistDetailImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder(JourneyDetailImageItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val img = mediaList[position]
        holder.mBinding.ivActivityImg.setImageURI(img)

        holder.mBinding.ivActivityImg.setOnClickListener {
            imageClickCallback.onImageClick(img)
        }
        holder.mBinding.ivRemove.setOnClickListener {
            imageClickCallback.onImgRemove(position)
        }
    }

    override fun getItemCount(): Int = mediaList.size
    class ViewHolder(val mBinding: JourneyDetailImageItemBinding) :
        RecyclerView.ViewHolder(mBinding.root)

}