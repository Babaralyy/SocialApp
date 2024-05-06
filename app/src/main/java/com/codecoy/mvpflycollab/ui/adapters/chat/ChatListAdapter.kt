package com.codecoy.mvpflycollab.ui.adapters.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.ChatsCallback
import com.codecoy.mvpflycollab.databinding.WhomeChatToItemViewBinding
import com.codecoy.mvpflycollab.datamodels.UserFollowingData
import com.codecoy.mvpflycollab.utils.Constant

class ChatListAdapter (
    private val chatsList: MutableList<UserFollowingData>,
    var context: Context,
    private val chatsCallback: ChatsCallback
) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(WhomeChatToItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val chatData = chatsList[position]

        Glide
            .with(context)
            .load(Constant.MEDIA_BASE_URL + chatData.profileImg)
            .placeholder(R.drawable.img)
            .into(holder.mBinding.ivProfile)


        holder.mBinding.tvName.text = chatData.name
        holder.mBinding.tvSms.text = chatData.username



//        if (chatData.online == true){
//            holder.mBinding.ivOnline.visibility = View.VISIBLE
//        } else {
//            holder.mBinding.ivOnline.visibility = View.GONE
//        }

        holder.itemView.setOnClickListener {
            chatsCallback.onFollowerClick(chatData)
        }
    }

    override fun getItemCount(): Int {
        return chatsList.size
    }

    class ViewHolder(val mBinding: WhomeChatToItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)
}