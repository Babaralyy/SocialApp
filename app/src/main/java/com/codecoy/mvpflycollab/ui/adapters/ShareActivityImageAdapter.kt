package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.ToShareImageClickCallback
import com.codecoy.mvpflycollab.databinding.ActivityImgItemBinding
import com.codecoy.mvpflycollab.datamodels.ActivityDetails
import com.codecoy.mvpflycollab.utils.Constant

class ShareActivityImageAdapter(
    private val mediaList: MutableList<ActivityDetails>,
    var context: Context,
    private var toShareImageClickCallback: ToShareImageClickCallback
) : RecyclerView.Adapter<ShareActivityImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ActivityImgItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val img = mediaList[position]

        holder.mBinding.ivRemove.visibility = View.GONE

        Glide
            .with(context)
            .load(Constant.MEDIA_BASE_URL + img.imgUrl)
            .placeholder(R.drawable.img)
            .error(R.drawable.loading_error)
            .into(holder.mBinding.ivActivityImg)

        holder.itemView.setOnClickListener {
            toShareImageClickCallback.onShareImageClick(Constant.MEDIA_BASE_URL + img.imgUrl)
        }
    }

    override fun getItemCount(): Int = mediaList.size
    class ViewHolder(val mBinding: ActivityImgItemBinding) :
        RecyclerView.ViewHolder(mBinding.root)

}