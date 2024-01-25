package com.codecoy.mvpflycollab.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.FragmentProfileBinding
import com.codecoy.mvpflycollab.ui.adapters.ViewPagerAdapter
import com.codecoy.mvpflycollab.utils.Constant.TAG


class ProfileFragment : Fragment() {


    private lateinit var mBinding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentProfileBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {


        openDrawer()

        val navigationView = mBinding.drawerNavigation
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
//                R.id.logout -> {
//                    // Handle logout action here
//                    Toast.makeText(activity, "Logout", Toast.LENGTH_SHORT).show()
//                    // Close the drawer
//                    mBinding.drawerLayout.closeDrawer(GravityCompat.END)
//                    true
//                }
//
//                R.id.iLevels -> {
//
//                    try {
//                        val action = MainFragmentDirections.actionMainFragmentToLevelsFragment()
//                        findNavController().navigate(action)
//                    }catch (e: Exception){
//
//                    }
//
//                    // Close the drawer
//                    mBinding.drawerLayout.closeDrawer(GravityCompat.END)
//                    true
//                }
//
//                R.id.iEdit -> {
//
//                    try {
//                        val action = MainFragmentDirections.actionMainFragmentToEditProfileFragment()
//                        findNavController().navigate(action)
//                    }catch (e: Exception){
//
//                    }
//
//                    // Close the drawer
//                    mBinding.drawerLayout.closeDrawer(GravityCompat.END)
//                    true
//                }

                // Handle other menu items if needed
                else -> false
            }

        }


    }

    override fun onResume() {
        super.onResume()
        setUpViewPager()
    }

    private fun setUpViewPager() {


        // Clear existing tabs
        mBinding.tabLayout.removeAllTabs()
        // Clear any selected tab
        mBinding.tabLayout.clearOnTabSelectedListeners()

        val pagerAdapter = ViewPagerAdapter(requireActivity().supportFragmentManager)
        pagerAdapter.addFragment(AboutProfileFragment(), "Profile")
        pagerAdapter.addFragment(JourneyFragment(), "Journey")
        pagerAdapter.addFragment(PlayListFragment(), "Playlist")
        pagerAdapter.addFragment(MembersFragment(), "Collab")
        mBinding.viewPager.adapter = pagerAdapter
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager)

        try {
            mBinding.tabLayout.getTabAt(0)?.select()
        } catch (e: Exception){
            Log.i(TAG, "setUpViewPager:: ${e.message} ")
        }


    }

    private fun openDrawer() {
        mBinding.ivDrawer.setOnClickListener{
            mBinding.drawerLayout.openDrawer(GravityCompat.END)
        }
        mBinding.drawerLayout.addDrawerListener(object :
            androidx.drawerlayout.widget.DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {}

            override fun onDrawerStateChanged(newState: Int) {
                if (newState == androidx.drawerlayout.widget.DrawerLayout.STATE_SETTLING) {
                    if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                        mBinding.drawerLayout.closeDrawer(GravityCompat.END)
                    }
                }
            }
        })
    }

}