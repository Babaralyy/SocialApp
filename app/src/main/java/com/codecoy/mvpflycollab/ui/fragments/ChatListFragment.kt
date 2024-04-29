package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.ChatsCallback
import com.codecoy.mvpflycollab.databinding.FragmentChatListBinding
import com.codecoy.mvpflycollab.datamodels.ChatsData
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.ChatListAdapter
import com.codecoy.mvpflycollab.utils.Constant.TAG

class ChatListFragment : Fragment(), ChatsCallback {

    private lateinit var chatListAdapter: ChatListAdapter
    private lateinit var chatsList: MutableList<ChatsData>

//    private lateinit var activity: MainActivity

    private lateinit var mBinding: FragmentChatListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentChatListBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        chatsList = arrayListOf()

        mBinding.rvFollowing.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvFollowing.setHasFixedSize(true)

        setRecyclerView()

        mBinding.btnBackPress.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setRecyclerView() {
        chatsList.clear()

        chatsList.add(ChatsData("", "User 1", "username"))
        chatsList.add(ChatsData("", "User 1", "username"))
        chatsList.add(ChatsData("", "User 1", "username"))

        chatListAdapter = ChatListAdapter(chatsList, requireContext(), this)
        mBinding.rvFollowing.adapter = chatListAdapter
    }

    override fun onUserClick() {
        try {
            findNavController().navigate(ChatListFragmentDirections.actionChatListFragmentToChatFragment2())
        }catch (e: Exception){
            Log.i(TAG, "onUserClick: ${e.message}")
        }
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        (context as MainActivity).also { activity = it }
//    }

}
