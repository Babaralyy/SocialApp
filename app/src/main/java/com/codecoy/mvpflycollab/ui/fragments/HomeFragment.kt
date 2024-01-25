package com.codecoy.mvpflycollab.ui.fragments

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.HomeCallback
import com.codecoy.mvpflycollab.callbacks.StoryCallback
import com.codecoy.mvpflycollab.databinding.FragmentHomeBinding
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.PostsAdapter
import com.codecoy.mvpflycollab.ui.adapters.stories.StoriesAdapter
import com.codecoy.mvpflycollab.utils.Constant.TAG


class HomeFragment : Fragment(), HomeCallback, StoryCallback {

    private lateinit var activity: MainActivity
    private lateinit var storiesAdapter: StoriesAdapter
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var storiesList: MutableList<String>
    private lateinit var postsList: MutableList<String>

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

        storiesList = arrayListOf()
        postsList = arrayListOf()

        mBinding.rvStories.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvStories.setHasFixedSize(true)

        mBinding.rvPosts.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mBinding.rvPosts.setHasFixedSize(true)

        setUpStoriesRecyclerView()
        setUpPostsRecyclerView()

        mBinding.ivMessenger.setOnClickListener {

            try {
                val action = MainFragmentDirections.actionMainFragmentToChatFragment()
                findNavController().navigate(action)
            } catch (e: Exception) {

            }
        }



        mBinding.drawerNavigation.itemIconTintList = null // To allow custom icon colors

        // Inflate the custom layout for navigation items
//        for (i in 0 until mBinding.drawerNavigation.menu.size()) {
//            val menuItem = mBinding.drawerNavigation.menu.getItem(i)
            val actionView = layoutInflater.inflate(R.layout.custom_nav_layout, null)
            // You can customize the content of the item if needed

            mBinding.drawerNavigation.addView(actionView)
//        }


        mBinding.navIcon.setOnClickListener {
            if (!mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                mBinding.drawerLayout.openDrawer(GravityCompat.START)
            } else {
                mBinding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        mBinding.drawerLayout.findViewById<ImageView>(R.id.ivCloseDrawer).setOnClickListener {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        mBinding.drawerLayout.findViewById<LinearLayout>(R.id.iProfile).setOnClickListener {

            mBinding.drawerLayout.closeDrawer(GravityCompat.START)

            try {
                val action = MainFragmentDirections.actionMainFragmentToAboutProfileFragment()
                findNavController().navigate(action)
            }catch (e: Exception){
                Log.i(TAG, "inIt: ${e.message}")
            }


        }

        mBinding.drawerLayout.findViewById<LinearLayout>(R.id.iJourney).setOnClickListener {

            mBinding.drawerLayout.closeDrawer(GravityCompat.START)

            try {
                val action = MainFragmentDirections.actionMainFragmentToJourneyFragment()
                findNavController().navigate(action)
            }catch (e: Exception){
                Log.i(TAG, "inIt: ${e.message}")
            }

        }

        mBinding.drawerLayout.findViewById<LinearLayout>(R.id.iPlaylist).setOnClickListener {

            mBinding.drawerLayout.closeDrawer(GravityCompat.START)

            try {
                val action = MainFragmentDirections.actionMainFragmentToPlayListFragment()
                findNavController().navigate(action)
            }catch (e: Exception){
                Log.i(TAG, "inIt: ${e.message}")
            }

        }


        mBinding.drawerLayout.findViewById<LinearLayout>(R.id.iCollab).setOnClickListener {

            mBinding.drawerLayout.closeDrawer(GravityCompat.START)

            try {
                val action = MainFragmentDirections.actionMainFragmentToMembersFragment()
                findNavController().navigate(action)
            }catch (e: Exception){
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

        storiesList.add("https://images.unsplash.com/photo-1488426862026-3ee34a7d66df?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8bW9kZWwlMjBpbWFnZXxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60")
        storiesList.add("https://manofmany.com/wp-content/uploads/2019/04/David-Gandy.jpg")
        storiesList.add("https://images.unsplash.com/photo-1524504388940-b1c1722653e1?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8bW9kZWwlMjBpbWFnZXxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60")
        storiesList.add("https://img.freepik.com/free-photo/portrait-handsome-confident-model-sexy-stylish-man-dressed-sweater-jeans-fashion-hipster-male-with-curly-hairstyle-posing-near-blue-wall-studio-isolated_158538-26600.jpg?w=2000")
        storiesList.add("https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NHx8bW9kZWwlMjBpbWFnZXxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60")
        storiesList.add("https://img.freepik.com/free-photo/portrait-handsome-confident-model-sexy-stylish-man-dressed-sweater-jeans-fashion-hipster-male-with-curly-hairstyle-posing-near-blue-wall-studio-isolated_158538-26600.jpg?w=2000")
        storiesList.add("https://media.photographycourse.net/wp-content/uploads/2022/04/08163010/Fashion-photography-poses-feature-image.png")
        storiesList.add("https://menshaircuts.com/wp-content/uploads/2022/06/male-models-jon-kortajarena-683x1024.jpg")
        storiesList.add("https://manofmany.com/wp-content/uploads/2019/04/David-Gandy.jpg")
        storiesList.add("https://menshaircuts.com/wp-content/uploads/2022/06/male-models-jon-kortajarena-683x1024.jpg")
        storiesList.add("https://images.unsplash.com/photo-1494790108377-be9c29b29330?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1374&q=80")
        storiesList.add("https://fastly.picsum.photos/id/5/200/300.jpg?hmac=1TWjKFT7_MRP0ApEyDUA3eCP0HXaKTWJfHgVjwGNoZU")
        storiesList.add("https://picsum.photos/200/300/?image=5")


        storiesAdapter = StoriesAdapter(storiesList, activity, this)
        mBinding.rvStories.adapter = storiesAdapter


    }

    private fun setUpPostsRecyclerView() {

        postsList.add("https://images.unsplash.com/photo-1488426862026-3ee34a7d66df?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8bW9kZWwlMjBpbWFnZXxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60")
        postsList.add("https://manofmany.com/wp-content/uploads/2019/04/David-Gandy.jpg")
        postsList.add("https://images.unsplash.com/photo-1524504388940-b1c1722653e1?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8bW9kZWwlMjBpbWFnZXxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60")
        postsList.add("https://img.freepik.com/free-photo/portrait-handsome-confident-model-sexy-stylish-man-dressed-sweater-jeans-fashion-hipster-male-with-curly-hairstyle-posing-near-blue-wall-studio-isolated_158538-26600.jpg?w=2000")
        postsList.add("https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NHx8bW9kZWwlMjBpbWFnZXxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60")
        postsList.add("https://img.freepik.com/free-photo/portrait-handsome-confident-model-sexy-stylish-man-dressed-sweater-jeans-fashion-hipster-male-with-curly-hairstyle-posing-near-blue-wall-studio-isolated_158538-26600.jpg?w=2000")
        postsList.add("https://media.photographycourse.net/wp-content/uploads/2022/04/08163010/Fashion-photography-poses-feature-image.png")
        postsList.add("https://menshaircuts.com/wp-content/uploads/2022/06/male-models-jon-kortajarena-683x1024.jpg")
        postsList.add("https://manofmany.com/wp-content/uploads/2019/04/David-Gandy.jpg")
        postsList.add("https://menshaircuts.com/wp-content/uploads/2022/06/male-models-jon-kortajarena-683x1024.jpg")
        postsList.add("https://images.unsplash.com/photo-1494790108377-be9c29b29330?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1374&q=80")


        postsAdapter = PostsAdapter(postsList, activity, this)
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