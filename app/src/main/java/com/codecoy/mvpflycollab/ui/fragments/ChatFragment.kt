package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.FragmentChatBinding
import com.codecoy.mvpflycollab.datamodels.MessageData
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.ChatAdapter


class ChatFragment : Fragment() {

    private lateinit var activity: MainActivity
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatList:MutableList<MessageData>

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

        chatList = arrayListOf()

        mBinding.rvChat.layoutManager = LinearLayoutManager(activity)
        mBinding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        chatList.add(MessageData("basdnbsnd", true))
        chatList.add(MessageData("basdnbsnd", true))
        chatList.add(MessageData("basdnbsnd", false))
        chatList.add(MessageData("basdnbsnd", true))
        chatList.add(MessageData("basdnbsnd", false))
        chatList.add(MessageData("basdnbsnd", true))
        chatList.add(MessageData("basdnbsnd", false))

        chatAdapter = ChatAdapter(chatList, activity)
        mBinding.rvChat.adapter = chatAdapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }
}