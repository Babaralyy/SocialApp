package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.databinding.FragmentJourneyBinding
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.JourneyAdapter

class JourneyFragment : Fragment() {

    private lateinit var activity: MainActivity
    private lateinit var journeyAdapter: JourneyAdapter
    private lateinit var journeyList: MutableList<String>

    private lateinit var mBinding: FragmentJourneyBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentJourneyBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        journeyList = arrayListOf()

        mBinding.rvJourney.layoutManager = LinearLayoutManager(activity)
        mBinding.rvJourney.setHasFixedSize(true)

        setUpAdapter()

    }

    private fun setUpAdapter() {
        journeyList.clear()

        journeyList.add("")
        journeyList.add("")
        journeyList.add("")
        journeyList.add("")
        journeyList.add("")
        journeyList.add("")

        journeyAdapter = JourneyAdapter(journeyList, activity)
        mBinding.rvJourney.adapter = journeyAdapter

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }

}