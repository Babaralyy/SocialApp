package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.FragmentAboutProfileBinding
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Utils
import com.google.android.material.snackbar.Snackbar


class AboutProfileFragment : Fragment() {

    private  var activity: MainActivity? = null

    private var currentUser: UserLoginData? = null
    private lateinit var mBinding: FragmentAboutProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAboutProfileBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        currentUser = Utils.getUserFromSharedPreferences(requireContext())
        currentUser?.let {
            setUpData()
        }
        clickListeners()
    }

    private fun setUpData() {
        Glide
            .with(requireContext())
            .load(Constant.MEDIA_BASE_URL + currentUser?.profileImg)
            .placeholder(R.drawable.img)
            .into(mBinding.ivAboutProfileImage)

        mBinding.tvName.text = currentUser?.name
        mBinding.tvAbout.text = currentUser?.aboutMe
        mBinding.tvWeb.text = currentUser?.websiteUrl
    }


    private fun clickListeners() {
        val navController = findNavController()
        val userId = currentUser?.id.toString()

        val navigateWithUserId: (directions: NavDirections) -> Unit = { directions ->
            if (navController.currentDestination?.id == R.id.aboutProfileFragment) {
                Utils.userId = userId
                navController.navigate(directions)
            }
        }

        mBinding.ivAboutProfileImage.setOnClickListener {
            navigateWithUserId(AboutProfileFragmentDirections.actionAboutProfileFragmentToProfileDetailFragment())
        }

        mBinding.btnAddJourney.setOnClickListener {
            navigateWithUserId(AboutProfileFragmentDirections.actionAboutProfileFragmentToJourneyFragment())
        }

        mBinding.btnAddPlaylist.setOnClickListener {
            navigateWithUserId(AboutProfileFragmentDirections.actionAboutProfileFragmentToPlayListFragment())
        }

        mBinding.btnAddActivity.setOnClickListener {
            if (navController.currentDestination?.id == R.id.aboutProfileFragment) {
                Utils.userId = userId
                navController.navigate(AboutProfileFragmentDirections.actionAboutProfileFragmentToMainFragment())
                Utils.isFromProfile = true
            }
        }
    }


    private fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }
}