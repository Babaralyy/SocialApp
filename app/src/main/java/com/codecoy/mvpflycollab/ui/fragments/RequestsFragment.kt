package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.FragmentRequestsBinding
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.CollaborateRequestsAdapter
import com.codecoy.mvpflycollab.ui.adapters.FollowRequestsAdapter


class RequestsFragment : Fragment() {

    private lateinit var activity: MainActivity
    private lateinit var collaborateRequestsAdapter: CollaborateRequestsAdapter
    private lateinit var followRequestsAdapter: FollowRequestsAdapter

    private lateinit var colList: MutableList<String>
    private lateinit var followList: MutableList<String>


    private lateinit var mBinding: FragmentRequestsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentRequestsBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        colList = arrayListOf()
        followList = arrayListOf()

        mBinding.rvCollabrate.layoutManager = LinearLayoutManager(activity)
        mBinding.rvFollower.layoutManager = LinearLayoutManager(activity)

        setColAdapter()
        setFollowAdapter()
    }

    private fun setColAdapter() {
        colList.clear()

        colList.add("")
        colList.add("")
        colList.add("")
        colList.add("")
        colList.add("")
        colList.add("")
        colList.add("")

        collaborateRequestsAdapter = CollaborateRequestsAdapter(colList, activity)
        mBinding.rvCollabrate.adapter =  collaborateRequestsAdapter
    }

    private fun setFollowAdapter() {
        followList.clear()

        followList.add("")
        followList.add("")
        followList.add("")
        followList.add("")
        followList.add("")
        followList.add("")
        followList.add("")

        followRequestsAdapter = FollowRequestsAdapter(followList, activity)
        mBinding.rvFollower.adapter =  followRequestsAdapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }
}