package com.codecoy.mvpflycollab.ui.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.HomeCallback
import com.codecoy.mvpflycollab.callbacks.StoryCallback
import com.codecoy.mvpflycollab.databinding.CommentsBottomDialogLayBinding
import com.codecoy.mvpflycollab.databinding.FragmentHomeBinding
import com.codecoy.mvpflycollab.databinding.PostItemViewBinding
import com.codecoy.mvpflycollab.datamodels.CalendarStoryData
import com.codecoy.mvpflycollab.datamodels.CommentsData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.datamodels.UserPostsData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.PostCommentsAdapter
import com.codecoy.mvpflycollab.ui.adapters.PostsAdapter
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


class HomeFragment : Fragment(), HomeCallback, StoryCallback {

    private lateinit var viewModel: PostsViewModel
    private lateinit var commentsViewModel: CommentsViewModel
    private var dialog: Dialog? = null

    private var currentUser: UserLoginData? = null

    private lateinit var activity: MainActivity

    private lateinit var storiesAdapter: StoriesAdapter
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var postCommentsAdapter: PostCommentsAdapter

    private lateinit var postItemViewBinding: PostItemViewBinding

    private lateinit var bottomBinding: CommentsBottomDialogLayBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private lateinit var mBinding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHomeBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        dialog = Constant.getDialog(activity)
        currentUser = Utils.getUserFromSharedPreferences(activity)

        mBinding.rvStories.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvStories.setHasFixedSize(true)

        mBinding.rvPosts.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mBinding.rvPosts.setHasFixedSize(true)

        postsAdapter = PostsAdapter(mutableListOf(), activity, this)
        mBinding.rvPosts.adapter = postsAdapter

        setUpViewModel()
        setUpDrawer()
        getAllPosts()
        getAllStories()
        setUpBottomDialog()

        mBinding.ivMessenger.setOnClickListener {
            try {
                val action = MainFragmentDirections.actionMainFragmentToChatFragment()
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.i(TAG, "inIt: ${e.message}")
            }
        }

        mBinding.btnDiscover.setOnClickListener {
            try {
                val action = MainFragmentDirections.actionMainFragmentToAboutProfileFragment()
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.i(TAG, "inIt: ${e.message}")
            }
        }


        mBinding.ivSearch.setOnClickListener {
            try {
                val action = MainFragmentDirections.actionMainFragmentToAllUserFragment()
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.i(TAG, "inIt: ${e.message}")
            }
        }

        mBinding.svHome.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                postsAdapter.filter(s.toString())
                if (s.toString().isEmpty()) {
                    getAllPosts()
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        responseFromViewModel()
    }

    private fun setUpBottomDialog() {
        bottomBinding = CommentsBottomDialogLayBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(activity)
        bottomSheetDialog.setContentView(bottomBinding.root)

        bottomBinding.rvComments.layoutManager = LinearLayoutManager(activity)
    }

    private fun getAllPosts() {
        viewModel.allUserPosts(
            "Bearer " + currentUser?.token.toString(),
            currentUser?.id.toString()
        )
    }

    private fun getAllStories() {
        viewModel.allStories("Bearer " + currentUser?.token.toString(), currentUser?.id.toString())
    }


    private fun responseFromViewModel() {
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                dialog?.show()
            } else {
                dialog?.dismiss()
            }
        }

        viewModel.allUsersPostsResponseLiveData.observe(this) { response ->

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
                    Toast.makeText(activity, response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(activity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.allStoriesResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val storiesResponse = response.body()
                if (storiesResponse != null && storiesResponse.success == true) {

                    try {
                        setUpStoriesRecyclerView(storiesResponse.calendarStoryData)
                    } catch (e: Exception) {
                        Log.i(TAG, "navControllerException:: ${e.message}")
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

        viewModel.likePostResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val storiesResponse = response.body()
                if (storiesResponse != null && storiesResponse.success == true) {

                    try {
                        getAllPosts()
                    } catch (e: Exception) {
                        Log.i(TAG, "navControllerException:: ${e.message}")
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

        viewModel.deletePostResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val postResponse = response.body()
                if (postResponse != null && postResponse.success == true) {

                    try {
                        getAllPosts()
                    } catch (e: Exception) {
                        Log.i(TAG, "navControllerException:: ${e.message}")
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

        viewModel.savePostResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val postResponse = response.body()
                if (postResponse != null && postResponse.success == true) {

                    try {
                        getAllPosts()
                    } catch (e: Exception) {
                        Log.i(TAG, "navControllerException:: ${e.message}")
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

        commentsViewModel.allCommentsResponseLiveData.observe(this) { response ->

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
                Log.i(TAG, "addJourneyResponseLiveData:: exception $exception")
                dialog?.dismiss()
            }
        }
    }

    private fun setUpCommentsRecyclerView(commentsData: ArrayList<CommentsData>) {
        if (commentsData.isNotEmpty()) {
            bottomBinding.tvNoComment.visibility = View.GONE
        } else {
            bottomBinding.tvNoComment.visibility = View.VISIBLE
        }

        postCommentsAdapter = PostCommentsAdapter(commentsData, activity)
        bottomBinding.rvComments.adapter = postCommentsAdapter
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


    private fun setUpDrawer() {
        mBinding.drawerNavigation.itemIconTintList = null // To allow custom icon colors
        val actionView = layoutInflater.inflate(R.layout.custom_nav_layout, null)
        mBinding.drawerNavigation.addView(actionView)



        mBinding.navIcon.setOnClickListener {
            if (!mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                mBinding.drawerLayout.openDrawer(GravityCompat.START)
            } else {
                mBinding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        drawersClicks()
    }

    private fun drawersClicks() {
        mBinding.drawerLayout.findViewById<ImageView>(R.id.ivCloseDrawer).setOnClickListener {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        Glide
            .with(activity)
            .load(Constant.MEDIA_BASE_URL + currentUser?.profileImg)
            .placeholder(R.drawable.img)
            .into(mBinding.drawerLayout.findViewById(R.id.ivUserProfile))

        mBinding.drawerLayout.findViewById<TextView>(R.id.tvProfileName).setText(currentUser?.name)

        mBinding.drawerLayout.findViewById<LinearLayout>(R.id.iProfile).setOnClickListener {

            mBinding.drawerLayout.closeDrawer(GravityCompat.START)

            try {
                val action = MainFragmentDirections.actionMainFragmentToAboutProfileFragment()
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.i(TAG, "inIt: ${e.message}")
            }
        }

        mBinding.drawerLayout.findViewById<LinearLayout>(R.id.iEditProfile).setOnClickListener {

            mBinding.drawerLayout.closeDrawer(GravityCompat.START)

            try {
                val action = MainFragmentDirections.actionMainFragmentToEditProfileFragment()
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.i(TAG, "inIt: ${e.message}")
            }
        }

        mBinding.drawerLayout.findViewById<LinearLayout>(R.id.iJourney).setOnClickListener {

            mBinding.drawerLayout.closeDrawer(GravityCompat.START)

            try {
                Utils.userId = currentUser?.id.toString()
                val action = MainFragmentDirections.actionMainFragmentToJourneyFragment()
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.i(TAG, "inIt: ${e.message}")
            }

        }

        mBinding.drawerLayout.findViewById<LinearLayout>(R.id.iPlaylist).setOnClickListener {

            mBinding.drawerLayout.closeDrawer(GravityCompat.START)

            try {
                Utils.userId = currentUser?.id.toString()
                val action = MainFragmentDirections.actionMainFragmentToPlayListFragment()
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.i(TAG, "inIt: ${e.message}")
            }

        }

        mBinding.drawerLayout.findViewById<LinearLayout>(R.id.iCollab).setOnClickListener {

            mBinding.drawerLayout.closeDrawer(GravityCompat.START)

            try {
                Utils.userId = currentUser?.id.toString()
                val action = MainFragmentDirections.actionMainFragmentToMembersFragment()
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.i(TAG, "inIt: ${e.message}")
            }

        }

        mBinding.drawerLayout.findViewById<LinearLayout>(R.id.iLevels).setOnClickListener {

            mBinding.drawerLayout.closeDrawer(GravityCompat.START)

            try {
                val action = MainFragmentDirections.actionMainFragmentToLevelsFragment()
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.i(TAG, "inIt: ${e.message}")
            }

        }

        mBinding.drawerLayout.findViewById<LinearLayout>(R.id.iShareUs).setOnClickListener {
            Toast.makeText(activity, "Clicked", Toast.LENGTH_SHORT).show()
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        mBinding.drawerLayout.findViewById<LinearLayout>(R.id.iRateUs).setOnClickListener {
            Toast.makeText(activity, "Clicked", Toast.LENGTH_SHORT).show()
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        mBinding.drawerLayout.findViewById<LinearLayout>(R.id.iLogout).setOnClickListener {
            try {
                Utils.clearSharedPreferences(activity)
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToSignInFragment())
            } catch (e: Exception) {

            }
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun setUpStoriesRecyclerView(calendarStoryData: ArrayList<CalendarStoryData>) {
        if (calendarStoryData.isNotEmpty()) {
            mBinding.rvStories.visibility = View.VISIBLE
        } else {
            mBinding.rvStories.visibility = View.GONE
        }
        storiesAdapter = StoriesAdapter(calendarStoryData, activity, this)
        mBinding.rvStories.adapter = storiesAdapter
    }

    private fun setUpPostsRecyclerView(userPostsData: ArrayList<UserPostsData>) {
        if (userPostsData.isNotEmpty()) {
            mBinding.group.visibility = View.GONE
        } else {
            mBinding.group.visibility = View.VISIBLE
        }

        postsAdapter.setItemList(userPostsData)

    }

    override fun onMenuClick(postsData: UserPostsData, mBinding: PostItemViewBinding) {
        showPopupMenu(mBinding.ivmenu, postsData)
    }

    private fun showPopupMenu(view: View, postsData: UserPostsData) {
        val popupMenu = PopupMenu(activity, view)
        popupMenu.inflate(R.menu.popup_menu) // Inflate the menu resource
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
               /* R.id.item_edit -> {
                    // Handle click on menu item 1
                    true
                }*/
                R.id.item_delete -> {
                    deletePost(postsData)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun deletePost(postsData: UserPostsData) {
        viewModel.deletePost("Bearer " + currentUser?.token.toString(), postsData.id.toString())
    }

    override fun onCommentsClick(postsData: UserPostsData) {
        /*   try {
               val action = MainFragmentDirections.actionMainFragmentToCommentsFragment()
               findNavController().navigate(action)
           } catch (e: Exception) {

           }*/

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
                    Toast.makeText(activity, response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(activity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
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
                        getAllPosts()

                    } catch (e: Exception) {
                        Log.i(TAG, "navControllerException:: ${e.message}")
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
                Toast.makeText(activity, "Please add comment", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onLikeClick(postsData: UserPostsData, postItemView: PostItemViewBinding) {

        this.postItemViewBinding = postItemView

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

    override fun onUserClick(postsData: UserPostsData, mBinding: PostItemViewBinding) {
        try {
            if(postsData.userId.toString() == currentUser?.id.toString()){
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToAboutProfileFragment())
            } else {
                Utils.userId = postsData.userId.toString()
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToUserProfileDetailsFragment())
            }
        } catch (e: Exception){
            Log.i(TAG, "clickListeners:: ${e.message}")
        }
    }

    override fun onSaveClick(postsData: UserPostsData, mBinding: PostItemViewBinding) {
        viewModel.savePost(
            "Bearer " + currentUser?.token.toString(),
            currentUser?.id.toString(),
            postsData.id.toString()
        )
    }

    override fun onStoryClick(storyData: CalendarStoryData) {
        try {
            Utils.storyDetail = storyData
            val action = MainFragmentDirections.actionMainFragmentToFollowingCalendarFragment()
            findNavController().navigate(action)
        } catch (e: Exception) {

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }
}