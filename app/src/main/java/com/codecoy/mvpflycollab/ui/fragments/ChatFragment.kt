package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.FragmentChatBinding
import com.codecoy.mvpflycollab.ui.activities.MainActivity


class ChatFragment : Fragment() {

    private lateinit var activity: MainActivity

    private lateinit var mBinding: FragmentChatBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentChatBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        mBinding.ivBack.setOnClickListener {
//            activity.replaceFragment(HomeFragment())
            findNavController().popBackStack()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }
}