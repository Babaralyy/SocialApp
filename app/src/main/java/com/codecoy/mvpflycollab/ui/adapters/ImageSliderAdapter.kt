package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.datamodels.UserPostsImages
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Constant.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageSliderAdapter(
    private val context: Context,
    private val images: ArrayList<UserPostsImages>
) : PagerAdapter() {
    private val players = SparseArray<ExoPlayer>()
    var currentPlayingPosition = -1
    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    @OptIn(UnstableApi::class)
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val player: ExoPlayer = ExoPlayer.Builder(context).build()
        players.put(position, player)

        val imageView = ImageView(context)
        val playerView = PlayerView(context)
        playerView.useController = false
        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)


        playerView.player = player

        Log.i(TAG, "destroyItem::  $position")

        Log.i(TAG, "instantiateItem:: $images")
        if (images[position].type == "img") {

            Glide
                .with(context)
                .load(Constant.MEDIA_BASE_URL + images[position].postImg)
                .placeholder(R.drawable.loading_svg)
                .into(imageView)

            container.addView(imageView)
            return imageView
        } else {

            CoroutineScope(Dispatchers.IO).launch {

                try {
                    val mediaItem = withContext(Dispatchers.IO) {
                        MediaItem.fromUri(Constant.MEDIA_BASE_URL + images[position].postImg.toString())
                    }
                    withContext(Dispatchers.Main) {
                        player.setMediaItem(mediaItem)
                        player.repeatMode = Player.REPEAT_MODE_ONE
                        player.prepare()
                        player.play()

                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error preparing video playback: ${e.message}")
                }
            }

            container.addView(playerView)
            return playerView
        }
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        Log.i(TAG, "destroyItem:: removeView  $position")
        container.removeView(`object` as View)

        // Release the ExoPlayer resource
        players[position]?.let { player ->
            player.stop()
            player.release()
            players.remove(position)
        }

        // Resume the player of the new primary item if it's a video
//        if (images[position].type != "img") {
//            players[position]?.let { player ->
//                player.playWhenReady = true
//                player.play()
//            }
//        }


    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        super.setPrimaryItem(container, position, `object`)

        // Pause previous players when a new item is in focus
//        for (i in 0 until players.size()) {
//            if (i != position) {
//                players[i]?.playWhenReady = false
//            }
//        }

        currentPlayingPosition = position
    }

    fun pausePlayer(position: Int) {
        players[position]?.let { player ->
            player.playWhenReady = false
            player.pause()
        }
    }

    fun playPlayer(position: Int) {
        players[position]?.let { player ->
            player.playWhenReady = true
            player.play()
        }
    }

    fun releaseAllPlayers() {
        for (i in 0 until players.size()) {
            players.valueAt(i)?.let { player ->
                player.stop()
                player.release()
            }
        }
        players.clear()
    }

}