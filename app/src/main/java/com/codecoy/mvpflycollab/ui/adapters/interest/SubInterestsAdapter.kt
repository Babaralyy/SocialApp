package com.codecoy.mvpflycollab.ui.adapters.interest

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.databinding.SubInterestsItemViewBinding


class SubInterestsAdapter(
    private val subInterestsList: ArrayList<String>,
    private var context: Context
) : RecyclerView.Adapter<SubInterestsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SubInterestsItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mBinding.tvTitle.text = subInterestsList[position]
    }

    override fun getItemCount(): Int {
        return subInterestsList.size
    }

    class ViewHolder(val mBinding: SubInterestsItemViewBinding) :
        RecyclerView.ViewHolder(mBinding.root)

}

