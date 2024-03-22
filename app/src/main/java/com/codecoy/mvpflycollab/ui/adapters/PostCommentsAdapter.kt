package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.CommentItemViewBinding
import com.codecoy.mvpflycollab.datamodels.CommentsData
import com.codecoy.mvpflycollab.utils.Constant

class PostCommentsAdapter(
    private val commentsList: MutableList<CommentsData>,
    var context: Context
 ) : RecyclerView.Adapter<PostCommentsAdapter.ViewHolder>() {

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         return ViewHolder(CommentItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
     }

     override fun onBindViewHolder(holder: ViewHolder, position: Int) {

         val comment = commentsList[position]

         Glide
             .with(context)
             .load(Constant.MEDIA_BASE_URL + comment.userData?.profileImg)
             .placeholder(R.drawable.img)
             .into(holder.mBinding.ivProfile)

         holder.mBinding.tvName.text = comment.userData?.name
         holder.mBinding.tvComment.text = comment.commentTitle
     }

     override fun getItemCount(): Int {
         return commentsList.size
     }

     class ViewHolder(val mBinding: CommentItemViewBinding) : RecyclerView.ViewHolder(mBinding.root)
 }