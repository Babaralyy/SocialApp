package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.callbacks.ShareActivityCallback
import com.codecoy.mvpflycollab.databinding.BottomsheetCalendarBinding
import com.codecoy.mvpflycollab.databinding.BottomsheetShareCalendarBinding
import com.codecoy.mvpflycollab.databinding.FragmentCalendarBinding
import com.codecoy.mvpflycollab.databinding.ShareWithMemnersDialogLayBinding
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.CalendarAdapter
import com.codecoy.mvpflycollab.ui.adapters.ShareActivityAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog


class CalendarFragment : Fragment(), ShareActivityCallback {

    private lateinit var activity: MainActivity

    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var shareActivityAdapter: ShareActivityAdapter

    private lateinit var eventsList: MutableList<String>
    private lateinit var shareActivityList: MutableList<String>

    private var shareBinding: ShareWithMemnersDialogLayBinding? = null
    private var shareBottomSheetDialog: BottomSheetDialog? = null

    private lateinit var mBinding: FragmentCalendarBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCalendarBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        shareBinding = ShareWithMemnersDialogLayBinding.inflate(layoutInflater)
        shareBottomSheetDialog = BottomSheetDialog(activity)
        shareBinding?.root?.let { shareBottomSheetDialog?.setContentView(it) }

        eventsList = arrayListOf()
        shareActivityList = arrayListOf()

        mBinding.rvEvents.layoutManager = LinearLayoutManager(activity)
        mBinding.rvEvents.setHasFixedSize(true)

        shareBinding?.rvActivityMembers?.layoutManager = LinearLayoutManager(activity)
        shareBinding?.rvActivityMembers?.setHasFixedSize(true)

        setUpRecyclerView()

        mBinding.floatingActionButton.setOnClickListener {
            showAddActivityBottomDialog()
        }

    }

    private fun showAddActivityBottomDialog() {

        val bottomBinding = BottomsheetCalendarBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(activity)
        bottomSheetDialog.setContentView(bottomBinding.root)

        bottomBinding.tvTitle.setOnClickListener {
//            startActivity(Intent(this@CalendarActivity,ShowStoriesActivity::class.java))
        }

        bottomSheetDialog.show()
    }


    private fun setUpRecyclerView() {

        eventsList.add("")
        eventsList.add("")
        eventsList.add("")
        eventsList.add("")

        calendarAdapter = CalendarAdapter(eventsList, activity, this)
        mBinding.rvEvents.adapter = calendarAdapter

    }

    override fun onShareActivityClick() {
        showShareActivityBottomDialog()
    }

    private fun showShareActivityBottomDialog() {

        val bottomBinding = BottomsheetShareCalendarBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(activity)
        bottomSheetDialog.setContentView(bottomBinding.root)

        bottomBinding.ivShare.setOnClickListener {
            bottomSheetDialog.dismiss()
            showShareWithMembersDialog()
        }


        bottomSheetDialog.show()
    }

    private fun showShareWithMembersDialog() {

        shareActivityList.clear()

        shareActivityList.add("")
        shareActivityList.add("")
        shareActivityList.add("")
        shareActivityList.add("")

        shareActivityAdapter = ShareActivityAdapter(shareActivityList, activity)
        shareBinding?.rvActivityMembers?.adapter = shareActivityAdapter

        shareBottomSheetDialog?.show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }


}