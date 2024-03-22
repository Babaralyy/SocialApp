package com.codecoy.mvpflycollab.ui.adapters.journey

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.JourneyCallback
import com.codecoy.mvpflycollab.databinding.JourneyItemViewBinding
import com.codecoy.mvpflycollab.datamodels.AllJourneyData
import com.codecoy.mvpflycollab.datamodels.UserPostsData
import com.codecoy.mvpflycollab.utils.Constant

class JourneyAdapter(
    private val journeyList: MutableList<AllJourneyData>,
    var context: Context,
    private var journeyCallback: JourneyCallback
) : RecyclerView.Adapter<JourneyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(JourneyItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val journeyData = journeyList[position]

        Glide
            .with(context)
            .load(Constant.MEDIA_BASE_URL + journeyData.journeyImg)
            .placeholder(R.drawable.img)
            .into(holder.mBinding.ivFolder)

        holder.mBinding.tvName.text = journeyData.title
        holder.mBinding.tvDes.text = journeyData.description

        holder.mBinding.ivView.setOnClickListener {
            journeyCallback.onJourneyClick(journeyData)
        }
        holder.mBinding.ivDel.setOnClickListener {
            journeyCallback.onJourneyDeleteClick(journeyData)
        }
    }

    override fun getItemCount(): Int {
        return journeyList.size
    }

    fun setItemList(userJourneyData: ArrayList<AllJourneyData>) {
        journeyList.clear()
        journeyList.addAll(userJourneyData)
        notifyDataSetChanged()
    }

    class ViewHolder(val mBinding: JourneyItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)
}

