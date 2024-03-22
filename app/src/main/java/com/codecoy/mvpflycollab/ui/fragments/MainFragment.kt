package com.codecoy.mvpflycollab.ui.fragments

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.ImageClickCallback
import com.codecoy.mvpflycollab.databinding.FragmentMainBinding
import com.codecoy.mvpflycollab.databinding.NewPostBottomDialogLayBinding
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.ShowPostImageAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.PostsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MultipartBody


class MainFragment : Fragment(), ImageClickCallback {

    private lateinit var viewModel: PostsViewModel
    private lateinit var activity: MainActivity
    private lateinit var showPostImageAdapter: ShowPostImageAdapter

    private var currentUser: UserLoginData? = null

    private var bottomBinding: NewPostBottomDialogLayBinding? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    private lateinit var imagePartList: MutableList<MultipartBody.Part>

    private var dialog: Dialog? = null

    private lateinit var textViewList: ArrayList<TextView>
    private lateinit var fragmentList : ArrayList<Fragment>

    private val requestImgPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickImgMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            imagePermission()
        }
    }

    private val pickImgMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                showSingleImage(uri)
            } else {
                Toast.makeText(activity, "No media selected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun showSingleImage(uri: Uri) {
        viewModel.mediaImgList.clear()
        viewModel.mediaImgList.add(0, uri)
        viewModel.mediaImgList.distinct()
        showPostImageAdapter = ShowPostImageAdapter(viewModel.mediaImgList, activity, this)
        bottomBinding?.rvMediaImage?.adapter = showPostImageAdapter
    }

    private lateinit var mBinding: FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMainBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mBinding.bottomNavigation.selectedItemId = R.id.navigation_home
            }
        }

        // Add onBackPressedCallback to the activity
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    private fun inIt() {
        textViewList = arrayListOf()
        fragmentList = arrayListOf()

        dialog = Constant.getDialog(activity)
        currentUser = Utils.getUserFromSharedPreferences(activity)
        imagePartList = arrayListOf()
        activity.replaceFragment(HomeFragment())

        setUpBottomDialog()
        setUpViewModel()


/*
        setUpBottomNavigation()
*/

    /*    mBinding.ivCreatePost.setOnClickListener {
            showAddPostBottomDialog()
        }
*/

        mBinding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    activity.replaceFragment(HomeFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_calendar -> {
                    activity.replaceFragment(CalendarFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_add_post -> {
                    showAddPostBottomDialog()
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_requests -> {
                    activity.replaceFragment(RequestsFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_notification -> {
                    activity.replaceFragment(NotificationsFragment())
                    return@setOnItemSelectedListener true
                }

            }
            return@setOnItemSelectedListener true
        }

    }

/*    private fun setUpBottomNavigation() {

        mBinding.tvHome.isSelected = true

        textViewList.clear()
        textViewList.add(mBinding.tvHome)
        textViewList.add(mBinding.tvCalendar)
        textViewList.add( mBinding.tvSavePost)
        textViewList.add(mBinding.tvProfile)

        fragmentList.clear()
        fragmentList.add(HomeFragment())
        fragmentList.add(CalendarFragment())
        fragmentList.add(SavedPostFragment())
        fragmentList.add(ProfileDetailFragment())

        textViewList.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                activity.replaceFragment(fragmentList[index])
                textViewList.forEach { it.isSelected = false }
                textView.isSelected = true
            }
        }

    }*/

    override fun onResume() {
        super.onResume()
        if (Utils.isFromProfile){
            mBinding.bottomNavigation.selectedItemId = R.id.navigation_calendar
            Utils.isFromProfile = false
        }
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

        viewModel.addNewPostResponseLiveData.observe(this) { response ->

            Log.i(Constant.TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val userResponse = response.body()
                if ( userResponse?.success == true) {

                    try {
                        bottomSheetDialog?.dismiss()
                    } catch (e: Exception) {
                        Log.i(Constant.TAG, "navControllerException:: ${e.message}")
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
                Log.i(Constant.TAG, "addJourneyResponseLiveData:: exception $exception")
                dialog?.dismiss()
            }
        }
    }

    private fun setUpBottomDialog() {
        bottomBinding = NewPostBottomDialogLayBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(activity)
        bottomBinding?.root?.let { bottomSheetDialog?.setContentView(it) }

        bottomBinding?.rvMediaImage?.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

    }

    private fun setUpViewModel() {

        val mApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val userRepository = MvpRepository(mApi)

        viewModel = ViewModelProvider(
            this,
            MvpViewModelFactory(userRepository)
        )[PostsViewModel::class.java]
    }

    private fun showAddPostBottomDialog() {
        bottomBinding?.tvMediaImage?.setOnClickListener {
            imagePermission()
        }
        bottomBinding?.btnAddPost?.setOnClickListener {
            checkBottomCredentials()
        }
        bottomSheetDialog?.show()
    }

    private fun checkBottomCredentials() {

        val des = bottomBinding?.etDes?.text.toString()

        if (des.isEmpty()) {
            bottomBinding?.etDes?.error = "Description is required"
            return
        }
        if (viewModel.mediaImgList.isEmpty()) {
            Toast.makeText(activity, "Please add post image", Toast.LENGTH_SHORT).show()
            return
        }
        if (des.isNotEmpty() &&  viewModel.mediaImgList.isNotEmpty()) {
            addNewPost(des)
        }
    }

    private fun addNewPost(des: String) {
        imagePartList.clear()

        for (item in viewModel.mediaImgList) {

            Utils.getRealPathFromImgURI(activity, item)
                .let {
                    Log.i(Constant.TAG, "mediaImgList:: getRealPathFromURI $it")

                    Utils.getFileFromPath(it)?.let { file ->

                        Log.i(Constant.TAG, "mediaImgList:: getFileFromPath $file")

                        val part = Utils.getPartFromFile("image/*", "post_images[]", file)


                        Log.i(Constant.TAG, "mediaImgList:: getPartFromFile $part")

                        imagePartList.add(part)

                        viewModel.addNewPost(
                            "Bearer " + currentUser?.token.toString(),
                            Utils.createTextRequestBody(currentUser?.id.toString()),
                            Utils.createTextRequestBody("combat_sports"),
                            Utils.createTextRequestBody("karate"),
                            Utils.createTextRequestBody(des),
                            Utils.createTextRequestBody("#Chill #Enjoy #2k24 2"),
                            Utils.createTextRequestBody("2.345786348789"),
                            Utils.createTextRequestBody("2.389786348754"),
                            imagePartList,
                        )
                    }
                }
        }

    }

    private fun imagePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // If Android version is 13 or above
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestImgPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                pickImgMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(activity, "Permission not granted", Toast.LENGTH_SHORT).show()
                // Request the permission
                requestImgPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            } else {
                pickImgMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }

    override fun onImageClick(imgPath: Uri) {

    }

}