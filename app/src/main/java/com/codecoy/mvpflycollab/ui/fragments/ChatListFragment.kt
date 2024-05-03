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
import com.codecoy.mvpflycollab.callbacks.ChatsCallback
import com.codecoy.mvpflycollab.databinding.FragmentChatListBinding
import com.codecoy.mvpflycollab.datamodels.ChatsData
import com.codecoy.mvpflycollab.datamodels.OnlineUserData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.ChatListAdapter
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.utils.Utils

class ChatListFragment : Fragment(), ChatsCallback {

    private lateinit var activity: MainActivity

    private lateinit var chatListAdapter: ChatListAdapter
    private lateinit var chatsList: MutableList<ChatsData>

    private var currentUser: UserLoginData? = null

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
        currentUser = Utils.getUserFromSharedPreferences(requireContext())

        mBinding.rvFollowing.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvFollowing.setHasFixedSize(true)

        mBinding.btnBackPress.setOnClickListener {
            findNavController().popBackStack()
        }

        activity.onlineUsersMutableLiveData.observe(
            viewLifecycleOwner
        ) {

            val distinctList = it.distinctBy { it1 ->
                it1.id
            }

            setRecyclerView(distinctList.toMutableList())
        }
    }

    private fun setRecyclerView(onlineUserData: MutableList<OnlineUserData>) {
        if (onlineUserData.isNotEmpty()){
            mBinding.tvNoChat.visibility = View.GONE
        } else{
            mBinding.tvNoChat.visibility = View.VISIBLE
        }
        chatListAdapter = ChatListAdapter(onlineUserData, requireContext(), this)
        mBinding.rvFollowing.adapter = chatListAdapter
    }

    override fun onUserClick(chatData: OnlineUserData) {
        try {
            chatData.id.let {
                if (it != null) {
                    Utils.receiverId = it
                }
            }
            Utils.socketId = chatData.socketId

            findNavController().navigate(ChatListFragmentDirections.actionChatListFragmentToChatFragment2())
        } catch (e: Exception) {
            Log.i(TAG, "onUserClick: ${e.message}")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }

}
