package com.codecoy.mvpflycollab.ui.adapters.members

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.MembersCallback
import com.codecoy.mvpflycollab.databinding.MembersItemViewBinding
import com.codecoy.mvpflycollab.datamodels.CollaboratorsListData
import com.codecoy.mvpflycollab.utils.Constant

class MembersAdapter (
    private val membersList: MutableList<CollaboratorsListData>,
    private var context: Context,
    private var membersCallback: MembersCallback,
)

    : RecyclerView.Adapter<MembersAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MembersItemViewBinding.inflate(LayoutInflater.from(context), parent,false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val memberData = membersList[position]


        Glide
            .with(context)
            .load(Constant.MEDIA_BASE_URL + memberData.profileImg)
            .placeholder(R.drawable.img)
            .into(holder.mBinding.ivItemMember)

        holder.mBinding.tvName.text = memberData.name

        holder.itemView.setOnClickListener{
            membersCallback.onMemberClick(memberData)
        }
    }

    override fun getItemCount(): Int {
        return membersList.size
    }
    class ViewHolder(val mBinding: MembersItemViewBinding): RecyclerView.ViewHolder(mBinding.root)
}
