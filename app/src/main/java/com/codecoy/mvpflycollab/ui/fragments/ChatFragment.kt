package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.databinding.FragmentChatBinding
import com.codecoy.mvpflycollab.datamodels.MessageData
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.OneToOneChatAdapter


class ChatFragment : Fragment() {

//    private lateinit var activity: MainActivity

    private lateinit var chatAdapter: OneToOneChatAdapter
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

        mBinding.rvChat.layoutManager = LinearLayoutManager(requireContext())
        mBinding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        chatList.add(MessageData("Hi", true))
        chatList.add(MessageData("Hello", true))
        chatList.add(MessageData("Hey", false))
        chatList.add(MessageData("How are you?", true))
        chatList.add(MessageData("I am good..", false))
        chatList.add(MessageData("Let's play football", true))
        chatList.add(MessageData("Okay", false))

        chatAdapter = OneToOneChatAdapter(chatList, requireContext())
        mBinding.rvChat.adapter = chatAdapter

        mBinding.ivSendMessage.setOnClickListener {
            val message = mBinding.etComment.text.toString().trim()
            chatList.add(MessageData(message, false))

            chatAdapter = OneToOneChatAdapter(chatList, requireContext())
            mBinding.rvChat.adapter = chatAdapter

            mBinding.etComment.setText("")
            mBinding.rvChat.scrollToPosition(chatAdapter.itemCount - 1)
        }
    }

   /* override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }*/
}