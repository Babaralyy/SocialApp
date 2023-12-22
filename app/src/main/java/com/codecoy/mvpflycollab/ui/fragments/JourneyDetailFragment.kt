package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.AddJourneyPicBottomDialogLayBinding
import com.codecoy.mvpflycollab.databinding.FragmentJouneyDetailBinding
import com.codecoy.mvpflycollab.databinding.NewJourneyBottomDialogLayBinding
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.journey.JourneyDetailAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog


class JourneyDetailFragment : Fragment() {


    private lateinit var activity: MainActivity
    private lateinit var journeyDetailAdapter: JourneyDetailAdapter
    private lateinit var journeyDetailList: MutableList<String>

    private lateinit var mBinding: FragmentJouneyDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentJouneyDetailBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        journeyDetailList = arrayListOf()

        mBinding.rvJourneyDetail.layoutManager = LinearLayoutManager(activity)

        setUpData()

        mBinding.floatingActionButton.setOnClickListener {
            showAddJourneyImageBottomDialog()
        }


    }

    private fun showAddJourneyImageBottomDialog() {

        val bottomBinding = AddJourneyPicBottomDialogLayBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(activity)
        bottomSheetDialog.setContentView(bottomBinding.root)

        bottomSheetDialog.show()
    }

    private fun setUpData() {

        journeyDetailList.add("")
        journeyDetailList.add("")
        journeyDetailList.add("")
        journeyDetailList.add("")

        journeyDetailAdapter = JourneyDetailAdapter(journeyDetailList, activity)
        mBinding.rvJourneyDetail.adapter = journeyDetailAdapter

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }
}