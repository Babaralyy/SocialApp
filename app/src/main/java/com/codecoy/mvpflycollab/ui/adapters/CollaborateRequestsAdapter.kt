package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.RequestsCallback
import com.codecoy.mvpflycollab.databinding.CollaborateRequestItemBinding
import com.codecoy.mvpflycollab.datamodels.FollowRequestsData
import com.codecoy.mvpflycollab.utils.Constant

class CollaborateRequestsAdapter(
    private val collaborateRequestList: MutableList<FollowRequestsData>,
    var context: Context,
    private var requestsCallback: RequestsCallback
) : RecyclerView.Adapter<CollaborateRequestsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CollaborateRequestItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reqData = collaborateRequestList[position]

        Glide
            .with(context)
            .load(Constant.MEDIA_BASE_URL + reqData.profileImg.toString())
            .placeholder(R.drawable.img)
            .into(holder.mBinding.ivShareItem)

        holder.mBinding.tvName.text = reqData.name

        holder.mBinding.tvAccept.setOnClickListener {
            requestsCallback.onAcceptColReq(reqData)
        }
        holder.mBinding.tvDecline.setOnClickListener {
            requestsCallback.onDeclineColReq(reqData)
        }
    }

    override fun getItemCount(): Int {
        return collaborateRequestList.size
    }

    fun setItemList(colReqList: ArrayList<FollowRequestsData>) {
        this.collaborateRequestList.clear()
        this.collaborateRequestList.addAll(colReqList)
        notifyDataSetChanged()
    }

    class ViewHolder(val mBinding: CollaborateRequestItemBinding) : RecyclerView.ViewHolder(mBinding.root)
}