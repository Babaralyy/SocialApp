package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.JourneyDetailImageItemBinding
import com.codecoy.mvpflycollab.utils.Constant


class JourneyDetailImageAdapter(
    private val imagesList: MutableList<String>,
    var context: Context,
) : RecyclerView.Adapter<JourneyDetailImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(JourneyDetailImageItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = imagesList[position]

        Glide
            .with(context)
            .load(Constant.MEDIA_BASE_URL + image)
            .placeholder(R.drawable.img)
            .into(holder.mBinding.ivImage)

    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

    class ViewHolder(val mBinding: JourneyDetailImageItemBinding) : RecyclerView.ViewHolder(mBinding.root)
}