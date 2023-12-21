package com.codecoy.mvpflycollab.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.codecoy.mvpflycollab.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private lateinit var mBinding: FragmentWelcomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentWelcomeBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        mBinding.btnLogin.setOnClickListener {
            try {
                val action = WelcomeFragmentDirections.actionWelcomeFragmentToSignInFragment()
                findNavController().navigate(action)
            }catch (e: Exception){

            }
        }

        mBinding.btnSignUp.setOnClickListener {
            try {
                val action = WelcomeFragmentDirections.actionWelcomeFragmentToSignUpFragment()
                findNavController().navigate(action)
            }catch (e: Exception){

            }
        }

    }

}