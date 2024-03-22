package com.codecoy.mvpflycollab.callbacks

import android.net.Uri

interface ImageClickCallback {
    fun onImageClick(imgPath: Uri)
    fun onImgRemove(position: Int){}
}