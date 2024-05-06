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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.UserFollowCallback
import com.codecoy.mvpflycollab.callbacks.UserProfilePostCallback
import com.codecoy.mvpflycollab.databinding.FragmentProfileDetailBinding
import com.codecoy.mvpflycollab.databinding.ShowImageDialogBinding
import com.codecoy.mvpflycollab.datamodels.AllUserData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.datamodels.UserPostsData
import com.codecoy.mvpflycollab.datamodels.UserProfilePosts
import com.codecoy.mvpflycollab.datamodels.UserProfileResponse
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.userprofile.AllUsersAdapter
import com.codecoy.mvpflycollab.ui.adapters.userprofile.UserProfilePostsAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.UserViewModel
import java.util.ArrayList
import java.util.Calendar
import java.util.Date


class ProfileDetailFragment : Fragment(), UserFollowCallback, UserProfilePostCallback {

//    private lateinit var activity: MainActivity

    private lateinit var viewModel: UserViewModel
    private var dialog: Dialog? = null

    private var currentUser: UserLoginData? = null

    private lateinit var allUsersAdapter: AllUsersAdapter
    private lateinit var userProfilePostsAdapter: UserProfilePostsAdapter

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

        mBinding.rvUserPosts.layoutManager = GridLayoutManager(requireContext(), 4)

        dialog = Constant.getDialog(requireContext())

        currentUser = Utils.getUserFromSharedPreferences(requireContext())


        mBinding.rvUsers.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvUsers.setHasFixedSize(true)

        allUsersAdapter = AllUsersAdapter(mutableListOf(), requireContext(), this)
        mBinding.rvUsers.adapter = allUsersAdapter

        setUpViewModel()
        getAllUsers()
        getProfileData()
        clickListeners()


    }

    private fun clickListeners() {
        mBinding.journeyLay.setOnClickListener {
            try {
                Utils.userId = currentUser?.id.toString()
                findNavController().navigate(ProfileDetailFragmentDirections.actionProfileDetailFragmentToJourneyFragment())
            } catch (e: Exception){
                Log.i(TAG, "clickListeners:: ${e.message}")
            }
        }
        mBinding.playlistLay.setOnClickListener {
            try {
                Utils.userId = currentUser?.id.toString()
                findNavController().navigate(ProfileDetailFragmentDirections.actionProfileDetailFragmentToPlayListFragment())
            } catch (e: Exception){
                Log.i(TAG, "clickListeners:: ${e.message}")
            }
        }
        mBinding.followingLay.setOnClickListener {
            try {
                Utils.userId = currentUser?.id.toString()
                findNavController().navigate(ProfileDetailFragmentDirections.actionProfileDetailFragmentToUserFollowingFragment())
            } catch (e: Exception){
                Log.i(TAG, "clickListeners:: ${e.message}")
            }
        }
        mBinding.collaborateLay.setOnClickListener {
            try {
                Utils.userId = currentUser?.id.toString()
                findNavController().navigate(ProfileDetailFragmentDirections.actionProfileDetailFragmentToMembersFragment())
            } catch (e: Exception){
                Log.i(TAG, "clickListeners:: ${e.message}")
            }
        }

        mBinding.savedLay.setOnClickListener {
            try {
                Utils.userId = currentUser?.id.toString()
                findNavController().navigate(ProfileDetailFragmentDirections.actionProfileDetailFragmentToSavedPostsFragment())
            } catch (e: Exception){
                Log.i(TAG, "clickListeners:: ${e.message}")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        responseFromViewModel()
    }
    private fun getProfileData() {
        viewModel.userProfile("Bearer " + currentUser?.token.toString(), currentUser?.id.toString())
        Log.i(TAG, "getProfileData:: ${currentUser?.name} ${currentUser?.id}")
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

            Log.i(TAG, "registerUser:: all users response $response")

            if (response.code() == 200) {
                val userResponse = response.body()
                if (userResponse != null && userResponse.success == true) {

                    try {
                        setUpAdapter(userResponse.userList)
                    } catch (e: Exception) {
                        Log.i(TAG, "navControllerException:: ${e.message}")
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


        viewModel.followUsersResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val userResponse = response.body()
                if (userResponse != null && userResponse.success == true) {

                    try {
                        getAllUsers()
                    } catch (e: Exception) {
                        Log.i(TAG, "navControllerException:: ${e.message}")
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

        viewModel.collabUsersResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val userResponse = response.body()
                if (userResponse != null && userResponse.success == true) {

                    try {
                        getAllUsers()
                    } catch (e: Exception) {
                        Log.i(TAG, "navControllerException:: ${e.message}")
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

        viewModel.usersProfileResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val userProfileResponse = response.body()
                if (userProfileResponse != null && userProfileResponse.success == true) {

                    try {
                        setUpProfileData(userProfileResponse)
                    } catch (e: Exception) {
                        Log.i(TAG, "navControllerException:: ${e.message}")
                    }

                } else {
                    Log.i(TAG, "responseFromViewModel:: ${response.message()}")
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(requireContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }


        viewModel.exceptionLiveData.observe(this) { exception ->
            if (exception != null) {
                Log.i(TAG, "addJourneyResponseLiveData:: exception $exception")
                dialog?.dismiss()
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
        mBinding.tvSavedCount.text = userProfileResponse.savePosts.toString()


        userProfilePostsAdapter = UserProfilePostsAdapter(userProfileResponse.posts, requireContext(), this)
        mBinding.rvUserPosts.adapter = userProfilePostsAdapter
    }

    private fun setUpAdapter(userList: ArrayList<AllUserData>) {

        val isVisible = userList.isNotEmpty()
        mBinding.rvUsers.visibility = if (isVisible) View.VISIBLE else View.GONE
        mBinding.tvSuggested.visibility = if (isVisible) View.VISIBLE else View.GONE

        userList.removeIf { it.followerDetails?.collabStatus == "accepted" || it.followerDetails?.collabStatus == "sent"}

        allUsersAdapter.setItemList(userList)

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
        if (user.followerDetails == null){
            val currentDate = Utils.formatDate(Date())
            val currentTime = Utils.getCurrentTime(Calendar.getInstance().time)
            viewModel.followUser("Bearer " + currentUser?.token.toString(), currentUser?.id.toString(),user.id.toString(),currentDate, currentTime)
        }
    }

    override fun onCollabClick(user: AllUserData) {
        val currentDate = Utils.formatDate(Date())
        val currentTime = Utils.getCurrentTime(Calendar.getInstance().time)
        viewModel.collabUser("Bearer " + currentUser?.token.toString(), currentUser?.id.toString(),user.id.toString(),currentDate, currentTime)
    }

    override fun onImageClick(user: AllUserData) {
        try {
            Utils.userId = user.id.toString()
            findNavController().navigate(ProfileDetailFragmentDirections.actionProfileDetailFragmentToUserProfileDetailsFragment())
        } catch (e: Exception){
            Log.i(TAG, "clickListeners:: ${e.message}")
        }
    }

    override fun onNameClick(user: AllUserData) {
        try {
            Utils.userId = user.id.toString()
            findNavController().navigate(ProfileDetailFragmentDirections.actionProfileDetailFragmentToUserProfileDetailsFragment())
        } catch (e: Exception){
            Log.i(TAG, "clickListeners:: ${e.message}")
        }
    }

    override fun onUserProfilePostClick(post: UserPostsData, position: Int) {
        try {
            Utils.scrollPosition = position
            Utils.isUserProfile = true
            Utils.userId = currentUser?.id.toString()
            findNavController().navigate(ProfileDetailFragmentDirections.actionProfileDetailFragmentToUserPostsFragment())
        }catch (e: Exception){
            Log.i(TAG, "onUserProfilePostClick: ${e.message}")
        }
    }


    private fun showImageDialog(imageUrl: String? = null) {

        val imageBinding = ShowImageDialogBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())
        dialog.setContentView(imageBinding.root)
        dialog.setCancelable(true)

        val window = dialog.window
        val height =
            (requireContext().resources.displayMetrics.widthPixels * 01.4).toInt() // 80% of screen width
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, height)


        if (imageUrl != null) {
            Glide
                .with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.img)
                .into(imageBinding.imageView)
        }

        dialog.show()
    }


/*    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }*/

}