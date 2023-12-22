package com.codecoy.mvpflycollab.ui.adapters.journey

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.callbacks.JourneyCallback
import com.codecoy.mvpflycollab.databinding.JourneyItemViewBinding

class JourneyAdapter(
    private val journeyList: MutableList<String>,
    var context: Context,
    var journeyCallback: JourneyCallback
) : RecyclerView.Adapter<JourneyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(JourneyItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mBinding.ivFolder.setOnClickListener {
            journeyCallback.onJourneyClick()
        }
    }

    override fun getItemCount(): Int {
        return journeyList.size
    }

    class ViewHolder(val mBinding: JourneyItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)
}

