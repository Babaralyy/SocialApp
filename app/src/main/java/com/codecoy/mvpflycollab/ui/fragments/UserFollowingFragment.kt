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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.FragmentUserFollowingBinding
import com.codecoy.mvpflycollab.datamodels.UserFollowingData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.UserFollowingAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.UserViewModel
import java.util.ArrayList


class UserFollowingFragment : Fragment() {

    private lateinit var activity: MainActivity
    private lateinit var viewModel: UserViewModel
    private var dialog: Dialog? = null

    private lateinit var userFollowingAdapter: UserFollowingAdapter

    private var currentUser: UserLoginData? = null
    private lateinit var mBinding: FragmentUserFollowingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentUserFollowingBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        dialog = Constant.getDialog(activity)
        currentUser = Utils.getUserFromSharedPreferences(activity)

        mBinding.rvFollowing.layoutManager = LinearLayoutManager(activity)

        setUpViewModel()
        responseFromViewModel()
        getFollowers()

        mBinding.btnBackPress.setOnClickListener {
            try {
                findNavController().popBackStack()
            }catch (e: Exception){

            }
        }
    }

    private fun getFollowers() {
        viewModel.userFollowing("Bearer " + currentUser?.token.toString(), Utils.userId.toString())
    }

    private fun responseFromViewModel() {
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                dialog?.show()
            } else {
                dialog?.dismiss()
            }
        }

        viewModel.usersFollowingProfileResponseLiveData.observe(this) { response ->

            Log.i(Constant.TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val userResponse = response.body()
                if (userResponse != null && userResponse.success == true) {

                    try {
                        setUpAdapter(userResponse.userFollowingData)
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

    private fun setUpAdapter(userFollowingData: ArrayList<UserFollowingData>) {

        if (userFollowingData.isNotEmpty()){
            mBinding.tvNoData.visibility = View.GONE
        } else {
            mBinding.tvNoData.visibility = View.VISIBLE
        }

        userFollowingAdapter = UserFollowingAdapter(userFollowingData, activity)
        mBinding.rvFollowing.adapter = userFollowingAdapter
    }

    private fun setUpViewModel() {
        val mApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val userRepository = MvpRepository(mApi)

        viewModel = ViewModelProvider(
            this,
            MvpViewModelFactory(userRepository)
        )[UserViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }
}