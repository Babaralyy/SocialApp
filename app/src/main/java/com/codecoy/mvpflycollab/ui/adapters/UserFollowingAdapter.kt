package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.UserFollowingItemBinding
import com.codecoy.mvpflycollab.datamodels.UserFollowingData
import com.codecoy.mvpflycollab.utils.Constant

class UserFollowingAdapter (
    private val followingList: MutableList<UserFollowingData>,
    var context: Context,
) : RecyclerView.Adapter<UserFollowingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(UserFollowingItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = followingList[position]
        Glide
            .with(context)
            .load(Constant.MEDIA_BASE_URL + data.profileImg)
            .placeholder(R.drawable.img)
            .into(holder.mBinding.ivProfile)

        holder.mBinding.tvName.text = data.name
        holder.mBinding.tvSms.text = data.username
    }

    override fun getItemCount(): Int {
        return followingList.size
    }

    class ViewHolder(val mBinding: UserFollowingItemBinding) : RecyclerView.ViewHolder(mBinding.root)
}