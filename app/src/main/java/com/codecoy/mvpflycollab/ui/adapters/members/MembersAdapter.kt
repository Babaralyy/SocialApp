package com.codecoy.mvpflycollab.ui.adapters.members

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.MembersItemViewBinding
import com.codecoy.mvpflycollab.utils.Constant.TAG

class MembersAdapter (
    private val membersList: MutableList<String>,
    private var context: Context)

    : RecyclerView.Adapter<MembersAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MembersItemViewBinding.inflate(LayoutInflater.from(context), parent,false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val memberData = membersList[position]


        Glide
            .with(context)
            .load(memberData)
            .placeholder(R.drawable.img)
            .into(holder.mBinding.ivItemMember)


        Log.i(TAG, "memberData:: $memberData ")

        holder.itemView.setOnClickListener{
//            val intent = Intent(context, CalendarActivity::class.java)
//            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return membersList.size
    }
    class ViewHolder(val mBinding: MembersItemViewBinding): RecyclerView.ViewHolder(mBinding.root)
}
