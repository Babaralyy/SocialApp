package com.codecoy.mvpflycollab.ui.fragments

import android.app.Dialog
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
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
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar
import java.util.Date


class HomeFragment : Fragment(), HomeCallback, StoryCallback {


    //    private lateinit var activity: MainActivity

    private lateinit var viewModel: PostsViewModel
    private lateinit var commentsViewModel: CommentsViewModel

    private var dialog: Dialog? = null
    private var currentUser: UserLoginData? = null

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
        setUpBottomDialog()
        responseFromViewModel()

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

    override fun onResume() {
        super.onResume()

    }

    private fun setUpBottomDialog() {
        bottomBinding = CommentsBottomDialogLayBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomBinding.root)

        bottomBinding.rvComments.layoutManager = LinearLayoutManager(requireContext())
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
                Log.i(TAG, "dialog --> :: show")
            } else {
                dialog?.dismiss()

                Log.i(TAG, "dialog --> :: dismiss")

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
                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(requireContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show()
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
                        getAllPosts()
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
                        getAllPosts()
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
                        getAllPosts()
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

        viewModel.exceptionLiveData.observe(this) { exception ->

            exception.let {
                Log.i(TAG, "addJourneyResponseLiveData:: exception $it")
                dialog?.dismiss()
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
                navigateTo { MainFragmentDirections.actionMainFragmentToAboutProfileFragment()}
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
            Utils.clearSharedPreferences(requireContext())
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

    private fun loadImageAndSetText(imageViewId: Int, textId: Int, imageUrl: String?, text: String?) {
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

//        if (calendarStoryData.isNotEmpty()) {
//            mBinding.rvStories.visibility = View.VISIBLE
//        } else {
//            mBinding.rvStories.visibility = View.GONE
//        }
        
        storiesAdapter = StoriesAdapter(calendarStoryData, requireContext(), this)
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
                        getAllPosts()

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
            Log.i(TAG, "onStoryClick: ${e.message}")
        }
    }

/*    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }*/
}