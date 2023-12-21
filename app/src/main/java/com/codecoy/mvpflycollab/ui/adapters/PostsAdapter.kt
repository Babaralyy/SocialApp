package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.callbacks.HomeCallback
import com.codecoy.mvpflycollab.databinding.PostItemViewBinding


class PostsAdapter(
    private val postsList: MutableList<String>,
    private var context: Context,
    private var homeCallback: HomeCallback
) : RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PostItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storyData = postsList[position]
        Glide
            .with(context)
            .load(storyData)
            .into(holder.mBinding.ivUser)


        holder.mBinding.ivCommentimage.setOnClickListener {
            homeCallback.onCommentsClick()
        }

    }

    override fun getItemCount(): Int {
        return postsList.size
    }

    class ViewHolder(val mBinding: PostItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)
}