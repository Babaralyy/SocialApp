package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.databinding.CollaborateRequestItemBinding

class CollaborateRequestsAdapter(
    private val collaborateRequestList: MutableList<String>,
    var context: Context,
) : RecyclerView.Adapter<CollaborateRequestsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CollaborateRequestItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return collaborateRequestList.size
    }

    class ViewHolder(val mBinding: CollaborateRequestItemBinding) : RecyclerView.ViewHolder(mBinding.root)
}