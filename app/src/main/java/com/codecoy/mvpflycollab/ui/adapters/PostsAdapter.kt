package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.HomeCallback
import com.codecoy.mvpflycollab.databinding.PostItemViewBinding
import com.codecoy.mvpflycollab.datamodels.UserPostsData
import com.codecoy.mvpflycollab.utils.Constant
import java.util.Locale


class PostsAdapter(
    private var postsList: MutableList<UserPostsData>,
    private var context: Context,
    private var homeCallback: HomeCallback
) : RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    private var filteredList: MutableList<UserPostsData> = postsList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PostItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val postsData = postsList[position]


        Glide
            .with(context)
            .load(Constant.MEDIA_BASE_URL + postsData.userData?.profileImg)
            .placeholder(R.drawable.img)
            .into(holder.mBinding.ivUser)

        holder.mBinding.tvUserName.text = postsData.userData?.username

        if (postsData.images.isNotEmpty()) {
            Glide
                .with(context)
                .load(Constant.MEDIA_BASE_URL + postsData.images[0].postImg)
                .placeholder(R.drawable.img)
                .into(holder.mBinding.ivPostImage)
        }


        if (postsData.userLikeStatus == "liked") {
            holder.mBinding.ivLikeimage.setImageResource(R.drawable.like_post)
        } else if (postsData.userLikeStatus == "unliked") {
            holder.mBinding.ivLikeimage.setImageResource(R.drawable.dislike_post)
        }

//        holder.mBinding.tvLocation.text = "Tokyo, Japan"
        holder.mBinding.tvlikenumber.text = postsData.totalLikes.toString()
        holder.mBinding.tvcommentnumber.text = postsData.totalComments.toString()
        holder.mBinding.tvComments.text =
            "${postsData.userData?.username}  ${postsData.postDesc.toString()}"

        holder.mBinding.ivCommentimage.setOnClickListener {
            homeCallback.onCommentsClick(postsData)
        }
        holder.mBinding.ivLikeimage.setOnClickListener {
            homeCallback.onLikeClick(postsData, holder.mBinding)
        }

        holder.mBinding.ivmenu.setOnClickListener {
            homeCallback.onMenuClick(postsData, holder.mBinding)
        }

    }

    override fun getItemCount(): Int {
        return postsList.size
    }

    class ViewHolder(val mBinding: PostItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)

    fun setItemList(userPostsData: ArrayList<UserPostsData>) {
        postsList.clear()
        postsList.addAll(userPostsData)
        notifyDataSetChanged()
    }


    fun filter(text: String) {
        //new array list that will hold the filtered data
        val filteredNames = ArrayList<UserPostsData>()


        //looping through existing elements
        for (s in postsList) {
            //if the existing elements contains the search input
            if (s.userData?.username.toString().lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            ) {
                //adding the element to filtered list
                filteredNames.add(s)
            }
        }

        //calling a method of the adapter class and passing the filtered list
        this.postsList = filteredNames
        notifyDataSetChanged()

    }
}