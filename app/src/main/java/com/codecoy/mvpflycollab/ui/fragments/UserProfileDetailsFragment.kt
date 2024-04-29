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
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.UserProfilePostCallback
import com.codecoy.mvpflycollab.databinding.FragmentUserProfileDetailsBinding
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.datamodels.UserPostsData
import com.codecoy.mvpflycollab.datamodels.UserProfileResponse
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.userprofile.UserProfilePostsAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.UserViewModel


class UserProfileDetailsFragment : Fragment(), UserProfilePostCallback {

    private lateinit var activity: MainActivity
    private lateinit var viewModel: UserViewModel
    private var dialog: Dialog? = null

    private var currentUser: UserLoginData? = null


    private lateinit var userProfilePostsAdapter: UserProfilePostsAdapter

    private lateinit var mBinding: FragmentUserProfileDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentUserProfileDetailsBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        mBinding.rvUserPosts.layoutManager = GridLayoutManager(requireContext(), 4)

        dialog = Constant.getDialog(requireContext())
        currentUser = Utils.getUserFromSharedPreferences(requireContext())



        setUpViewModel()
        getProfileData()
        clickListeners()
        responseFromViewModel()
    }


    private fun responseFromViewModel() {
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                dialog?.show()
            } else {
                dialog?.dismiss()
            }
        }

        viewModel.usersProfileResponseLiveData.observe(this) { response ->

            Log.i(Constant.TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val userProfileResponse = response.body()
                if (userProfileResponse != null && userProfileResponse.success == true) {

                    try {
                        setUpProfileData(userProfileResponse)
                    } catch (e: Exception) {
                        Log.i(Constant.TAG, "navControllerException:: ${e.message}")
                    }

                } else {
                    Log.i(Constant.TAG, "responseFromViewModel:: ${response.message()}")
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
    private fun setUpViewModel() {

        val mApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val userRepository = MvpRepository(mApi)

        viewModel = ViewModelProvider(
            this,
            MvpViewModelFactory(userRepository)
        )[UserViewModel::class.java]
    }

    private fun getProfileData() {
        viewModel.userProfile("Bearer " + currentUser?.token.toString(), Utils.userId.toString())
        Log.i(Constant.TAG, "getProfileData:: ${currentUser?.name} ${currentUser?.id}")
    }

    private fun clickListeners() {
        mBinding.journeyLay.setOnClickListener {
            try {
                findNavController().navigate(UserProfileDetailsFragmentDirections.actionUserProfileDetailsFragmentToJourneyFragment())
            } catch (e: Exception){
                Log.i(Constant.TAG, "clickListeners:: ${e.message}")
            }
        }
        mBinding.playlistLay.setOnClickListener {
            try {
                findNavController().navigate(UserProfileDetailsFragmentDirections.actionUserProfileDetailsFragmentToPlayListFragment())
            } catch (e: Exception){
                Log.i(Constant.TAG, "clickListeners:: ${e.message}")
            }
        }
        mBinding.followingLay.setOnClickListener {
            try {
                findNavController().navigate(UserProfileDetailsFragmentDirections.actionUserProfileDetailsFragmentToUserFollowingFragment())
            } catch (e: Exception){
                Log.i(Constant.TAG, "clickListeners:: ${e.message}")
            }
        }
        mBinding.collaborateLay.setOnClickListener {
            try {
                findNavController().navigate(UserProfileDetailsFragmentDirections.actionUserProfileDetailsFragmentToMembersFragment())
            } catch (e: Exception){
                Log.i(Constant.TAG, "clickListeners:: ${e.message}")
            }
        }
    }

    private fun setUpProfileData(userProfileResponse: UserProfileResponse) {
        Glide
            .with(requireContext())
            .load(Constant.MEDIA_BASE_URL + userProfileResponse.user?.profileImg)
            .placeholder(R.drawable.img)
            .into(mBinding.ivProfile)

        mBinding.tvProfileName.text = userProfileResponse.user?.name
        mBinding.tvProfileLocation.text = userProfileResponse.user?.username
        mBinding.tvJourneyCount.text = userProfileResponse.journeyCount.toString()
        mBinding.tvPlaylistCount.text = userProfileResponse.playlistCount.toString()
        mBinding.tvFollowersCount.text = userProfileResponse.followersCount.toString()
        mBinding.tvCollaboratorsCount.text = userProfileResponse.collabCount.toString()

        if (userProfileResponse.posts.isNotEmpty()){
            mBinding.tvNoPost.visibility = View.GONE
        } else {
            mBinding.tvNoPost.visibility = View.VISIBLE
        }

        userProfilePostsAdapter = UserProfilePostsAdapter(userProfileResponse.posts, requireContext(), this)
        mBinding.rvUserPosts.adapter = userProfilePostsAdapter
    }

    override fun onUserProfilePostClick(post: UserPostsData, position: Int) {
        try {
            Utils.scrollPosition = position
            Utils.isUserProfile = true
            Utils.userId = post.userId.toString()
            findNavController().navigate(UserProfileDetailsFragmentDirections.actionUserProfileDetailsFragmentToUserPostsFragment())
        }catch (e: Exception){
            Log.i(Constant.TAG, "onUserProfilePostClick: ${e.message}")
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }

}