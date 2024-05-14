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
import com.codecoy.mvpflycollab.callbacks.HomeCallback
import com.codecoy.mvpflycollab.databinding.CommentsBottomDialogLayBinding
import com.codecoy.mvpflycollab.databinding.FragmentSavedPostsBinding
import com.codecoy.mvpflycollab.databinding.PostItemViewBinding
import com.codecoy.mvpflycollab.datamodels.CommentsData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.datamodels.UserPostsData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.PostCommentsAdapter
import com.codecoy.mvpflycollab.ui.adapters.PostsAdapter
import com.codecoy.mvpflycollab.ui.adapters.SavedPostsAdapter
import com.codecoy.mvpflycollab.ui.adapters.stories.StoriesAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.CommentsViewModel
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.PostsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Calendar
import java.util.Date


class SavedPostsFragment : Fragment(), HomeCallback {

    private lateinit var viewModel: PostsViewModel
    private lateinit var commentsViewModel: CommentsViewModel
    private var dialog: Dialog? = null

    private var currentUser: UserLoginData? = null

//    private lateinit var activity: MainActivity


    private lateinit var savedPostsAdapter: SavedPostsAdapter
    private lateinit var postCommentsAdapter: PostCommentsAdapter

    private lateinit var postItemViewBinding: PostItemViewBinding

    private lateinit var bottomBinding: CommentsBottomDialogLayBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private lateinit var mBinding: FragmentSavedPostsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSavedPostsBinding.inflate(inflater)
        inIt()
        return mBinding.root
    }

    private fun inIt() {


        dialog = Constant.getDialog(requireContext())
        currentUser = Utils.getUserFromSharedPreferences(requireContext())

        mBinding.rvSavedPosts.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.rvSavedPosts.setHasFixedSize(true)

        savedPostsAdapter = SavedPostsAdapter(mutableListOf(), requireContext(), this)
        mBinding.rvSavedPosts.adapter = savedPostsAdapter

        setUpViewModel()

        getAllSavedPosts()

        setUpBottomDialog()
        responseFromViewModel()

        mBinding.btnBackPress.setOnClickListener {
            try {
                findNavController().popBackStack()
            }catch (e: Exception){
                Log.i(TAG, "inIt:: ${e.message}")
            }
        }
    }


    private fun setUpViewModel() {

        val mApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val userRepository = MvpRepository(mApi)

        viewModel = ViewModelProvider(
            this,
            MvpViewModelFactory(userRepository)
        )[PostsViewModel::class.java]

        commentsViewModel = ViewModelProvider(
            this,
            MvpViewModelFactory(userRepository)
        )[CommentsViewModel::class.java]
    }

    private fun getAllSavedPosts() {
        viewModel.savedPosts(
            "Bearer " + currentUser?.token.toString(),
            currentUser?.id.toString()
        )

        Log.i(TAG, "getAllSavedPosts:: ${"Bearer " + currentUser?.token.toString()}  ${ currentUser?.id.toString()}")
    }

    private fun setUpBottomDialog() {
        bottomBinding = CommentsBottomDialogLayBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomBinding.root)

        bottomBinding.rvComments.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun responseFromViewModel() {
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                dialog?.show()
            } else {
                dialog?.dismiss()
            }
        }

        viewModel.savedUsersPostsResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val postsResponse = response.body()
                if (postsResponse != null && postsResponse.success == true) {

                    try {
                        setUpPostsRecyclerView(postsResponse.userPostsData)
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



        viewModel.likePostResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val storiesResponse = response.body()
                if (storiesResponse != null && storiesResponse.success == true) {

                    try {
//                        getAllSavedPosts()
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

        viewModel.deletePostResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val postResponse = response.body()
                if (postResponse != null && postResponse.success == true) {

                    try {
//                        getAllSavedPosts()
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

        viewModel.savePostResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val postResponse = response.body()
                if (postResponse != null && postResponse.success == true) {

                    try {
//                        getAllSavedPosts()
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

/*        commentsViewModel.allCommentsResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

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
        }*/

        viewModel.exceptionLiveData.observe(this) { exception ->
            if (exception != null) {
                Log.i(TAG, "addJourneyResponseLiveData:: exception $exception")
                dialog?.dismiss()
            }
        }
    }

    private fun setUpPostsRecyclerView(userPostsData: ArrayList<UserPostsData>) {
        if (userPostsData.isNotEmpty()) {
            mBinding.tvNoPost.visibility = View.GONE
        } else {
            mBinding.tvNoPost.visibility = View.VISIBLE
        }

        savedPostsAdapter.setItemList(userPostsData)

    }

    override fun onMenuClick(postsData: UserPostsData, mBinding: PostItemViewBinding) {

    }

    override fun onCommentsClick(postsData: UserPostsData) {
        showBottomCommentDialog(postsData)
    }

    override fun onLikeClick(postsData: UserPostsData, postItemView: PostItemViewBinding) {
        this.postItemViewBinding = postItemView
        val tag = postItemView.ivLikeimage.tag
        when (tag) {
            R.drawable.like_post -> {
                postItemView.ivLikeimage.setImageResource(R.drawable.dislike_post)
                postItemView.ivLikeimage.tag = (R.drawable.dislike_post)
            }

            R.drawable.dislike_post -> {
                postItemView.ivLikeimage.setImageResource(R.drawable.like_post)
                postItemView.ivLikeimage.tag = (R.drawable.like_post)
            }
        }
        Log.i(TAG, "onLikeClick:: $tag")


        val currentDate = Utils.formatDate(Date())
        val currentTime = Utils.getCurrentTime(Calendar.getInstance().time)
        viewModel.likePost(
            "Bearer " + currentUser?.token.toString(),
            currentUser?.id.toString(),
            postsData.id.toString(),
            currentDate,
            currentTime
        )
    }

    override fun onUserClick(postsData: UserPostsData, postItemView: PostItemViewBinding) {

    }

    override fun onSaveClick(postsData: UserPostsData, postItemView: PostItemViewBinding) {
        val tag = postItemView.ivSaveImage.tag
        when (tag) {
            R.drawable.save_post_filled -> {
                postItemView.ivSaveImage.setImageResource(R.drawable.unsaved_post)
                postItemView.ivSaveImage.tag = R.drawable.unsaved_post
            }

            R.drawable.unsaved_post -> {
                postItemView.ivSaveImage.setImageResource(R.drawable.save_post_filled)
                postItemView.ivSaveImage.tag = R.drawable.save_post_filled
            }
        }

        viewModel.savePost(
            "Bearer " + currentUser?.token.toString(),
            currentUser?.id.toString(),
            postsData.id.toString()
        )
    }

    private fun showBottomCommentDialog(postsData: UserPostsData) {

        commentsViewModel.loading.observe(this) { isLoading ->
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
                        getAllSavedPosts()

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
                val currentDate = Utils.formatDate(Date())
                val currentTime = Utils.getCurrentTime(Calendar.getInstance().time)
                commentsViewModel.addComment(
                    "Bearer " + currentUser?.token.toString(),
                    currentUser?.id.toString(),
                    postsData.id.toString(),
                    comment,
                    currentDate,
                    currentTime
                )
            } else {
                Toast.makeText(requireContext(), "Please add comment", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpCommentsRecyclerView(commentsData: ArrayList<CommentsData>) {

        val hasComments = commentsData.isNotEmpty()
        bottomBinding.tvNoComment.visibility = if (hasComments) View.GONE else View.VISIBLE

        postCommentsAdapter = PostCommentsAdapter(commentsData, requireContext())
        bottomBinding.rvComments.adapter = postCommentsAdapter
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//
//        (context as MainActivity).also { activity = it }
//    }
}