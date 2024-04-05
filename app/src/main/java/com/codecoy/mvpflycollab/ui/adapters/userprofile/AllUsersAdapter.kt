package com.codecoy.mvpflycollab.ui.adapters.userprofile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.UserFollowCallback
import com.codecoy.mvpflycollab.databinding.UserItemViewBinding
import com.codecoy.mvpflycollab.datamodels.AllJourneyData
import com.codecoy.mvpflycollab.datamodels.AllUserData
import com.codecoy.mvpflycollab.utils.Constant

class AllUsersAdapter(
    private val usersList: MutableList<AllUserData>,
    var context: Context,
    private val followCallback: UserFollowCallback,
) : RecyclerView.Adapter<AllUsersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(UserItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val user = usersList[position]

        Glide
            .with(context)
            .load(Constant.MEDIA_BASE_URL + user.profileImg.toString())
            .placeholder(R.drawable.img)
            .into(holder.mBinding.ivUserProfile)

        holder.mBinding.tvUserName.text = user.name

        if (user.followerDetails != null && user.followerDetails?.followStatus == "sent"){
            holder.mBinding.btnFollow.text = "Sent"
        }
        holder.mBinding.btnFollow.setOnClickListener {
            followCallback.onFollowClick(user)
        }
        holder.mBinding.btnCollaborate.setOnClickListener {
            followCallback.onCollabClick(user)
        }
        holder.mBinding.ivUserProfile.setOnClickListener {
            followCallback.onImageClick(user)
        }
        holder.mBinding.tvUserName.setOnClickListener {
            followCallback.onNameClick(user)
        }

    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    fun setItemList(usersList: ArrayList<AllUserData>) {
        this.usersList.clear()
        this.usersList.addAll(usersList)
        notifyDataSetChanged()
    }

    class ViewHolder(val mBinding: UserItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)
}