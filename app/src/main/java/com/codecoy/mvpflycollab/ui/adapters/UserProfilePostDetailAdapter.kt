package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.UserProfilePostCallback
import com.codecoy.mvpflycollab.databinding.PostItemViewBinding
import com.codecoy.mvpflycollab.databinding.ProfilePostsItemBinding
import com.codecoy.mvpflycollab.datamodels.UserProfilePosts
import com.codecoy.mvpflycollab.ui.adapters.userprofile.UserProfilePostsAdapter
import com.codecoy.mvpflycollab.utils.Constant

class UserProfilePostDetailAdapter(
    private val postsList: MutableList<UserProfilePosts>,
    var context: Context,
) : RecyclerView.Adapter<UserProfilePostDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PostItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val post = postsList[position]

        if (post.postImages.isNotEmpty()){
            Glide
                .with(context)
                .load(Constant.MEDIA_BASE_URL + post.postImages[0].postImg)
                .placeholder(R.drawable.img)
                .into(holder.mBinding.ivPostImage)
        }



    }

    override fun getItemCount(): Int {
        return postsList.size
    }

    class ViewHolder(val mBinding: PostItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)
}