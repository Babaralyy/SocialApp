package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.BottomsheetVideonewplaylistBinding
import com.codecoy.mvpflycollab.databinding.FragmentPlayListDetailBinding
import com.codecoy.mvpflycollab.datamodels.PlaylistDetailData
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.playlist.PlaylistDetailAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog


class PlayListDetailFragment : Fragment() {

    private lateinit var activity: MainActivity

    private lateinit var playlistDetailAdapter: PlaylistDetailAdapter
    private lateinit var playDetailList: MutableList<PlaylistDetailData>


    private lateinit var mBinding: FragmentPlayListDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPlayListDetailBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        playDetailList = arrayListOf()

        mBinding.rvPlayListDetail.layoutManager = LinearLayoutManager(activity)
        mBinding.rvPlayListDetail.setHasFixedSize(true)

        setUpData()


        mBinding.floatingActionButton3.setOnClickListener{
            showBottomDialog()
        }

    }


    private fun showBottomDialog() {
        val dialogBinding = BottomsheetVideonewplaylistBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(activity)
        bottomSheetDialog.setContentView(dialogBinding.root)

        bottomSheetDialog.show()
    }


    private fun setUpData() {
        playDetailList.add(
            PlaylistDetailData("https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/Big_Buck_Bunny_-_forest.jpg/800px-Big_Buck_Bunny_-_forest.jpg",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
        )
        playDetailList.add(PlaylistDetailData("https://images.unsplash.com/photo-1488426862026-3ee34a7d66df?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8bW9kZWwlMjBpbWFnZXxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60",
            "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"))
        playDetailList.add(PlaylistDetailData("https://storage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerJoyrides.jpg","https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"))

        playlistDetailAdapter = PlaylistDetailAdapter(playDetailList, activity)
        mBinding.rvPlayListDetail.adapter = playlistDetailAdapter

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }

}