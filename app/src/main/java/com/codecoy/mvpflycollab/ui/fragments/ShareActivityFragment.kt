package com.codecoy.mvpflycollab.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.FragmentShareActivityBinding


class ShareActivityFragment : Fragment() {


    private lateinit var mBinding: FragmentShareActivityBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentShareActivityBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

    }

}