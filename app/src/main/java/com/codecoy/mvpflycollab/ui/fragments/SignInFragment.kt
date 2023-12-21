package com.codecoy.mvpflycollab.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.FragmentSignInBinding


class SignInFragment : Fragment() {

    private lateinit var mBinding: FragmentSignInBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSignInBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        mBinding.btnSignIn.setOnClickListener {
            try {
                val action = SignInFragmentDirections.actionSignInFragmentToInterestsFragment()
                findNavController().navigate(action)

            } catch (e: Exception){

            }
        }
    }

}