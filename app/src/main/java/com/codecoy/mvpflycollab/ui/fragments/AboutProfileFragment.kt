package com.codecoy.mvpflycollab.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.FragmentAboutProfileBinding


class AboutProfileFragment : Fragment() {

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
        mBinding.ivAboutProfileImage.setOnClickListener {
            try {
                val action = MainFragmentDirections.actionMainFragmentToProfileDetailFragment()
                findNavController().navigate(action)
            }catch (e: Exception){

            }
        }
    }

}