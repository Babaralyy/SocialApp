package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.BottomsheetCalendarBinding
import com.codecoy.mvpflycollab.databinding.FragmentMainBinding
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialog


class MainFragment : Fragment() {

    private lateinit var activity: MainActivity

    private var bottomBinding: BottomsheetCalendarBinding? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    private lateinit var mBinding: FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMainBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        activity.replaceFragment(HomeFragment())

        setUpBottomDialog()

        mBinding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    activity.replaceFragment(HomeFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_calendar -> {
                    activity.replaceFragment(CalendarFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_add_post -> {
                    showAddPostBottomDialog()
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_save_post -> {
                    activity.replaceFragment(SavedPostFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_notification -> {
                    activity.replaceFragment(NotificationsFragment())
                    return@setOnItemSelectedListener true
                }

            }
            return@setOnItemSelectedListener true
        }


    }

    private fun setUpBottomDialog() {
        bottomBinding = BottomsheetCalendarBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(activity)
        bottomBinding?.root?.let { bottomSheetDialog?.setContentView(it) }

        bottomBinding?.rvMediaImage?.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        bottomBinding?.rvMediaVideo?.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun showAddPostBottomDialog() {
        bottomSheetDialog?.show()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }

}