package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.UserFollowCallback
import com.codecoy.mvpflycollab.databinding.PeopleItemBinding
import com.codecoy.mvpflycollab.databinding.UserItemViewBinding
import com.codecoy.mvpflycollab.datamodels.AllUserData
import com.codecoy.mvpflycollab.ui.adapters.userprofile.AllUsersAdapter
import com.codecoy.mvpflycollab.utils.Constant

class SearchUserAdapter(
    private val usersList: MutableList<AllUserData>,
    var context: Context,
    private val followCallback: UserFollowCallback,
) : RecyclerView.Adapter<SearchUserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PeopleItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val user = usersList[position]

        Glide
            .with(context)
            .load(Constant.MEDIA_BASE_URL + user.profileImg.toString())
            .placeholder(R.drawable.img)
            .into(holder.mBinding.ivShareItem)

        holder.mBinding.tvName.text = user.name
        holder.mBinding.tvUsername.text = user.username

        if (user.followerDetails != null && user.followerDetails?.followStatus == "sent"){
            holder.mBinding.tvDecline.text = "Sent"
        }
        holder.mBinding.tvAccept.setOnClickListener {
            followCallback.onFollowClick(user)
        }
        holder.mBinding.tvDecline.setOnClickListener {
            followCallback.onCollabClick(user)
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

    class ViewHolder(val mBinding: PeopleItemBinding) : RecyclerView.ViewHolder(mBinding.root)
}