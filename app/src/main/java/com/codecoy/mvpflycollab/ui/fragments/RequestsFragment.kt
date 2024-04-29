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
import com.codecoy.mvpflycollab.callbacks.RequestsCallback
import com.codecoy.mvpflycollab.databinding.FragmentRequestsBinding
import com.codecoy.mvpflycollab.datamodels.FollowRequestsData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.CollaborateRequestsAdapter
import com.codecoy.mvpflycollab.ui.adapters.FollowRequestsAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.UserViewModel
import java.util.Calendar
import java.util.Date


class RequestsFragment : Fragment(), RequestsCallback {

/*    private lateinit var activity: MainActivity*/
    private lateinit var collaborateRequestsAdapter: CollaborateRequestsAdapter
    private lateinit var followRequestsAdapter: FollowRequestsAdapter

    private lateinit var viewModel: UserViewModel
    private var dialog: Dialog? = null

    private var currentUser: UserLoginData? = null

    private lateinit var colList: MutableList<String>
    private lateinit var followList: MutableList<String>


    private lateinit var mBinding: FragmentRequestsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentRequestsBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        colList = arrayListOf()
        followList = arrayListOf()


        dialog = Constant.getDialog(requireContext())
        currentUser = Utils.getUserFromSharedPreferences(requireContext())

        mBinding.rvCollabrate.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvFollower.layoutManager = LinearLayoutManager(requireContext())

        collaborateRequestsAdapter = CollaborateRequestsAdapter(mutableListOf(), requireContext(), this)
        mBinding.rvCollabrate.adapter = collaborateRequestsAdapter

        followRequestsAdapter = FollowRequestsAdapter(mutableListOf(), requireContext(), this)
        mBinding.rvFollower.adapter = followRequestsAdapter


        setUpViewModel()
        getAllFollowRequests()
        getAllColRequests()
        responseFromViewModel()
    }

    private fun getAllColRequests() {
        viewModel.userColRequests(
            "Bearer " + currentUser?.token.toString(),
            currentUser?.id.toString()
        )
    }

    private fun getAllFollowRequests() {
        viewModel.userFollowingRequests(
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
        )[UserViewModel::class.java]
    }


    private fun responseFromViewModel() {
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                dialog?.show()
            } else {
                dialog?.dismiss()
            }
        }

        viewModel.usersFollowingReqProfileResponseLiveData.observe(this) { response ->

            Log.i(Constant.TAG, "registerUser:: all users response $response")

            if (response.code() == 200) {
                val userResponse = response.body()
                if (userResponse != null && userResponse.success == true) {

                    try {
                        setFollowAdapter(userResponse.followRequestsList)
                    } catch (e: Exception) {
                        Log.i(Constant.TAG, "navControllerException:: ${e.message}")
                    }

                } else {
                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(requireContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.usersColReqProfileResponseLiveData.observe(this) { response ->

            Log.i(Constant.TAG, "registerUser:: all users response $response")

            if (response.code() == 200) {
                val userResponse = response.body()
                if (userResponse != null && userResponse.success == true) {

                    try {
                        setColAdapter(userResponse.followRequestsList)
                    } catch (e: Exception) {
                        Log.i(Constant.TAG, "navControllerException:: ${e.message}")
                    }

                } else {
                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(requireContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }


        viewModel.acceptFollowReqLiveData.observe(this) { response ->

            Log.i(Constant.TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val reqResponse = response.body()
                if (reqResponse != null && reqResponse.success == true) {

                    try {
                        getAllFollowRequests()
                    } catch (e: Exception) {
                        Log.i(Constant.TAG, "navControllerException:: ${e.message}")
                    }

                } else {
                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(requireContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.declineFollowReqLiveData.observe(this) { response ->

            Log.i(Constant.TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val reqResponse = response.body()
                if (reqResponse != null && reqResponse.success == true) {

                    try {
                        getAllFollowRequests()
                    } catch (e: Exception) {
                        Log.i(Constant.TAG, "navControllerException:: ${e.message}")
                    }

                } else {
                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(requireContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.acceptColReqLiveData.observe(this) { response ->

            Log.i(Constant.TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val reqResponse = response.body()
                if (reqResponse != null && reqResponse.success == true) {

                    try {
                        getAllColRequests()
                    } catch (e: Exception) {
                        Log.i(Constant.TAG, "navControllerException:: ${e.message}")
                    }

                } else {
                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(requireContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }


        viewModel.declineColReqLiveData.observe(this) { response ->

            Log.i(Constant.TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val reqResponse = response.body()
                if (reqResponse != null && reqResponse.success == true) {

                    try {
                        getAllColRequests()
                    } catch (e: Exception) {
                        Log.i(Constant.TAG, "navControllerException:: ${e.message}")
                    }

                } else {
                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
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

    private fun setColAdapter(colRequestsList: ArrayList<FollowRequestsData>) {
        if (colRequestsList.isNotEmpty()) {
            mBinding.tvColData.visibility = View.GONE

        } else {
            mBinding.tvColData.visibility = View.VISIBLE
        }

        collaborateRequestsAdapter.setItemList(colRequestsList)
    }

    private fun setFollowAdapter(followRequestsList: ArrayList<FollowRequestsData>) {


        if (followRequestsList.isNotEmpty()) {
            mBinding.tvNoData.visibility = View.GONE

        } else {
            mBinding.tvNoData.visibility = View.VISIBLE
        }

        followRequestsAdapter.setItemList(followRequestsList)
    }


    override fun onAcceptFollowReq(reqData: FollowRequestsData) {
        val currentDate = Utils.formatDate(Date())
        val currentTime = Utils.getCurrentTime(Calendar.getInstance().time)
        viewModel.acceptFollowRequest(
            "Bearer " + currentUser?.token.toString(),
            currentUser?.id.toString(),
            reqData.id.toString(),
            currentDate,
            currentTime
        )
    }

    override fun onDeclineFollowReq(reqData: FollowRequestsData) {
        viewModel.declineFollowReq(  "Bearer " + currentUser?.token.toString(),
            currentUser?.id.toString(),
            reqData.id.toString())
    }

    override fun onAcceptColReq(reqData: FollowRequestsData) {
        val currentDate = Utils.formatDate(Date())
        val currentTime = Utils.getCurrentTime(Calendar.getInstance().time)
        viewModel.acceptColRequest(
            "Bearer " + currentUser?.token.toString(),
            currentUser?.id.toString(),
            reqData.id.toString(),
            currentDate,
            currentTime
        )
    }

    override fun onDeclineColReq(reqData: FollowRequestsData) {
        viewModel.declineColReq(  "Bearer " + currentUser?.token.toString(),
            currentUser?.id.toString(),
            reqData.id.toString())
    }

/*
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }
*/


}