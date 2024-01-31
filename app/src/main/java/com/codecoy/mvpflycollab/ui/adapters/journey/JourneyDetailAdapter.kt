package com.codecoy.mvpflycollab.ui.adapters.journey

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.JourneyDetailImageItemBinding
import com.codecoy.mvpflycollab.databinding.JourneyDetailItemViewBinding
import com.codecoy.mvpflycollab.datamodels.JourneyDetailImages
import com.codecoy.mvpflycollab.datamodels.JourneyDetailsData
import com.codecoy.mvpflycollab.utils.Constant


class JourneyDetailAdapter(
    private val journeyDetailList: MutableList<JourneyDetailsData>,
    var context: Context
) : RecyclerView.Adapter<JourneyDetailAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            JourneyDetailItemViewBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            ),
            context
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val journeyDetails = journeyDetailList[position]

        holder.bind(journeyDetails)

        holder.mBinding.tvTitle.text = journeyDetails.title
        holder.mBinding.tvDescription.text = journeyDetails.description
        holder.mBinding.tvDate.text = journeyDetails.date
    }

    override fun getItemCount(): Int {
        return journeyDetailList.size
    }

    class ViewHolder(val mBinding: JourneyDetailItemViewBinding, private val mContext: Context) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(journeyDetails: JourneyDetailsData) {
            // Initialize inner RecyclerView
            val innerAdapter =
                JourneyDetailImagesAdapter(journeyDetails.journeyDetailImages, mContext)
            mBinding.rvDetailsImages.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = innerAdapter
            }
        }
    }
}

class JourneyDetailImagesAdapter(
    private val journeyDetailImages: MutableList<JourneyDetailImages>,
    var context: Context
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

        Glide
            .with(context)
            .load(Constant.MEDIA_BASE_URL + imageData.image)
            .placeholder(R.drawable.img)
            .into(holder.mBinding.ivImage)
    }

    override fun getItemCount(): Int {
        return journeyDetailImages.size
    }

    class ViewHolder(val mBinding: JourneyDetailImageItemBinding) :
        RecyclerView.ViewHolder(mBinding.root)
}