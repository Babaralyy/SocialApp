package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.databinding.ShareItemViewBinding

class ShareActivityAdapter(
    private val activityMembersList: MutableList<String>,
    private var context: Context
) : RecyclerView.Adapter<ShareActivityAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ShareItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storyData = activityMembersList[position]


    }

    override fun getItemCount(): Int {
        return activityMembersList.size
    }

    class ViewHolder(val mBinding: ShareItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)
}