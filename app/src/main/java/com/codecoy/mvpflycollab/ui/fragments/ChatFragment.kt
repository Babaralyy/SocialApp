package com.codecoy.mvpflycollab.ui.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.chat.socket.SocketManager
import com.codecoy.mvpflycollab.databinding.FragmentChatBinding
import com.codecoy.mvpflycollab.datamodels.MessageData
import com.codecoy.mvpflycollab.datamodels.NewMessageResponseData
import com.codecoy.mvpflycollab.datamodels.OneTwoOneChatData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.chat.OneToOneChatAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.ChatViewModel
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.google.android.material.snackbar.Snackbar
import io.socket.client.Socket
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URISyntaxException
import kotlin.coroutines.resumeWithException


class ChatFragment : Fragment() {

    private lateinit var activity: MainActivity
    private var currentUser: UserLoginData? = null
    private var dialog: Dialog? = null



    private lateinit var chatViewModel: ChatViewModel

    private lateinit var chatAdapter: OneToOneChatAdapter
    private lateinit var chatList: MutableList<MessageData>



    private lateinit var mBinding: FragmentChatBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentChatBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun inIt() {

        chatList = arrayListOf()
        dialog = Constant.getDialog(requireContext())
        currentUser = Utils.getUserFromSharedPreferences(requireContext())

        chatAdapter = OneToOneChatAdapter(mutableListOf(), activity)
        mBinding.rvChat.adapter = chatAdapter

        setUpViewModel()
        getOneToOneChat()
        responseFromViewModel()

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

        mBinding.rvChat.layoutManager = LinearLayoutManager(requireContext())

        mBinding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        mBinding.ivSendMessage.setOnClickListener {
            val message = mBinding.etComment.text.toString().trim()
            emmitMessageEvent(message)

            mBinding.etComment.setText("")

        }
    }

    private fun getOneToOneChat() {

        mBinding.tvChat.text = Utils.chatName

        if (Utils.senderId != 0){
            chatViewModel.oneTwoOneChat(
                "Bearer " + currentUser?.token.toString(),
                Utils.senderId.toString(), Utils.receiverId.toString()
            )
        } else {
            chatViewModel.oneTwoOneChat(
                "Bearer " + currentUser?.token.toString(),
                currentUser?.id.toString(), Utils.receiverId.toString()
            )
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

    private fun responseFromViewModel() {

    /*    chatViewModel.loading.observe(this) { isLoading ->
            dialog?.apply { if (isLoading) show() else dismiss() }
        }*/


        chatViewModel.oneTwoOneResponseLiveData.observe(this) { response ->
            Log.i(TAG, "registerUser:: response $response")

            when (response.code()) {
                200 -> {
                    val userResponse = response.body()
                    if (userResponse?.success == true) {
                        setRecyclerview(userResponse.oneTwoOneChatData)
                    } else {
                        showSnackBar(mBinding.root, response.body()?.message ?: "Unknown error")
                    }
                }

                401 -> {
                    // Handle 401 Unauthorized
                }

                else -> showSnackBar(mBinding.root, "Something went wrong")
            }
        }

        chatViewModel.exceptionLiveData.observe(this) { exception ->
            exception.let {
                Log.i(TAG, "addJourneyResponseLiveData:: exception $exception")
                dialog?.dismiss()
            }

        }
    }

    private fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun setRecyclerview(oneTwoOneChatData: ArrayList<OneTwoOneChatData>) {

        chatList.clear()

        for (item in oneTwoOneChatData){
            if (item.senderId != currentUser?.id){
                chatList.add(MessageData(item.message.toString(), true))
            } else {
                chatList.add(MessageData(item.message.toString(), false))
            }
        }



        chatAdapter = OneToOneChatAdapter(chatList, activity)
        mBinding.rvChat.adapter = chatAdapter

        mBinding.rvChat.scrollToPosition(chatAdapter.itemCount - 1)
    }

    private fun emmitMessageEvent(message: String) {
        val jSONObject = JSONObject()
        jSONObject.put("message", message)
        jSONObject.put("sender_id", currentUser?.id.toString())
        jSONObject.put("receiver_id", Utils.receiverId)
        jSONObject.put("receiver_socket_id", Utils.socketId)
        jSONObject.put(
            "token",
            "Bearer " + currentUser?.token.toString()
        )
        SocketManager.socket?.emit("send_message_user", jSONObject)
        Log.i(TAG, "main socket:: emit")
    }

    private suspend fun connectToSocket() {
        withContext<Socket?>(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                try {
                    SocketManager.socket?.on("get_conversation") {
                        activity.runOnUiThread {

                            getOneToOneChat()
                            Log.i(TAG, "main socket:: message ${it[0]}")
                        }
                    }
                    SocketManager.socket?.on(Socket.EVENT_CONNECT_ERROR) { error ->
                        activity.runOnUiThread {
                            Log.i(TAG, "main socket:: $error ")
                        }

                    }
                    SocketManager.socket?.on(Socket.EVENT_DISCONNECT) {
                        activity.runOnUiThread {
                            Log.i(TAG, "main socket:: EVENT_DISCONNECT $it")
                        }
                    }
                } catch (e: URISyntaxException) {
                    continuation.resumeWithException(e)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }
}