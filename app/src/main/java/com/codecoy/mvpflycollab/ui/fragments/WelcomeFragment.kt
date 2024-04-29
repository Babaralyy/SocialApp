package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.codecoy.mvpflycollab.databinding.FragmentWelcomeBinding
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.utils.Constant.TAG

class WelcomeFragment : Fragment() {

    private lateinit var activity: MainActivity

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
        clickListeners()
    }

    private fun clickListeners() {
        mBinding.btnLogin.setOnClickListener {
            try {
                val action = WelcomeFragmentDirections.actionWelcomeFragmentToSignInFragment()
                findNavController().navigate(action)
            }catch (e: Exception){
                Log.i(TAG, "navControllerException:: ${e.message}")
            }
        }
        mBinding.btnSignUp.setOnClickListener {
            try {
                val action = WelcomeFragmentDirections.actionWelcomeFragmentToSignUpFragment()
                findNavController().navigate(action)
            }catch (e: Exception){
                Log.i(TAG, "navControllerException:: ${e.message}")
            }
        }
        mBinding.googleLay.setOnClickListener {
            Toast.makeText(requireContext(), "Pending", Toast.LENGTH_SHORT).show()
        }
        mBinding.facebookLay.setOnClickListener {
            Toast.makeText(requireContext(), "Pending", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }
}