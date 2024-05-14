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
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.datamodels.UserPostsData
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Utils
import java.util.Locale


class PostsAdapter(
    private var postsList: MutableList<UserPostsData>,
    private var context: Context,
    private var homeCallback: HomeCallback
) : RecyclerView.Adapter<PostsAdapter.ViewHolder>() {
    private var currentUser: UserLoginData? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        currentUser = Utils.getUserFromSharedPreferences(context)
        return ViewHolder(PostItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val postsData = postsList[position]

        holder.mBinding.ivmenu.visibility = if (currentUser?.id.toString() == postsData.userId) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }


        Glide
            .with(context)
            .load(Constant.MEDIA_BASE_URL + postsData.userData?.profileImg)
            .placeholder(R.drawable.loading_svg)
            .into(holder.mBinding.ivUser)

        holder.mBinding.tvUserName.text = postsData.userData?.name
        holder.mBinding.tvUser.text = postsData.userData?.username

        if (postsData.images.isNotEmpty()) {
            if (postsData.images.size == 1) {
                holder.mBinding.tvCount.visibility = View.INVISIBLE
            } else {
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

        if (!postsData.locName.isNullOrEmpty()){
            holder.mBinding.tvLocation.text = postsData.locName
        }

        when (postsData.userLikeStatus) {
            "liked" -> {
                holder.mBinding.ivLikeimage.setImageResource(R.drawable.like_post)
                holder.mBinding.ivLikeimage.tag = R.drawable.like_post
            }

            "unliked" -> {
                holder.mBinding.ivLikeimage.setImageResource(R.drawable.dislike_post)
                holder.mBinding.ivLikeimage.tag = R.drawable.dislike_post
            }
        }

        when (postsData.userSaveStatus) {
            "saved" -> {
                holder.mBinding.ivSaveImage.setImageResource(R.drawable.save_post_filled)
                holder.mBinding.ivSaveImage.tag = R.drawable.save_post_filled
            }
            "unsaved" -> {
                holder.mBinding.ivSaveImage.setImageResource(R.drawable.unsaved_post)
                holder.mBinding.ivSaveImage.tag = R.drawable.unsaved_post
            }
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