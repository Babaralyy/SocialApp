package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.callbacks.ImageClickCallback
import com.codecoy.mvpflycollab.databinding.ActivityImgItemBinding
import com.codecoy.mvpflycollab.databinding.ActivityVideoItemBinding

class ShowActivityImageAdapter(
    private val mediaList: MutableList<Uri>,
    var context: Context,
    private var imageClickCallback: ImageClickCallback
) : RecyclerView.Adapter<ShowActivityImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return  ViewHolder(ActivityImgItemBinding.inflate(LayoutInflater.from(context), parent, false))

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
    class ViewHolder(val mBinding: ActivityImgItemBinding) :
        RecyclerView.ViewHolder(mBinding.root)

}