package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.codecoy.mvpflycollab.databinding.FragmentStartChatBinding
import com.codecoy.mvpflycollab.datamodels.OnlineUserData
import com.codecoy.mvpflycollab.ui.activities.MainActivity


class StartChatFragment : Fragment() {

    private lateinit var activity: MainActivity
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

        listenSocket()

        mBinding.btnStart.setOnClickListener {
            try {
                findNavController().navigate(StartChatFragmentDirections.actionStartChatFragmentToChatListFragment())
            } catch (e: Exception) {

            }
        }

        mBinding.btnBackPress.setOnClickListener {
            findNavController().popBackStack()
        }


    }

    private fun listenSocket() {

    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }
}