package com.codecoy.mvpflycollab.ui.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.HomeCallback
import com.codecoy.mvpflycollab.callbacks.StoryCallback
import com.codecoy.mvpflycollab.databinding.FragmentHomeBinding
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.datamodels.UserPostsData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.PostsAdapter
import com.codecoy.mvpflycollab.ui.adapters.stories.StoriesAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.PostsViewModel


class HomeFragment : Fragment(), HomeCallback, StoryCallback {

    private lateinit var viewModel: PostsViewModel
    private var dialog: Dialog? = null

    private var currentUser: UserLoginData? = null

    private lateinit var activity: MainActivity
    private lateinit var storiesAdapter: StoriesAdapter
    private lateinit var postsAdapter: PostsAdapter


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

        setUpViewModel()
        setUpDrawer()
        getAllPosts()

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
                val action = MainFragmentDirections.actionMainFragmentToProfileDetailFragment()
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.i(TAG, "inIt: ${e.message}")
            }
        }
    }

    private fun getAllPosts() {
        viewModel.allUserPosts("Bearer " + currentUser?.token.toString(), currentUser?.id.toString())
    }


    override fun onResume() {
        super.onResume()
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


        viewModel.exceptionLiveData.observe(this) { exception ->
            if (exception != null) {
                Log.i(TAG, "addJourneyResponseLiveData:: exception $exception")
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
        )[PostsViewModel::class.java]
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

        mBinding.drawerLayout.findViewById<LinearLayout>(R.id.iProfile).setOnClickListener {

            mBinding.drawerLayout.closeDrawer(GravityCompat.START)

            try {
                val action = MainFragmentDirections.actionMainFragmentToProfileDetailFragment()
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.i(TAG, "inIt: ${e.message}")
            }
        }

        mBinding.drawerLayout.findViewById<LinearLayout>(R.id.iJourney).setOnClickListener {

            mBinding.drawerLayout.closeDrawer(GravityCompat.START)

            try {
                val action = MainFragmentDirections.actionMainFragmentToJourneyFragment()
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.i(TAG, "inIt: ${e.message}")
            }

        }

        mBinding.drawerLayout.findViewById<LinearLayout>(R.id.iPlaylist).setOnClickListener {

            mBinding.drawerLayout.closeDrawer(GravityCompat.START)

            try {
                val action = MainFragmentDirections.actionMainFragmentToPlayListFragment()
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.i(TAG, "inIt: ${e.message}")
            }

        }

        mBinding.drawerLayout.findViewById<LinearLayout>(R.id.iCollab).setOnClickListener {

            mBinding.drawerLayout.closeDrawer(GravityCompat.START)

            try {
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
            Toast.makeText(activity, "Clicked", Toast.LENGTH_SHORT).show()
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun setUpStoriesRecyclerView() {

    /*    storiesAdapter = StoriesAdapter(storiesList, activity, this)
        mBinding.rvStories.adapter = storiesAdapter*/


    }

    private fun setUpPostsRecyclerView(userPostsData: ArrayList<UserPostsData>) {
        if (userPostsData.isNotEmpty()){
            mBinding.group.visibility = View.GONE
        } else {
            mBinding.group.visibility = View.VISIBLE
        }
        postsAdapter = PostsAdapter(userPostsData, activity, this)
        mBinding.rvPosts.adapter = postsAdapter
    }

    override fun onCommentsClick() {
        try {
            val action = MainFragmentDirections.actionMainFragmentToCommentsFragment()
            findNavController().navigate(action)
        } catch (e: Exception) {

        }
    }

    override fun onStoryClick() {
        try {
            val action = MainFragmentDirections.actionMainFragmentToViewStoryFragment()
            findNavController().navigate(action)
        } catch (e: Exception) {

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }


}