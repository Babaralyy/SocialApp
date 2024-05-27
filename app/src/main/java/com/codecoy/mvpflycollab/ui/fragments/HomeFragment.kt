package com.codecoy.mvpflycollab.ui.fragments

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.HomeCallback
import com.codecoy.mvpflycollab.callbacks.RemovePlayer
import com.codecoy.mvpflycollab.callbacks.StoryCallback
import com.codecoy.mvpflycollab.databinding.CommentsBottomDialogLayBinding
import com.codecoy.mvpflycollab.databinding.FragmentHomeBinding
import com.codecoy.mvpflycollab.databinding.PostItemViewBinding
import com.codecoy.mvpflycollab.datamodels.CalendarStoryData
import com.codecoy.mvpflycollab.datamodels.CommentsData
import com.codecoy.mvpflycollab.datamodels.MessageEvent
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.datamodels.UserPostsData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.ImageSliderAdapter
import com.codecoy.mvpflycollab.ui.adapters.PostCommentsAdapter
import com.codecoy.mvpflycollab.ui.adapters.PostsAdapter
import com.codecoy.mvpflycollab.ui.adapters.stories.StoriesAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.CommentsViewModel
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.PostsViewModel
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import de.hdodenhof.circleimageview.CircleImageView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.Calendar
import java.util.Date


class HomeFragment: Fragment(), HomeCallback, StoryCallback {

    private lateinit var activity: MainActivity

    private lateinit var viewModel: PostsViewModel
    private lateinit var commentsViewModel: CommentsViewModel

    private var dialog: Dialog? = null
    private var currentUser: UserLoginData? = null

    private lateinit var storiesAdapter: StoriesAdapter


    lateinit var postsAdapter: PostsAdapter


    private lateinit var postCommentsAdapter: PostCommentsAdapter

    private lateinit var postItemViewBinding: PostItemViewBinding

    private lateinit var googleApiClient: GoogleApiClient

    private lateinit var bottomBinding: CommentsBottomDialogLayBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private var currentPage = 1
    private var isLoading = false


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Continue the action or workflow in your
            // app.

        } else {
            showSnackBar(mBinding.root, "Please allow notification permission")
        }
    }

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
        dialog = Constant.getDialog(requireContext())

        currentUser = Utils.getUserFromSharedPreferences(requireContext())

        mBinding.rvStories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvStories.setHasFixedSize(true)

        mBinding.rvPosts.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.rvPosts.setHasFixedSize(true)

        postsAdapter = PostsAdapter(mutableListOf(), requireContext(), this)
        mBinding.rvPosts.adapter = postsAdapter

        setUpViewModel()
        setUpDrawer()
        getAllPosts()
        getAllStories()
        getLevels()
        setUpBottomDialog()
        responseFromViewModel()
        initGoogleSignIn()

        mBinding.ivMessenger.setOnClickListener {
            try {
                val action = MainFragmentDirections.actionMainFragmentToStartChatFragment()
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


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        requestNotificationPermission()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

    }



    // Initialize Google Sign-In
    private fun initGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleApiClient = GoogleApiClient.Builder(requireContext())
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
        googleApiClient.connect()
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {

        when {
            ContextCompat.checkSelfPermission(
                activity, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                Log.e(TAG, "onCreate: PERMISSION GRANTED")
            }

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {

            }

            else -> {
                // The registered ActivityResultCallback gets the result of this request
                requestPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }


    private fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun setUpBottomDialog() {
        bottomBinding = CommentsBottomDialogLayBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomBinding.root)

        bottomBinding.rvComments.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun getAllPosts() {



        Log.i(TAG, "getAllPosts:: ${currentUser?.id.toString()} ${currentUser?.token.toString()}")
        viewModel.allUserPosts(
            "Bearer " + currentUser?.token.toString(),
            currentUser?.id.toString(), currentPage
        )
    }

    private fun getAllStories() {
        viewModel.allStories("Bearer " + currentUser?.token.toString(), currentUser?.id.toString())
    }

    private fun getLevels() {
        viewModel.userLevels("Bearer " + currentUser?.token.toString(), currentUser?.id.toString())

    }

    private fun responseFromViewModel() {
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
//                dialog?.show()
                mBinding.progressBar.visibility = View.VISIBLE
                Log.i(TAG, "dialog --> :: show")
            } else {
//                dialog?.dismiss()
                mBinding.progressBar.visibility = View.GONE
                Log.i(TAG, "dialog --> :: dismiss")

            }
        }

        viewModel.allUsersPostsResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val postsResponse = response.body()
                if (postsResponse != null && postsResponse.success == true) {

                    try {
                        setUpPostsRecyclerView(postsResponse.userPostsResponseData.data)
                    } catch (e: Exception) {
                        Log.i(TAG, "navControllerException:: ${e.message}")
                    }

                } else {
                    mBinding.group.visibility = View.VISIBLE
                    showSnackBar(mBinding.root, response.body()?.message ?: "Unknown error")
                }
            } else if (response.code() == 401) {

            } else {
                showSnackBar(mBinding.root, response.errorBody().toString())
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
                    showSnackBar(mBinding.root, response.body()?.message ?: "Unknown error")
                }
            } else if (response.code() == 401) {

            } else {
                showSnackBar(mBinding.root, response.errorBody().toString())
            }
        }

        viewModel.likePostResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val storiesResponse = response.body()
                if (storiesResponse != null && storiesResponse.success == true) {

                    try {
//                        getAllPosts()
                    } catch (e: Exception) {
                        Log.i(TAG, "navControllerException:: ${e.message}")
                    }

                } else {
                    showSnackBar(mBinding.root, response.body()?.message ?: "Unknown error")
                }
            } else if (response.code() == 401) {

            } else {
                showSnackBar(mBinding.root, response.errorBody().toString())
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
                    showSnackBar(mBinding.root, response.body()?.message ?: "Unknown error")
                }
            } else if (response.code() == 401) {

            } else {
                showSnackBar(mBinding.root, response.errorBody().toString())
            }
        }

        viewModel.savePostResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val postResponse = response.body()
                if (postResponse != null && postResponse.success == true) {

                    try {
//                        getAllPosts()
                    } catch (e: Exception) {
                        Log.i(TAG, "navControllerException:: ${e.message}")
                    }

                } else {
                    showSnackBar(mBinding.root, response.body()?.message ?: "Unknown error")
                }
            } else if (response.code() == 401) {

            } else {
                showSnackBar(mBinding.root, response.errorBody().toString())
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
                    showSnackBar(mBinding.root, response.body()?.message ?: "Unknown error")
                }
            } else if (response.code() == 401) {

            } else {
                showSnackBar(mBinding.root, response.errorBody().toString())
            }
        }

        viewModel.levelsResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val levelsResponse = response.body()
                if (levelsResponse != null && levelsResponse.success == true) {

                    try {
                        levelsResponse.userLevelsData?.let {
                            Utils.saveLevelsToSharedPreferences(
                                requireContext(),
                                it
                            )
                        }
                    } catch (e: Exception) {
                        Log.i(TAG, "navControllerException:: ${e.message}")
                    }

                } else {
                    showSnackBar(mBinding.root, response.body()?.message ?: "Unknown error")
                }
            } else if (response.code() == 401) {

            } else {
                showSnackBar(mBinding.root, response.errorBody().toString())
            }
        }

        viewModel.exceptionLiveData.observe(this) { exception ->

            exception.let {
//                dialog?.dismiss()
                mBinding.progressBar.visibility = View.GONE
                showSnackBar(mBinding.root, it.message.toString())
            }
        }
    }

    private fun setUpCommentsRecyclerView(commentsData: ArrayList<CommentsData>) {

        val hasComments = commentsData.isNotEmpty()
        bottomBinding.tvNoComment.visibility = if (hasComments) View.GONE else View.VISIBLE

        postCommentsAdapter = PostCommentsAdapter(commentsData, requireContext())
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
            val isOpen = mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)
            mBinding.drawerLayout.apply {
                if (!isOpen) openDrawer(GravityCompat.START) else closeDrawer(GravityCompat.START)
            }
        }

        drawersClicks()
    }

    private fun drawersClicks() {

        mBinding.drawerLayout.findViewById<ImageView>(R.id.ivCloseDrawer).setOnClickListener {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        loadImageAndSetText(
            R.id.ivUserProfile,
            R.id.tvProfileName,
            currentUser?.profileImg,
            currentUser?.name
        )

        setupDrawerItems()


    }

    private fun setupDrawerItems() {

        mBinding.drawerLayout.findViewById<CircleImageView>(R.id.ivUserProfile).setOnClickListener {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
            try {
                navigateTo { MainFragmentDirections.actionMainFragmentToAboutProfileFragment() }
            } catch (e: Exception) {
                Log.i(TAG, "init: ${e.message}")
            }
        }

        setDrawerItemClick(R.id.iProfile) {
            navigateTo { MainFragmentDirections.actionMainFragmentToAboutProfileFragment() }
        }

        setDrawerItemClick(R.id.iEditProfile) {
            navigateTo { MainFragmentDirections.actionMainFragmentToEditProfileFragment() }
        }

        setDrawerItemClick(R.id.iJourney) {
            Utils.userId = currentUser?.id.toString()
            navigateTo { MainFragmentDirections.actionMainFragmentToJourneyFragment() }
        }

        setDrawerItemClick(R.id.iPlaylist) {
            Utils.userId = currentUser?.id.toString()
            navigateTo { MainFragmentDirections.actionMainFragmentToPlayListFragment() }
        }

        setDrawerItemClick(R.id.iCollab) {
            Utils.userId = currentUser?.id.toString()
            navigateTo { MainFragmentDirections.actionMainFragmentToMembersFragment() }
        }

        setDrawerItemClick(R.id.iLevels) {
            navigateTo { MainFragmentDirections.actionMainFragmentToLevelsFragment() }
        }

        setDrawerItemClick(R.id.iShareUs) {
            showToast("Clicked")
        }

        setDrawerItemClick(R.id.iRateUs) {
            showToast("Clicked")
        }

        setDrawerItemClick(R.id.iLogout) {

            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback {

            }

            Utils.clearSharedPreferences(requireContext())
            Utils.saveNotificationStateIntoPref(activity, false)
            navigateTo { MainFragmentDirections.actionMainFragmentToSignInFragment() }
        }
    }

    private fun setDrawerItemClick(itemId: Int, action: () -> Unit) {
        mBinding.drawerLayout.findViewById<LinearLayout>(itemId).setOnClickListener {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
            try {
                action()
            } catch (e: Exception) {
                Log.i(TAG, "init: ${e.message}")
            }
        }
    }

    private fun loadImageAndSetText(
        imageViewId: Int,
        textId: Int,
        imageUrl: String?,
        text: String?
    ) {
        Glide.with(requireContext())
            .load(Constant.MEDIA_BASE_URL + imageUrl)
            .placeholder(R.drawable.img)
            .into(mBinding.drawerLayout.findViewById(imageViewId))
        mBinding.drawerLayout.findViewById<TextView>(textId).text = text
    }

    private fun navigateTo(destinationAction: () -> NavDirections) {
        findNavController().navigate(destinationAction())
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setUpStoriesRecyclerView(calendarStoryData: ArrayList<CalendarStoryData>) {

        val hasStories = calendarStoryData.isNotEmpty()
        mBinding.rvStories.visibility = if (hasStories) View.VISIBLE else View.GONE

        storiesAdapter = StoriesAdapter(calendarStoryData, requireContext(), this)
        mBinding.rvStories.adapter = storiesAdapter
    }

    private fun setUpPostsRecyclerView(userPostsData: ArrayList<UserPostsData>) {


        if (userPostsData.isNotEmpty()) {
//            mBinding.group.visibility = View.GONE
            ++currentPage
        } else {
//            mBinding.group.visibility = View.VISIBLE
        }

        postsAdapter.releaseAllPlayers()
        postsAdapter.setItemList(userPostsData)

        if (Utils.postId != null) {
            val index = userPostsData.indexOfFirst { it.id == Utils.postId?.toInt() }
            // Print the index
            if (index != -1) {
                mBinding.rvPosts.smoothScrollToPosition(index)
            }
            Utils.postId = null
        }

        isLoading = false

        mBinding.rvPosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Find the center item
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val centerPosition =
                    layoutManager.findFirstVisibleItemPosition() + (layoutManager.findLastVisibleItemPosition() - layoutManager.findFirstVisibleItemPosition()) / 2


                if (!isLoading && layoutManager.findLastVisibleItemPosition() == postsAdapter.itemCount - 1) {
                    isLoading = true

                    Log.i(TAG, "onScrolled:: inside $currentPage")
                    getAllPosts()
                }

   /*             val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                Log.i(TAG, "onScrolled:: totalItemCount $totalItemCount lastVisibleItem $lastVisibleItem")

                if (totalItemCount - 1 <= lastVisibleItem) {
                    ++currentPage
                    Log.i(TAG, "onScrolled:: inside $currentPage")
                    getAllPosts()
                }
*/
                try {
                    for (i in 0 until layoutManager.childCount) {
                        val view = layoutManager.getChildAt(i)
                        val viewHolder =
                            view?.let { recyclerView.getChildViewHolder(it) } as PostsAdapter.ViewHolder
                        val viewPager = viewHolder.getViewPager()
                        val adapter = viewPager.adapter as ImageSliderAdapter

                        if (layoutManager.findViewByPosition(centerPosition) == view) {
                            // Play the video in the center item
                            adapter.playPlayer(viewPager.currentItem)
                        } else {
                            // Pause videos in non-center items
                            adapter.pausePlayer(viewPager.currentItem)
                        }
                    }
                }catch (e: Exception){
                    Log.i(TAG, "onScrolled:: ${e.message}")
                }


            }
        })

    }

    override fun onMenuClick(postsData: UserPostsData, mBinding: PostItemViewBinding) {
        showPopupMenu(mBinding.ivmenu, postsData)
    }

    private fun showPopupMenu(view: View, postsData: UserPostsData) {
        val popupMenu = PopupMenu(requireContext(), view)
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
        showAlertDialog(postsData)

    }

    private fun showAlertDialog(postsData: UserPostsData) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Post")
        builder.setMessage("Are you sure you want to delete?")
        builder.setPositiveButton("Yes") { dialog, which ->
            viewModel.deletePost("Bearer " + currentUser?.token.toString(), postsData.id.toString())
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, which ->

            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onCommentsClick(postsData: UserPostsData) {
        showBottomCommentDialog(postsData)
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
                    showSnackBar(mBinding.root, response.body()?.message ?: "Unknown error")
                }
            } else if (response.code() == 401) {

            } else {
                showSnackBar(mBinding.root, response.errorBody().toString())
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
                    showSnackBar(mBinding.root, response.body()?.message ?: "Unknown error")
                }
            } else if (response.code() == 401) {

            } else {
                showSnackBar(mBinding.root, response.errorBody().toString())
            }
        }

        viewModel.exceptionLiveData.observe(this) { exception ->
            if (exception != null) {
                bottomBinding.progressBar.visibility = View.GONE
                showSnackBar(mBinding.root, exception.message.toString())
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

    override fun onUserClick(postsData: UserPostsData, mBinding: PostItemViewBinding) {
        try {
            if (postsData.userId.toString() == currentUser?.id.toString()) {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToAboutProfileFragment())
            } else {
                Utils.userId = postsData.userId.toString()
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToUserProfileDetailsFragment())
            }
        } catch (e: Exception) {
            Log.i(TAG, "clickListeners:: ${e.message}")
        }
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

    override fun onStoryClick(storyData: CalendarStoryData) {
        try {
            Utils.storyDetail = storyData
            val action = MainFragmentDirections.actionMainFragmentToFollowingCalendarFragment()
            findNavController().navigate(action)
        } catch (e: Exception) {
            Log.i(TAG, "onStoryClick: ${e.message}")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }


    @Subscribe
    fun onMessageEvent(event: MessageEvent) {
        postsAdapter.releaseAllPlayers()
    }

    override fun onStop() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        super.onStop()
        Log.i(TAG, "lifecycle callbacks:: onStop ")

    }

    override fun onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        super.onDestroy()
        Log.i(TAG, "lifecycle callbacks:: onDestroy ")
    }
}