package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.RequestsCallback
import com.codecoy.mvpflycollab.databinding.FollowRequestItemBinding
import com.codecoy.mvpflycollab.datamodels.FollowRequestsData
import com.codecoy.mvpflycollab.utils.Constant

class FollowRequestsAdapter(
    private val followRequestList: MutableList<FollowRequestsData>,
    var context: Context,
    private var requestsCallback: RequestsCallback
) : RecyclerView.Adapter<FollowRequestsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FollowRequestItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val reqData = followRequestList[position]

        Glide
            .with(context)
            .load(Constant.MEDIA_BASE_URL + reqData.profileImg.toString())
            .placeholder(R.drawable.img)
            .into(holder.mBinding.ivShareItem)

        holder.mBinding.tvName.text = reqData.name

        holder.mBinding.tvAccept.setOnClickListener {
            requestsCallback.onAcceptFollowReq(reqData)
        }
        holder.mBinding.tvDecline.setOnClickListener {
            requestsCallback.onDeclineFollowReq(reqData)
        }
    }

    override fun getItemCount(): Int {
        return followRequestList.size
    }

    fun setItemList(followReqList: ArrayList<FollowRequestsData>) {
        this.followRequestList.clear()
        this.followRequestList.addAll(followReqList)
        notifyDataSetChanged()
    }

    class ViewHolder(val mBinding: FollowRequestItemBinding) : RecyclerView.ViewHolder(mBinding.root)
}