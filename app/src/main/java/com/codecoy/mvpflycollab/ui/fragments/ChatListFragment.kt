package com.codecoy.mvpflycollab.ui.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.callbacks.ChatsCallback
import com.codecoy.mvpflycollab.databinding.FragmentChatListBinding
import com.codecoy.mvpflycollab.datamodels.ChatsData
import com.codecoy.mvpflycollab.datamodels.UserFollowingData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.chat.ChatListAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.UserViewModel
import com.google.android.material.snackbar.Snackbar

class ChatListFragment : Fragment(), ChatsCallback {

    private lateinit var activity: MainActivity
    private var currentUser: UserLoginData? = null
    private var dialog: Dialog? = null


    private lateinit var userViewModel: UserViewModel

    private lateinit var chatListAdapter: ChatListAdapter
    private lateinit var chatsList: MutableList<ChatsData>


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
        dialog = Constant.getDialog(requireContext())
        currentUser = Utils.getUserFromSharedPreferences(requireContext())

        mBinding.rvFollowing.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvFollowing.setHasFixedSize(true)

        chatsList = arrayListOf()

        setUpViewModel()
        getFollowersList()
        responseFromViewModel()

        mBinding.btnBackPress.setOnClickListener {
            findNavController().popBackStack()
        }

        activity.onlineUsersMutableLiveData.observe(
            viewLifecycleOwner
        ) {

            val distinctList = it.distinctBy { it1 ->
                it1.id
            }
        }
    }

    private fun getFollowersList() {
        userViewModel.userFollowing("Bearer " + currentUser?.token.toString(), currentUser?.id.toString())
    }

    private fun setUpViewModel() {

        val mApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val userRepository = MvpRepository(mApi)

        userViewModel = ViewModelProvider(
            this,
            MvpViewModelFactory(userRepository)
        )[UserViewModel::class.java]
    }

    private fun responseFromViewModel() {

        userViewModel.loading.observe(this) { isLoading ->
            dialog?.apply { if (isLoading) show() else dismiss() }
        }


        userViewModel.usersFollowingProfileResponseLiveData.observe(this) { response ->
            Log.i(Constant.TAG, "registerUser:: response $response")

            when (response.code()) {
                200 -> {
                    val userResponse = response.body()
                    if (userResponse?.success == true) {
                        setRecyclerView(userResponse.userFollowingData)
                    } else {
                        showSnackBar(mBinding.root, response.body()?.message ?: "Unknown error")
                    }
                }

                401 -> {
                    // Handle 401 Unauthorized
                }

                else -> showSnackBar(mBinding.root, response.errorBody().toString())
            }
        }

        userViewModel.exceptionLiveData.observe(this) { exception ->
            exception.let {
                showSnackBar(mBinding.root, exception.message.toString())
                dialog?.dismiss()
            }

        }
    }

    private fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun setRecyclerView(userFollowingData: ArrayList<UserFollowingData>) {
        val hasChat = userFollowingData.isNotEmpty()
        mBinding.tvNoChat.visibility = if (hasChat) View.GONE else View.VISIBLE

        chatListAdapter = ChatListAdapter(userFollowingData, requireContext(), this)
        mBinding.rvFollowing.adapter = chatListAdapter
    }


    override fun onFollowerClick(chatData: UserFollowingData) {
        try {
            Utils.chatName = chatData.name
            chatData.id.let {
                if (it != null) {
                    Utils.receiverId = it
                }
            }
            Utils.socketId = chatData.socketId

            findNavController().navigate(ChatListFragmentDirections.actionChatListFragmentToChatFragment2())
        } catch (e: Exception) {
            Log.i(Constant.TAG, "onUserClick: ${e.message}")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }



}
