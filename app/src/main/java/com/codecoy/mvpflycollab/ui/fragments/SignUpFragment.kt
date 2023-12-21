package com.codecoy.mvpflycollab.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.FragmentSignUpBinding


class SignUpFragment : Fragment() {


    private lateinit var mBinding: FragmentSignUpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSignUpBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        mBinding.btnSignUp.setOnClickListener {
            try {
                val action = SignUpFragmentDirections.actionSignUpFragmentToInterestsFragment()
                findNavController().navigate(action)
            } catch (e: Exception){

            }

        }
    }

}