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
import androidx.recyclerview.widget.GridLayoutManager
import com.codecoy.mvpflycollab.callbacks.MembersCallback
import com.codecoy.mvpflycollab.databinding.FragmentMembersBinding
import com.codecoy.mvpflycollab.datamodels.CollaboratorsListData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.members.MembersAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.UserViewModel
import com.google.android.material.snackbar.Snackbar


class MembersFragment : Fragment(), MembersCallback {

//    private lateinit var activity: MainActivity

    private lateinit var membersAdapter: MembersAdapter

    private lateinit var viewModel: UserViewModel
    private var dialog: Dialog? = null

    private var currentUser: UserLoginData? = null


    private lateinit var mBinding: FragmentMembersBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMembersBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        dialog = Constant.getDialog(requireContext())
        currentUser = Utils.getUserFromSharedPreferences(requireContext())

        mBinding.rvMembers.layoutManager = GridLayoutManager(requireContext(), 3)
        mBinding.rvMembers.setHasFixedSize(true)

        setUpViewModel()
        getAllCol()
        responseFromViewModel()

        mBinding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setUpViewModel() {

        val mApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val userRepository = MvpRepository(mApi)

        viewModel = ViewModelProvider(
            this,
            MvpViewModelFactory(userRepository)
        )[UserViewModel::class.java]
    }

    private fun getAllCol() {
        viewModel.collaboratorsList("Bearer " + currentUser?.token.toString(),     Utils.userId.toString())
    }

    private fun responseFromViewModel() {
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                dialog?.show()
            } else {
                dialog?.dismiss()
            }
        }

        viewModel.usersColResponseLiveData.observe(this) { response ->

            Log.i(Constant.TAG, "registerUser:: all users response $response")

            if (response.code() == 200) {
                val userResponse = response.body()
                if (userResponse != null && userResponse.success == true) {

                    try {
                        setUpAdapter(userResponse.collaboratorsListData)
                    } catch (e: Exception) {
                        Log.i(Constant.TAG, "navControllerException:: ${e.message}")
                    }

                } else {
                    mBinding.tvNoData.visibility = View.VISIBLE
                    showSnackBar(mBinding.root, response.body()?.message ?: "Unknown error")
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

    private fun setUpAdapter(collaboratorsListData: ArrayList<CollaboratorsListData>) {

        if (collaboratorsListData.isNotEmpty()){
            mBinding.tvNoData.visibility = View.GONE
        } else {
            mBinding.tvNoData.visibility = View.VISIBLE
        }

        membersAdapter = MembersAdapter(collaboratorsListData, requireContext(), this)
        mBinding.rvMembers.adapter = membersAdapter

    }

    override fun onMemberClick(memberData: CollaboratorsListData) {
//        try {
//            Utils.userId = memberData.id.toString()
//            findNavController().navigate(MembersFragmentDirections.actionMembersFragmentToUserProfileDetailsFragment())
//        } catch (e: Exception){
//            Log.i(Constant.TAG, "clickListeners:: ${e.message}")
//        }
    }
    private fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }


/*    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }*/
}