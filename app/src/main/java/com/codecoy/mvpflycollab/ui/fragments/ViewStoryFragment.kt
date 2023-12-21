package com.codecoy.mvpflycollab.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.FragmentViewStoryBinding


class ViewStoryFragment : Fragment() {

    private lateinit var mBinding: FragmentViewStoryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentViewStoryBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        mBinding.btnnavigation.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}