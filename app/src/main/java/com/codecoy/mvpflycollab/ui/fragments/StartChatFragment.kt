package com.codecoy.mvpflycollab.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.codecoy.mvpflycollab.databinding.FragmentStartChatBinding

class StartChatFragment : Fragment() {

    private lateinit var mBinding: FragmentStartChatBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentStartChatBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        mBinding.btnStart.setOnClickListener {
            try {
                findNavController().navigate(StartChatFragmentDirections.actionStartChatFragmentToChatListFragment())
            }catch (e: Exception){

            }
        }

        mBinding.btnBackPress.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}