package com.codecoy.mvpflycollab.ui.adapters.interest

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.mvpflycollab.databinding.InterestItemViewBinding
import com.codecoy.mvpflycollab.utils.Constant
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager

class InterestsAdapter(
    private var interestsList: MutableList<String>,
    var context: Context
) :
    RecyclerView.Adapter<InterestsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            InterestItemViewBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.mBinding.tvMain.text = interestsList[position]


        Log.i(Constant.TAG, "setDataList:: ${interestsList[position]}")

        val subInterestsList = arrayListOf<String>()

        subInterestsList.add("Running")
        subInterestsList.add("Cycling")
        subInterestsList.add("ABC")
        subInterestsList.add("Jump rope")
        subInterestsList.add("Aerobics")
        subInterestsList.add("Dancing")
        subInterestsList.add("Functional fitness workouts")

        val adapter = SubInterestsAdapter(subInterestsList, context)

        val flexLayoutManager = FlexboxLayoutManager(context)
        flexLayoutManager.flexDirection = FlexDirection.ROW

        holder.mBinding.subInterestRecyclerView.layoutManager = flexLayoutManager
        holder.mBinding.subInterestRecyclerView.adapter = adapter

    }

    override fun getItemCount(): Int {
        return interestsList.size
    }

    class ViewHolder(val mBinding: InterestItemViewBinding) :
        RecyclerView.ViewHolder(mBinding.root)
}

