package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.UserFollowCallback
import com.codecoy.mvpflycollab.databinding.UserItemViewBinding
import com.codecoy.mvpflycollab.datamodels.AllUserData
import com.codecoy.mvpflycollab.utils.Constant

class AllUsersAdapter(
    private val usersList: MutableList<AllUserData>,
    var context: Context,
    private val followCallback: UserFollowCallback
) : RecyclerView.Adapter<AllUsersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(UserItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val user = usersList[position]

        Glide
            .with(context)
            .load(Constant.MEDIA_BASE_URL + user.profileImg)
            .placeholder(R.drawable.img)
            .into(holder.mBinding.ivUserProfile)

        holder.mBinding.tvUserName.text = user.name

        holder.mBinding.btnFollow.setOnClickListener {
            followCallback.onFollowClick(user)
        }

    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    class ViewHolder(val mBinding: UserItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)
}