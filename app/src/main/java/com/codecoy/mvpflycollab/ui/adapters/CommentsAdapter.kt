package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.callbacks.LikesCallback
import com.codecoy.mvpflycollab.databinding.CommentsItemViewBinding

class CommentsAdapter (
    private val commentsList: MutableList<String>,
    var context: Context,
    private var likesCallback: LikesCallback,
) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CommentsItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mBinding.ivCLike.setOnClickListener {
            likesCallback.onLikesClick()
        }
    }

    override fun getItemCount(): Int {
        return commentsList.size
    }

    class ViewHolder(val mBinding: CommentsItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)
}