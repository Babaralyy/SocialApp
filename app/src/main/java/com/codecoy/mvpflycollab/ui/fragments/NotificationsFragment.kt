package com.codecoy.mvpflycollab.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.databinding.FragmentNotificationsBinding
import com.codecoy.mvpflycollab.datamodels.NotificationListData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.adapters.NotificationsAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.NotificationViewModel


class NotificationsFragment : Fragment() {

    private lateinit var notificationsAdapter: NotificationsAdapter

    private lateinit var viewModel: NotificationViewModel
    private var dialog: Dialog? = null
    private var currentUser: UserLoginData? = null

    private lateinit var mBinding: FragmentNotificationsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentNotificationsBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        dialog = Constant.getDialog(requireContext())
        currentUser = Utils.getUserFromSharedPreferences(requireContext())

        mBinding.rvNotification.layoutManager = LinearLayoutManager(requireContext())

        setUpViewModel()
        responseFromViewModel()
        getNotifications()
    }

    private fun getNotifications() {
        viewModel.notifications(
            "Bearer " + currentUser?.token.toString(),
            currentUser?.id.toString()
        )
    }

    private fun setUpViewModel() {

        val mApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val userRepository = MvpRepository(mApi)

        viewModel = ViewModelProvider(
            this,
            MvpViewModelFactory(userRepository)
        )[NotificationViewModel::class.java]
    }

    private fun responseFromViewModel() {
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                dialog?.show()
            } else {
                dialog?.dismiss()
            }
        }

        viewModel.notListResponseLiveData.observe(this) { response ->

            Log.i(Constant.TAG, "registerUser:: all users response ${response.body()}")

            if (response.code() == 200) {
                val userResponse = response.body()
                if (userResponse != null && userResponse.success == true) {

                    try {
                        setNotificationAdapter(userResponse.notificationList)
                    } catch (e: Exception) {
                        Log.i(Constant.TAG, "navControllerException:: ${e.message}")
                    }

                } else {
                    mBinding.tvNoData.visibility = View.VISIBLE
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(requireContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }


        viewModel.exceptionLiveData.observe(this) { exception ->
            if (exception != null) {
                Log.i(Constant.TAG, "addJourneyResponseLiveData:: exception $exception")
                dialog?.dismiss()
            }
        }
    }

    private fun setNotificationAdapter(notificationList: ArrayList<NotificationListData>) {
        if (notificationList.isNotEmpty()){
            mBinding.tvNoData.visibility = View.GONE
        }else {
            mBinding.tvNoData.visibility = View.VISIBLE
        }

        notificationsAdapter = NotificationsAdapter(notificationList, requireContext())
        mBinding.rvNotification.adapter = notificationsAdapter

    }
}