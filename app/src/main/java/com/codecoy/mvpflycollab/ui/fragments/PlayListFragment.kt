package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.PlaylistCallback
import com.codecoy.mvpflycollab.databinding.BottomsheetNewplaylistBinding
import com.codecoy.mvpflycollab.databinding.FragmentPlayListBinding
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.playlist.PlayListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog


class PlayListFragment : Fragment(), PlaylistCallback {

    private lateinit var activity: MainActivity
    private lateinit var playListAdapter: PlayListAdapter
    private lateinit var playList: MutableList<String>

    private lateinit var mBinding: FragmentPlayListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPlayListBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        playList = arrayListOf()
        mBinding.rvPlayList.layoutManager = GridLayoutManager(activity, 2)
        mBinding.rvPlayList.setHasFixedSize(true)

        setUpAdapter()

        mBinding.floatingActionButton.setOnClickListener {
            showBottomDialog()
        }

    }

    private fun showBottomDialog() {
        val bottomBinding = BottomsheetNewplaylistBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomBinding.root)

        bottomSheetDialog.show()
    }

    private fun setUpAdapter() {

        playList.clear()

        playList.add("")
        playList.add("")
        playList.add("")
        playList.add("")
        playList.add("")
        playList.add("")
        playList.add("")
        playList.add("")

        playListAdapter = PlayListAdapter(playList, activity, this)
        mBinding.rvPlayList.adapter = playListAdapter

    }

    override fun onPlaylistClick() {
        try {

            val action = PlayListFragmentDirections.actionPlayListFragmentToPlayListDetailFragment()
            findNavController().navigate(action)

        }catch (e: Exception){

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }


}