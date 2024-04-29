package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.callbacks.ChatsCallback
import com.codecoy.mvpflycollab.databinding.WhomeChatToItemViewBinding
import com.codecoy.mvpflycollab.datamodels.ChatsData

class ChatListAdapter (
    private val chatsList: MutableList<ChatsData>,
    var context: Context,
    private val chatsCallback: ChatsCallback
) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(WhomeChatToItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val chatData = chatsList[position]

        holder.mBinding.tvName.text = chatData.fullName
        holder.mBinding.tvSms.text = chatData.userName

        holder.itemView.setOnClickListener {
            chatsCallback.onUserClick()
        }
    }

    override fun getItemCount(): Int {
        return chatsList.size
    }

    class ViewHolder(val mBinding: WhomeChatToItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)
}