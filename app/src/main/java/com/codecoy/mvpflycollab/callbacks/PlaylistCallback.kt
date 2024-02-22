package com.codecoy.mvpflycollab.callbacks

import com.codecoy.mvpflycollab.datamodels.AllPlaylistData

interface PlaylistCallback {
    fun onPlaylistClick(playData: AllPlaylistData)
}