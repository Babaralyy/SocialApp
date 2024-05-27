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
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.NotificationCallback
import com.codecoy.mvpflycollab.databinding.FragmentNotificationsBinding
import com.codecoy.mvpflycollab.datamodels.NotificationListData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.NotificationsAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.NotificationViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar


class NotificationsFragment : Fragment(), NotificationCallback {

    private lateinit var activity: MainActivity
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
                showSnackBar(mBinding.root, response.errorBody().toString())
            }
        }


        viewModel.exceptionLiveData.observe(this) { exception ->
            if (exception != null) {
                dialog?.dismiss()
                showSnackBar(mBinding.root, exception.message.toString())
            }
        }
    }


    private fun setNotificationAdapter(notificationList: ArrayList<NotificationListData>) {
        if (notificationList.isNotEmpty()){
            mBinding.tvNoData.visibility = View.GONE
        }else {
            mBinding.tvNoData.visibility = View.VISIBLE
        }

        notificationsAdapter = NotificationsAdapter(notificationList, requireContext(), this)
        mBinding.rvNotification.adapter = notificationsAdapter

    }

    override fun onNotificationClick(notData: NotificationListData) {
        Utils.postId = notData.postId.toString()
        activity.findViewById<BottomNavigationView>(R.id.bottomNavigation).selectedItemId = R.id.navigation_home
        activity.replaceFragment(HomeFragment())
    }

    private fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }

}