package com.codecoy.mvpflycollab.ui.adapters.stories

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.StoryCallback
import com.codecoy.mvpflycollab.databinding.StoriesItemViewBinding
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.squareup.picasso.Picasso


class StoriesAdapter (
    private val storiesList: MutableList<String>,
    private var context: Context,
    private var storyCallback: StoryCallback
) : RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(StoriesItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storyData = storiesList[position]


        Glide
            .with(context)
            .load(storyData)
            .placeholder(R.drawable.img)
            .into(holder.mBinding.ivStoryImage)


        Log.i(TAG, "onBindViewHolder:: $storyData")





//        Picasso.get()
//            .load(uri)
//            .placeholder(R.drawable.img)
//            .into(holder.mBinding.ivStoryImage)

        holder.mBinding.tvStoryName.text="Name:$position"

        holder.mBinding.ivStoryImage.setOnClickListener {
            storyCallback.onStoryClick()
        }
    }

    override fun getItemCount(): Int {
        return storiesList.size
    }

    class ViewHolder(val mBinding: StoriesItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)
}