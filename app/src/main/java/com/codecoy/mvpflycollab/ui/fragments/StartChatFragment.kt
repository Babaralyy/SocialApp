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
import com.codecoy.mvpflycollab.chat.socket.SocketManager
import com.codecoy.mvpflycollab.databinding.FragmentStartChatBinding
import com.codecoy.mvpflycollab.datamodels.UserChatListData
import com.codecoy.mvpflycollab.datamodels.UserFollowingData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.chat.AddUserForChatAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.ChatViewModel
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.UserViewModel
import com.google.android.material.snackbar.Snackbar
import io.socket.client.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.net.URISyntaxException
import kotlin.coroutines.resumeWithException


class StartChatFragment : Fragment(), ChatsCallback {


    private lateinit var activity: MainActivity
    private var currentUser: UserLoginData? = null
    private var dialog: Dialog? = null

    private lateinit var chatViewModel: ChatViewModel

    private lateinit var addUserForChatAdapter: AddUserForChatAdapter

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
        dialog = Constant.getDialog(requireContext())
        currentUser = Utils.getUserFromSharedPreferences(requireContext())
        mBinding.rvChatUser.layoutManager = LinearLayoutManager(requireContext())

        setUpViewModel()
        getChatUserList()
        responseFromViewModel()

        mBinding.btnBackPress.setOnClickListener {
            findNavController().popBackStack()
        }
        mBinding.fabAdd.setOnClickListener {
            try {
                findNavController().navigate(StartChatFragmentDirections.actionStartChatFragmentToChatListFragment())
            } catch (e: Exception) {

            }
        }
    }


    private fun setUpViewModel() {
        val mApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val userRepository = MvpRepository(mApi)

        chatViewModel = ViewModelProvider(
            this,
            MvpViewModelFactory(userRepository)
        )[ChatViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()

        try {
            GlobalScope.launch {
                try {
                    connectToSocket()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    private suspend fun connectToSocket() {
        withContext<Socket?>(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                try {
                    SocketManager.socket?.on("get_conversation") {
                        activity.runOnUiThread {
                            getChatUserList()
                            Log.i(Constant.TAG, "main socket:: message ${it[0]}")
                        }
                    }
                    SocketManager.socket?.on(Socket.EVENT_CONNECT_ERROR) { error ->
                        activity.runOnUiThread {
                            Log.i(Constant.TAG, "main socket:: $error ")
                        }

                    }
                    SocketManager.socket?.on(Socket.EVENT_DISCONNECT) {
                        activity.runOnUiThread {
                            Log.i(Constant.TAG, "main socket:: message $it")
                        }
                    }
                } catch (e: URISyntaxException) {
                    continuation.resumeWithException(e)
                }
            }
        }
    }

    private fun getChatUserList() {
        chatViewModel.userChatList(
            "Bearer " + currentUser?.token.toString(),
            currentUser?.id.toString()
        )
    }

    private fun responseFromViewModel() {

        chatViewModel.loading.observe(this) { isLoading ->
            dialog?.apply { if (isLoading) show() else dismiss() }
        }


        chatViewModel.chatListResponseLiveData.observe(this) { response ->
            Log.i(Constant.TAG, "registerUser:: response $response")

            when (response.code()) {
                200 -> {
                    val userResponse = response.body()
                    if (userResponse?.success == true) {
                        setUpRecyclerView(userResponse.response)
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

        chatViewModel.exceptionLiveData.observe(this) { exception ->
            exception.let {
                dialog?.dismiss()
                showSnackBar(mBinding.root, exception.message.toString())
            }
        }
    }

    private fun setUpRecyclerView(response: ArrayList<UserChatListData>) {

        val hasChat = response.isNotEmpty()
        mBinding.tvChatMessage.visibility = if (hasChat) View.GONE else View.VISIBLE

        addUserForChatAdapter = AddUserForChatAdapter(response, requireContext(), this)
        mBinding.rvChatUser.adapter = addUserForChatAdapter
    }

    private fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onUserClick(chatData: UserChatListData) {

        try {
            Utils.chatName = chatData.name
            chatData.id.let {
                if (it != null) {
                    Utils.receiverId = it
                }
            }
            Utils.socketId = chatData.socketId

            findNavController().navigate(StartChatFragmentDirections.actionStartChatFragmentToChatFragment())
        } catch (e: Exception) {
            Log.i(Constant.TAG, "onUserClick: ${e.message}")
        }
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }


}