package com.codecoy.mvpflycollab.ui.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.callbacks.UserFollowCallback
import com.codecoy.mvpflycollab.databinding.FragmentProfileDetailBinding
import com.codecoy.mvpflycollab.datamodels.AllUserData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.AllUsersAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.UserViewModel
import java.util.ArrayList
import java.util.Calendar
import java.util.Date


class ProfileDetailFragment : Fragment(), UserFollowCallback {


    private lateinit var activity: MainActivity
    private lateinit var viewModel: UserViewModel
    private var dialog: Dialog? = null

    private var currentUser: UserLoginData? = null

    private lateinit var allUsersAdapter: AllUsersAdapter



    private lateinit var mBinding: FragmentProfileDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentProfileDetailBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        dialog = Constant.getDialog(activity)
        currentUser = Utils.getUserFromSharedPreferences(activity)


        mBinding.rvUsers.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvUsers.setHasFixedSize(true)

        setUpViewModel()
    }

    override fun onResume() {
        super.onResume()
        getAllUsers()
        responseFromViewModel()
    }

    private fun getAllUsers() {
        viewModel.allUsers("Bearer " + currentUser?.token.toString())
    }

    private fun responseFromViewModel() {
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                dialog?.show()
            } else {
                dialog?.dismiss()
            }
        }

        viewModel.allUsersResponseLiveData.observe(this) { response ->

            Log.i(Constant.TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val userResponse = response.body()
                if (userResponse != null && userResponse.success == true) {

                    try {
                        setUpAdapter(userResponse.userList)
                    } catch (e: Exception) {
                        Log.i(Constant.TAG, "navControllerException:: ${e.message}")
                    }

                } else {
                    Toast.makeText(activity, response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(activity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }


        viewModel.followUsersResponseLiveData.observe(this) { response ->

            Log.i(Constant.TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val userResponse = response.body()
                if (userResponse != null && userResponse.success == true) {

                    try {
                        getAllUsers()
                    } catch (e: Exception) {
                        Log.i(Constant.TAG, "navControllerException:: ${e.message}")
                    }

                } else {
                    Toast.makeText(activity, response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(activity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }


        viewModel.exceptionLiveData.observe(this) { exception ->
            if (exception != null) {
                Log.i(Constant.TAG, "addJourneyResponseLiveData:: exception $exception")
                dialog?.dismiss()
            }
        }
    }

    private fun setUpAdapter(userList: ArrayList<AllUserData>) {
        allUsersAdapter = AllUsersAdapter(userList, activity, this)
        mBinding.rvUsers.adapter = allUsersAdapter
    }

    private fun setUpViewModel() {

        val mApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val userRepository = MvpRepository(mApi)

        viewModel = ViewModelProvider(
            this,
            MvpViewModelFactory(userRepository)
        )[UserViewModel::class.java]
    }

    override fun onFollowClick(user: AllUserData) {
        val currentDate = Utils.formatDate(Date())
        val currentTime = Utils.getCurrentTime(Calendar.getInstance().time)
        viewModel.followUser("Bearer " + currentUser?.token.toString(), currentUser?.id.toString(),user.id.toString(),currentDate, currentTime)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }


}