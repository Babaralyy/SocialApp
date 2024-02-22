package com.codecoy.mvpflycollab.callbacks.playlistdetailsvideo

import android.net.Uri

interface VideoClickCallback {
    fun onVideoClick(videoPath: Uri)
}