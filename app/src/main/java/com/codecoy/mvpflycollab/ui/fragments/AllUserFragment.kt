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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.UserFollowCallback
import com.codecoy.mvpflycollab.databinding.FragmentAllUserBinding
import com.codecoy.mvpflycollab.datamodels.AllUserData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.SearchUserAdapter
import com.codecoy.mvpflycollab.ui.adapters.userprofile.AllUsersAdapter
import com.codecoy.mvpflycollab.ui.adapters.userprofile.UserProfilePostsAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.UserViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.ArrayList


class AllUserFragment : Fragment(), UserFollowCallback {

//    private lateinit var activity: MainActivity

    private lateinit var viewModel: UserViewModel
    private var dialog: Dialog? = null

    private var currentUser: UserLoginData? = null

    private lateinit var searchUserAdapter: SearchUserAdapter


    private lateinit var mBinding: FragmentAllUserBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAllUserBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        dialog = Constant.getDialog(requireContext())
        currentUser = Utils.getUserFromSharedPreferences(requireContext())


        mBinding.rvPeople.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvPeople.setHasFixedSize(true)

        searchUserAdapter = SearchUserAdapter(mutableListOf(), requireContext(), this)
        mBinding.rvPeople.adapter = searchUserAdapter

        setUpViewModel()
        getAllUsers()
        responseFromViewModel()
    }

    private fun setUpViewModel() {

        val mApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val userRepository = MvpRepository(mApi)

        viewModel = ViewModelProvider(
            this,
            MvpViewModelFactory(userRepository)
        )[UserViewModel::class.java]
    }

    private fun getAllUsers() {
        viewModel.allUsers("Bearer " + currentUser?.token.toString(), currentUser?.id.toString())
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

            Log.i(Constant.TAG, "registerUser:: all users response $response")

            if (response.code() == 200) {
                val userResponse = response.body()
                if (userResponse != null && userResponse.success == true) {

                    try {
                        setUpAdapter(userResponse.userList)
                    } catch (e: Exception) {
                        Log.i(Constant.TAG, "navControllerException:: ${e.message}")
                    }

                } else {
                    showSnackBar(mBinding.root, response.body()?.message.toString())
                }
            } else if (response.code() == 401) {

            } else {
                showSnackBar(mBinding.root, response.errorBody().toString())
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
                    showSnackBar(mBinding.root, response.body()?.message.toString())
                }
            } else if (response.code() == 401) {

            } else {
                showSnackBar(mBinding.root, response.errorBody().toString())
            }
        }

        viewModel.collabUsersResponseLiveData.observe(this) { response ->

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
                    showSnackBar(mBinding.root, response.body()?.message.toString())

                }
            } else if (response.code() == 401) {

            } else {
                showSnackBar(mBinding.root, response.errorBody().toString())
            }
        }


        viewModel.exceptionLiveData.observe(this) { exception ->
            if (exception != null) {
                showSnackBar(mBinding.root, exception.message.toString())
                dialog?.dismiss()
            }
        }
    }

    private fun setUpAdapter(userList: ArrayList<AllUserData>) {
        if (userList.isNotEmpty()) {
            mBinding.tvNoData.visibility = View.GONE

        } else {
            mBinding.tvNoData.visibility = View.VISIBLE

        }

        searchUserAdapter.setItemList(userList)

    }

    override fun onFollowClick(user: AllUserData) {

    }

    override fun onCollabClick(user: AllUserData) {

    }

    override fun onImageClick(user: AllUserData) {

    }

    override fun onNameClick(user: AllUserData) {

    }

    private fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    /* override fun onAttach(context: Context) {
         super.onAttach(context)
         (context as MainActivity).also { activity = it }
     }*/
}