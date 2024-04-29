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
import com.codecoy.mvpflycollab.callbacks.HomeCallback
import com.codecoy.mvpflycollab.databinding.CommentsBottomDialogLayBinding
import com.codecoy.mvpflycollab.databinding.FragmentUserPostsBinding
import com.codecoy.mvpflycollab.databinding.PostItemViewBinding
import com.codecoy.mvpflycollab.datamodels.CommentsData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.datamodels.UserPostsData
import com.codecoy.mvpflycollab.datamodels.UserProfileResponse
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.PostCommentsAdapter
import com.codecoy.mvpflycollab.ui.adapters.UserProfilePostDetailAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.CommentsViewModel
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.PostsViewModel
import com.codecoy.mvpflycollab.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Calendar
import java.util.Date


class UserPostsFragment : Fragment(), HomeCallback {

    private lateinit var activity: MainActivity
    private lateinit var viewModel: UserViewModel
    private lateinit var postViewModel: PostsViewModel
    private lateinit var commentsViewModel: CommentsViewModel
    private var dialog: Dialog? = null

    private var currentUser: UserLoginData? = null

    private lateinit var bottomBinding: CommentsBottomDialogLayBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private lateinit var postCommentsAdapter: PostCommentsAdapter

    private lateinit var userProfilePostsAdapter: UserProfilePostDetailAdapter

    private lateinit var mBinding: FragmentUserPostsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentUserPostsBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        mBinding.rvPosts.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.rvPosts.setHasFixedSize(true)

        userProfilePostsAdapter = UserProfilePostDetailAdapter(mutableListOf(), requireContext(), this)
        mBinding.rvPosts.adapter = userProfilePostsAdapter

        dialog = Constant.getDialog(requireContext())
        currentUser = Utils.getUserFromSharedPreferences(requireContext())


        setUpViewModel()
        getProfileData()
        setUpBottomDialog()

        mBinding.btnBackPress.setOnClickListener {
            try {
                findNavController().popBackStack() 
            }catch (e: Exception){
                Log.i(TAG, "inIt: ${e.message}")
            }
        }
    }


    override fun onResume() {
        super.onResume()
        responseFromViewModel()
    }

    private fun setUpBottomDialog() {
        bottomBinding = CommentsBottomDialogLayBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomBinding.root)

        bottomBinding.rvComments.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setUpViewModel() {

        val mApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val userRepository = MvpRepository(mApi)

        viewModel = ViewModelProvider(
            this,
            MvpViewModelFactory(userRepository)
        )[UserViewModel::class.java]

        postViewModel = ViewModelProvider(
            this,
            MvpViewModelFactory(userRepository)
        )[PostsViewModel::class.java]

        commentsViewModel = ViewModelProvider(
            this,
            MvpViewModelFactory(userRepository)
        )[CommentsViewModel::class.java]
    }

    private fun getProfileData() {
        viewModel.userProfile("Bearer " + currentUser?.token.toString(), Utils.userId.toString())
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

        postViewModel.likePostResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val storiesResponse = response.body()
                if (storiesResponse != null && storiesResponse.success == true) {

                    try {
                        getProfileData()
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


        commentsViewModel.allCommentsResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val commentsResponse = response.body()
                if (commentsResponse != null && commentsResponse.success == true) {

                    try {
                   /*     setUpCommentsRecyclerView(commentsResponse.commentsData)*/
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


        viewModel.exceptionLiveData.observe(this) { exception ->
            if (exception != null) {
                Log.i(TAG, "addJourneyResponseLiveData:: exception $exception")
                dialog?.dismiss()
            }
        }
    }

    private fun setUpProfileData(userProfileResponse: UserProfileResponse) {

        userProfilePostsAdapter.setItemList(userProfileResponse.posts)
        if (Utils.isUserProfile){
            mBinding.rvPosts.smoothScrollToPosition(Utils.scrollPosition)
            Utils.isUserProfile = false
        }
    }

    override fun onMenuClick(postsData: UserPostsData, mBinding: PostItemViewBinding) {

    }

    override fun onCommentsClick(postsData: UserPostsData) {
        showBottomCommentDialog(postsData)
    }

    private fun showBottomCommentDialog(postsData: UserPostsData) {

        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                bottomBinding.progressBar.visibility = View.VISIBLE
            } else {
                bottomBinding.progressBar.visibility = View.GONE
            }
        }


        commentsViewModel.allCommentsResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response ${response.body()}")

            if (response.code() == 200) {
                val commentsResponse = response.body()
                if (commentsResponse != null && commentsResponse.success == true) {

                    try {
                        setUpCommentsRecyclerView(commentsResponse.commentsData)
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

        commentsViewModel.addCommentsResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response ${response.body()}")

            if (response.code() == 200) {
                val commentsResponse = response.body()
                if (commentsResponse != null && commentsResponse.success == true) {

                    try {
                        commentsViewModel.allCommentsAgainstPost(
                            "Bearer " + currentUser?.token.toString(),
                            postsData.id.toString()
                        )

                        getProfileData()

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

        viewModel.exceptionLiveData.observe(this) { exception ->
            if (exception != null) {
                Log.i(TAG, "addJourneyResponseLiveData:: exception $exception")
                bottomBinding.progressBar.visibility = View.GONE
            }
        }

        commentsViewModel.allCommentsAgainstPost(
            "Bearer " + currentUser?.token.toString(),
            postsData.id.toString()
        )
        bottomSheetDialog.show()

        bottomBinding.ivSendComment.setOnClickListener {
            val comment = bottomBinding.etComment.text.toString().trim()
            if (comment.isNotEmpty()) {
                bottomBinding.etComment.setText("")
                commentsViewModel.addComment(
                    "Bearer " + currentUser?.token.toString(),
                    currentUser?.id.toString(),
                    postsData.id.toString(),
                    comment
                )
            } else {
                Toast.makeText(requireContext(), "Please add comment", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpCommentsRecyclerView(commentsData: ArrayList<CommentsData>) {
        if (commentsData.isNotEmpty()) {
            bottomBinding.tvNoComment.visibility = View.GONE
        } else {
            bottomBinding.tvNoComment.visibility = View.VISIBLE
        }

        postCommentsAdapter = PostCommentsAdapter(commentsData, requireContext())
        bottomBinding.rvComments.adapter = postCommentsAdapter
    }

    override fun onLikeClick(postsData: UserPostsData, postItemView: PostItemViewBinding) {

        val currentDate = Utils.formatDate(Date())
        val currentTime = Utils.getCurrentTime(Calendar.getInstance().time)
        postViewModel.likePost(
            "Bearer " + currentUser?.token.toString(),
            currentUser?.id.toString(),
            postsData.id.toString(),
            currentDate,
            currentTime
        )
    }

    override fun onUserClick(postsData: UserPostsData, mBinding: PostItemViewBinding) {

    }

    override fun onSaveClick(postsData: UserPostsData, mBinding: PostItemViewBinding) {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }
}