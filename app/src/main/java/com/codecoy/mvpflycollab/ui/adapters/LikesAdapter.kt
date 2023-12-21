package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.databinding.LikesItemViewBinding

class LikesAdapter(
    private val likesList: MutableList<String>,
    var context: Context,
) : RecyclerView.Adapter<LikesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LikesItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return likesList.size
    }

    class ViewHolder(val mBinding: LikesItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)
}