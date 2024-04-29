package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.databinding.ChatReceiverBinding
import com.codecoy.mvpflycollab.databinding.ChatSenderBinding
import com.codecoy.mvpflycollab.datamodels.MessageData

class OneToOneChatAdapter (
    private val chat1List: MutableList<MessageData>,
    var context: Context,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_1 = 1
        private const val VIEW_TYPE_2 = 2
    }

    inner class Type1ViewHolder(private val binding: ChatSenderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MessageData) {
            // Bind data to views
            binding.tvSender.text = item.text
        }
    }

    inner class Type2ViewHolder(private val binding: ChatReceiverBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MessageData) {
            // Bind data to views
            binding.tvReceiver.text = item.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_1 -> {
                val binding = ChatSenderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                Type1ViewHolder(binding)
            }
            VIEW_TYPE_2 -> {
                val binding = ChatReceiverBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                Type2ViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_1 -> {
                val viewHolder = holder as Type1ViewHolder
                viewHolder.bind(chat1List[position])
            }
            VIEW_TYPE_2 -> {
                val viewHolder = holder as Type2ViewHolder
                viewHolder.bind(chat1List[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return chat1List.size
    }

    override fun getItemViewType(position: Int): Int {

        return if (chat1List[position].isSender){
            VIEW_TYPE_1
        } else {
            VIEW_TYPE_2
        }
    }
}
