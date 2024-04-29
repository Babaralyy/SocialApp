package com.codecoy.mvpflycollab.ui.fragments

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.FragmentVideoPreviewBinding
import com.codecoy.mvpflycollab.databinding.ShowVideoDialogBinding
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class VideoPreviewFragment : Fragment() {

    private lateinit var activity: MainActivity
    private lateinit var mBinding: FragmentVideoPreviewBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentVideoPreviewBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        playVideo(Utils.videoUrl)
    }

    private fun playVideo( videoUrl: String? = null) {
            val player = ExoPlayer.Builder(requireContext()).build()
            mBinding.videoPlayer.player = player
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val mediaItem = withContext(Dispatchers.IO) {
                        MediaItem.fromUri(videoUrl.toString())
                    }
                    withContext(Dispatchers.Main) {
                        player.setMediaItem(mediaItem)
                        player.repeatMode = Player.REPEAT_MODE_ONE
                        player.prepare()
                        player.play()
                    }
                } catch (e: Exception) {
                    Log.e(Constant.TAG, "Error preparing video playback: ${e.message}")
                }
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }

}