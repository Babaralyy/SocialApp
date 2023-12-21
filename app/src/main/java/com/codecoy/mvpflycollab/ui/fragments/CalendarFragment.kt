package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.databinding.BottomsheetCalendarBinding
import com.codecoy.mvpflycollab.databinding.FragmentCalendarBinding
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.CalendarAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog


class CalendarFragment : Fragment() {

    private lateinit var activity: MainActivity

    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var eventsList: MutableList<String>

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

        eventsList = arrayListOf()

        mBinding.rvEvents.layoutManager = LinearLayoutManager(activity)
        mBinding.rvEvents.setHasFixedSize(true)

        setUpRecyclerView()

        mBinding.floatingActionButton.setOnClickListener {
            showBottomDialog()
        }

    }

    private fun showBottomDialog() {
        val bottomBinding = BottomsheetCalendarBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(activity)
        bottomSheetDialog.setContentView(bottomBinding.root)

        bottomBinding.tvTitle.setOnClickListener{
//            startActivity(Intent(this@CalendarActivity,ShowStoriesActivity::class.java))
        }

        bottomSheetDialog.show()
    }

    private fun setUpRecyclerView() {

        eventsList.add("")
        eventsList.add("")
        eventsList.add("")
        eventsList.add("")

        calendarAdapter = CalendarAdapter(eventsList, activity)
        mBinding.rvEvents.adapter = calendarAdapter

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }
}