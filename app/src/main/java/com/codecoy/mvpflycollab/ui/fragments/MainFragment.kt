package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.FragmentMainBinding
import com.codecoy.mvpflycollab.ui.activities.MainActivity



class MainFragment : Fragment() {

    private lateinit var activity: MainActivity

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
                    Toast.makeText(activity, "Pending", Toast.LENGTH_SHORT).show()
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

        /*
                mBinding.tvHome.isSelected = true
                mBinding.tvHome.isPressed = true


                mBinding.tvHome.setOnClickListener {


                    mBinding.progressBar.visibility = View.VISIBLE

                    Handler(Looper.getMainLooper()).postDelayed({


                        activity.replaceFragment(HomeFragment())

                        mBinding.tvHome.isSelected = true
                        mBinding.tvHome.isPressed = true

                        mBinding.tvCalender.isSelected = false
                        mBinding.tvCalender.isPressed = false

                        mBinding.tvSave.isSelected = false
                        mBinding.tvSave.isPressed = false

                        mBinding.tvProfile.isSelected = false
                        mBinding.tvProfile.isPressed = false


                        mBinding.progressBar.visibility = View.GONE

                    }, 500)




                }


                mBinding.tvCalender.setOnClickListener {


                    mBinding.progressBar.visibility = View.VISIBLE

                    Handler(Looper.getMainLooper()).postDelayed({

                        activity.replaceFragment(CalendarFragment())

                        mBinding.tvCalender.isSelected = true
                        mBinding.tvCalender.isPressed = true

                        mBinding.tvSave.isSelected = false
                        mBinding.tvSave.isPressed = false

                        mBinding.tvProfile.isSelected = false
                        mBinding.tvProfile.isPressed = false

                        mBinding.tvHome.isSelected = false
                        mBinding.tvHome.isPressed = false

                        mBinding.progressBar.visibility = View.GONE

                    }, 500)




                }

                mBinding.tvSave.setOnClickListener {


                    mBinding.tvSave.isSelected = true
                    mBinding.tvSave.isPressed = true

                    mBinding.tvCalender.isSelected = false
                    mBinding.tvCalender.isPressed = false

                    mBinding.tvProfile.isSelected = false
                    mBinding.tvProfile.isPressed = false

                    mBinding.tvHome.isSelected = false
                    mBinding.tvHome.isPressed = false


                }


                mBinding.tvProfile.setOnClickListener {


                    mBinding.progressBar.visibility = View.VISIBLE

                    Handler(Looper.getMainLooper()).postDelayed({

                        activity.replaceFragment(ProfileFragment())

                        mBinding.tvSave.isSelected = false
                        mBinding.tvSave.isPressed = false

                        mBinding.tvCalender.isSelected = false
                        mBinding.tvCalender.isPressed = false

                        mBinding.tvHome.isSelected = false
                        mBinding.tvHome.isPressed = false

                        mBinding.tvProfile.isSelected = true
                        mBinding.tvProfile.isPressed = true

                        mBinding.progressBar.visibility = View.GONE

                    }, 500)




                }
        */

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }

}