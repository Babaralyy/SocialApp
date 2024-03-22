package com.codecoy.mvpflycollab.ui.adapters.userprofile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.UserProfilePostCallback
import com.codecoy.mvpflycollab.databinding.ProfilePostsItemBinding
import com.codecoy.mvpflycollab.datamodels.UserProfilePosts
import com.codecoy.mvpflycollab.utils.Constant

class UserProfilePostsAdapter (
    private val postsList: MutableList<UserProfilePosts>,
    var context: Context,
    private var userProfilePostCallback: UserProfilePostCallback,
) : RecyclerView.Adapter<UserProfilePostsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ProfilePostsItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val post = postsList[position]

        if (post.postImages.isNotEmpty()){
            Glide
                .with(context)
                .load(Constant.MEDIA_BASE_URL + post.postImages[0].postImg)
                .placeholder(R.drawable.img)
                .into(holder.mBinding.ivImage)
        }

        holder.itemView.setOnClickListener {
            userProfilePostCallback.onUserProfilePostClick(post)
        }
    }

    override fun getItemCount(): Int {
        return postsList.size
    }

    class ViewHolder(val mBinding: ProfilePostsItemBinding) : RecyclerView.ViewHolder(mBinding.root)
}