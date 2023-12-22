package com.codecoy.mvpflycollab.ui.adapters.journey

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.databinding.JourneyDetailItemViewBinding


class JourneyDetailAdapter(
    private val journeyDetailList: MutableList<String>,
    var context: Context
) : RecyclerView.Adapter<JourneyDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(JourneyDetailItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return journeyDetailList.size
    }

    class ViewHolder(val mBinding: JourneyDetailItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)
}