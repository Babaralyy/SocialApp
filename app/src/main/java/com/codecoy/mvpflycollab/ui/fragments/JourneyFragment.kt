package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.callbacks.JourneyCallback
import com.codecoy.mvpflycollab.databinding.FragmentJourneyBinding
import com.codecoy.mvpflycollab.databinding.NewJourneyBottomDialogLayBinding
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.journey.JourneyAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

class JourneyFragment : Fragment(), JourneyCallback {

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

        mBinding.floatingActionButton.setOnClickListener {

            showAddJourneyBottomDialog()

        }

    }

    private fun showAddJourneyBottomDialog() {

        val bottomBinding = NewJourneyBottomDialogLayBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(activity)
        bottomSheetDialog.setContentView(bottomBinding.root)

        bottomSheetDialog.show()
    }

    private fun setUpAdapter() {
        journeyList.clear()

        journeyList.add("")
        journeyList.add("")
        journeyList.add("")
        journeyList.add("")
        journeyList.add("")
        journeyList.add("")

        journeyAdapter = JourneyAdapter(journeyList, activity, this)
        mBinding.rvJourney.adapter = journeyAdapter

    }

    override fun onJourneyClick() {
        try {

            val action = JourneyFragmentDirections.actionJourneyFragmentToJourneyDetailFragment()
            findNavController().navigate(action)

        }catch (e: Exception){

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }

}