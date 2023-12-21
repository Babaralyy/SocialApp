package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.databinding.JourneyItemViewBinding

class JourneyAdapter(
    private val journeyList: MutableList<String>,
    var context: Context
) : RecyclerView.Adapter<JourneyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(JourneyItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return journeyList.size
    }

    class ViewHolder(val mBinding: JourneyItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)
}

