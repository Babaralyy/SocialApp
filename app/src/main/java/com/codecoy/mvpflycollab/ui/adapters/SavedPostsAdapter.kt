package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.HomeCallback
import com.codecoy.mvpflycollab.databinding.PostItemViewBinding
import com.codecoy.mvpflycollab.datamodels.UserPostsData
import com.codecoy.mvpflycollab.utils.Constant
import java.util.Locale

class SavedPostsAdapter(
    private var postsList: MutableList<UserPostsData>,
    private var context: Context,
    private var homeCallback: HomeCallback
) : RecyclerView.Adapter<SavedPostsAdapter.ViewHolder>() {

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

        holder.mBinding.tvUserName.text = postsData.userData?.name
        holder.mBinding.tvLocation.text = postsData.userData?.username

        if (postsData.images.isNotEmpty()) {
            if (postsData.images.size == 1){
                holder.mBinding.tvCount.visibility = View.INVISIBLE
            }else {
                holder.mBinding.tvCount.visibility = View.VISIBLE
                holder.mBinding.tvCount.text = "1/${postsData.images.size}"
            }


            val sliderView = holder.mBinding.sliderLay.imageSlider
            val adapter = ImageSliderAdapter(context, postsData.images)
            sliderView.adapter = adapter

            sliderView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                    // Unused
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    // Unused
                }

                override fun onPageSelected(position: Int) {
                    // Here you get the position of the currently selected image
                    holder.mBinding.tvCount.text = "${(position + 1)}/${postsData.images.size}"
                }
            })

        }


        if (postsData.userLikeStatus == "liked") {
            holder.mBinding.ivLikeimage.setImageResource(R.drawable.like_post)
        }
        else if (postsData.userLikeStatus == "unliked") {
            holder.mBinding.ivLikeimage.setImageResource(R.drawable.dislike_post)
        }

        if (postsData.userSaveStatus == "saved") {
            holder.mBinding.ivSaveImage.setImageResource(R.drawable.save_post_filled)
        }
        else if (postsData.userSaveStatus == "unsaved") {
            holder.mBinding.ivSaveImage.setImageResource(R.drawable.unsaved_post)
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

        holder.mBinding.ivUser.setOnClickListener {
            homeCallback.onUserClick(postsData, holder.mBinding)
        }
        holder.mBinding.tvUserName.setOnClickListener {
            homeCallback.onUserClick(postsData, holder.mBinding)
        }
        holder.mBinding.ivSaveImage.setOnClickListener {
            homeCallback.onSaveClick(postsData, holder.mBinding)
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

}