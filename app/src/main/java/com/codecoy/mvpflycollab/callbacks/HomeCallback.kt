package com.codecoy.mvpflycollab.callbacks

import com.codecoy.mvpflycollab.databinding.PostItemViewBinding
import com.codecoy.mvpflycollab.datamodels.UserPostsData

interface HomeCallback {
    fun onMenuClick(postsData: UserPostsData, mBinding: PostItemViewBinding)
    fun onCommentsClick(postsData: UserPostsData)
    fun onLikeClick(postsData: UserPostsData, mBinding: PostItemViewBinding)
    fun onUserClick(postsData: UserPostsData, mBinding: PostItemViewBinding)
    fun onSaveClick(postsData: UserPostsData, mBinding: PostItemViewBinding)
}