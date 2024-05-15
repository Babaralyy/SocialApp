package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.util.Log
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.SHOW_BUFFERING_ALWAYS
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

class ImageSliderAdapter(private val context: Context, private val images: ArrayList<UserPostsImages>) : PagerAdapter() {

    private var player: ExoPlayer
    init {
         player = ExoPlayer.Builder(context).build()
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    @OptIn(UnstableApi::class)
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(context)
        val playerView = PlayerView(context)
        playerView.setShowBuffering(SHOW_BUFFERING_ALWAYS)
        playerView.useController = false




        Log.i(TAG, "instantiateItem:: $images")
        if (images[position].type == "img"){
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
        container.removeView(`object` as View)
    }
}