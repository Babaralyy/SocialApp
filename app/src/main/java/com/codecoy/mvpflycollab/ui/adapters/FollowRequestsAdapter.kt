package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.databinding.FollowRequestItemBinding

class FollowRequestsAdapter(
    private val followRequestList: MutableList<String>,
    var context: Context,
) : RecyclerView.Adapter<FollowRequestsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FollowRequestItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return followRequestList.size
    }

    class ViewHolder(val mBinding: FollowRequestItemBinding) : RecyclerView.ViewHolder(mBinding.root)
}