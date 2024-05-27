package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.FragmentAboutProfileBinding
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.utils.Utils
import com.google.android.material.snackbar.Snackbar


class AboutProfileFragment : Fragment() {

//    private lateinit var activity: MainActivity

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
        clickListeners()
        setUpData()
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
        mBinding.ivAboutProfileImage.setOnClickListener {
            try {
                val action =
                    AboutProfileFragmentDirections.actionAboutProfileFragmentToProfileDetailFragment()
                findNavController().navigate(action)

            } catch (e: Exception) {
                showSnackBar(mBinding.root, e.message.toString())
            }
        }

        mBinding.btnAddJourney.setOnClickListener {
            try {
                Utils.userId = currentUser?.id.toString()
                val action =
                    AboutProfileFragmentDirections.actionAboutProfileFragmentToJourneyFragment()
                findNavController().navigate(action)

            } catch (e: Exception) {
                showSnackBar(mBinding.root, e.message.toString())
            }
        }

        mBinding.btnAddPlaylist.setOnClickListener {
            try {
                Utils.userId = currentUser?.id.toString()
                val action =
                    AboutProfileFragmentDirections.actionAboutProfileFragmentToPlayListFragment()
                findNavController().navigate(action)

            } catch (e: Exception) {
                showSnackBar(mBinding.root, e.message.toString())
            }
        }

        mBinding.btnAddActivity.setOnClickListener {
            try {
                Utils.userId = currentUser?.id.toString()
                findNavController().navigate(AboutProfileFragmentDirections.actionAboutProfileFragmentToMainFragment())
                Utils.isFromProfile = true
            } catch (e: Exception) {
                showSnackBar(mBinding.root, e.message.toString())
            }
        }
    }

    private fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

/*    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }*/
}