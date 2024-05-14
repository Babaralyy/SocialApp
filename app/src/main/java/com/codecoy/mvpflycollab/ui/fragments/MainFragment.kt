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
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
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
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.PostsViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import okhttp3.MultipartBody


class MainFragment : Fragment(), ImageClickCallback {

    private lateinit var placesClient: PlacesClient

    private lateinit var viewModel: PostsViewModel
    private lateinit var activity: MainActivity
    private lateinit var showPostImageAdapter: ShowPostImageAdapter

    private var currentUser: UserLoginData? = null

    private var bottomBinding: NewPostBottomDialogLayBinding? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    private lateinit var imagePartList: MutableList<MultipartBody.Part>

    private var dialog: Dialog? = null

    private lateinit var textViewList: ArrayList<TextView>
    private lateinit var fragmentList: ArrayList<Fragment>

    private var adapter: ArrayAdapter<String>? = null

    private val placeNames = mutableListOf<String>()

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
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uris != null) {
                showSingleImage(uris)
            } else {
                showSnackBar(mBinding.root, "No media selected")
            }
        }

    private fun showSingleImage(uriList: List<Uri>) {
        for (item in uriList) {
            viewModel.mediaImgList.add(item)
        }

        showPostImageAdapter = ShowPostImageAdapter(viewModel.mediaImgList, requireContext(), this)
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
                if (mBinding.bottomNavigation.selectedItemId != R.id.navigation_home) {
                    mBinding.bottomNavigation.selectedItemId = R.id.navigation_home

                    Log.i(TAG, "handleOnBackPressed:: if")

                } else {
                    activity.finish()
                    Log.i(TAG, "handleOnBackPressed:: else")
                }
            }
        }

        // Add onBackPressedCallback to the activity
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    private fun inIt() {

        textViewList = arrayListOf()
        fragmentList = arrayListOf()

        dialog = Constant.getDialog(requireContext())
        currentUser = Utils.getUserFromSharedPreferences(requireContext())
        imagePartList = arrayListOf()
        activity.replaceFragment(HomeFragment())

        // Initialize Places API
        Places.initialize(requireContext(), getString(R.string.google_maps_key))
        placesClient = Places.createClient(requireContext())

        setUpBottomDialog()
        setUpBottomNav()
        setUpViewModel()
        responseFromViewModel()

        if (Utils.isFromFollowingCalendar) {
            Utils.isFromFollowingCalendar = false
//            val menuItem = mBinding.bottomNavigation.menu.findItem(R.id.navigation_calendar)
//            menuItem?.isChecked = true
//            activity.replaceFragment(CalendarFragment())
        }

    }

    private fun setUpBottomNav() {
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


    override fun onResume() {
        super.onResume()
        if (Utils.isFromProfile) {
            mBinding.bottomNavigation.selectedItemId = R.id.navigation_calendar
            Utils.isFromProfile = false
        }
    }

    private fun responseFromViewModel() {

        viewModel.loading.observe(this) { isLoading ->
            dialog?.apply { if (isLoading) show() else dismiss() }
        }

        viewModel.addNewPostResponseLiveData.observe(this) { response ->
            Log.i(Constant.TAG, "registerUser:: response $response")

            when (response.code()) {
                200 -> {
                    val userResponse = response.body()
                    if (userResponse?.success == true) {
                        viewModel.mediaImgList.clear()
                        bottomSheetDialog?.dismiss()
                        val menuItem = mBinding.bottomNavigation.menu.findItem(R.id.navigation_home)
                        menuItem?.isChecked = true
                        activity.replaceFragment(HomeFragment())
                    } else {
                        showSnackBar(mBinding.root, response.body()?.message ?: "Unknown error")
                    }
                }

                401 -> {
                    // Handle 401 Unauthorized
                }

                else -> showSnackBar(mBinding.root, "Something went wrong")
            }
        }


        viewModel.exceptionLiveData.observe(this) { exception ->

            exception?.let {
                Log.i(TAG, "addJourneyResponseLiveData:: exception $exception")
                dialog?.dismiss()
            }

        }
    }


    private fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun setUpBottomDialog() {
        bottomBinding = NewPostBottomDialogLayBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomBinding?.root?.let { bottomSheetDialog?.setContentView(it) }

        bottomBinding?.rvMediaImage?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true)

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

        // Start fetching predictions when the user starts typing
        bottomBinding?.etLoc?.addTextChangedListener { text ->
            if (!text.isNullOrEmpty()) {
                searchLocation(text.toString())
            }
        }
        bottomBinding?.btnAddPost?.setOnClickListener {
            checkBottomCredentials()
        }
        bottomSheetDialog?.show()
    }

    private fun searchLocation(locationName: String) {

        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

        val request =
            com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest.builder()
                .setQuery(locationName)
                .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                if (response.autocompletePredictions.isNotEmpty()) {
                    val placeId = response.autocompletePredictions[0].placeId


                    response.autocompletePredictions.forEach { prediction ->
                        placeNames.add(prediction.getPrimaryText(null).toString())
                    }

                    adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        placeNames.distinct()
                    )

                    bottomBinding?.etLoc?.setAdapter(adapter)
                    placeNames.clear()

                    // Fetch details of the place using Place ID
                    val placeRequest =
                        com.google.android.libraries.places.api.net.FetchPlaceRequest.newInstance(
                            placeId,
                            fields
                        )
                    placesClient.fetchPlace(placeRequest)
                        .addOnSuccessListener { fetchPlaceResponse ->
                            val place = fetchPlaceResponse.place




//                            place.name?.let { placeNames.add(it) }





                            Log.i(TAG, "searchLocation:: ${place.name}")

//                            bottomBinding?.etLoc?.setText(place.name)
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                requireContext(),
                                "Failed to fetch place details: $exception",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                } else {
                    Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT)
                    .show()
            }

    }

    private fun checkBottomCredentials() {

        val description = bottomBinding?.etDes?.text.toString().trim()
        val loc = bottomBinding?.etLoc?.text.toString().trim()

        if (description.isEmpty()) {
            bottomBinding?.etDes?.error = "Description is required"
            return
        }
        if (viewModel.mediaImgList.isEmpty()) {
            showSnackBar(mBinding.root, "Please add post image")
            return
        }

        addNewPost(description, loc)
        bottomBinding?.etDes?.setText("")

    }

    private fun addNewPost(description: String, loc: String) {
        imagePartList.clear()

        imagePartList = viewModel.mediaImgList.mapNotNull { item ->
            Utils.getRealPathFromImgURI(requireContext(), item).let { path ->
                Utils.getFileFromPath(path)?.let { file ->
                    Utils.getPartFromFile("image/*", "post_images[]", file)
                }
            }
        }.toMutableList()

        viewModel.addNewPost(
            "Bearer " + currentUser?.token.toString(),
            Utils.createTextRequestBody(currentUser?.id.toString()),
            Utils.createTextRequestBody("combat_sports"),
            Utils.createTextRequestBody("karate"),
            Utils.createTextRequestBody(description),
            Utils.createTextRequestBody("#Chill #Enjoy #2k24 2"),
            Utils.createTextRequestBody("2.345786348789"),
            Utils.createTextRequestBody("2.389786348754"),
            Utils.createTextRequestBody(loc),
            imagePartList,
        )

    }

    private fun imagePermission() {

        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            showSnackBar(mBinding.root, "Permission not granted")

            requestImgPermissionLauncher.launch(permission)
        } else {
            pickImgMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    override fun onImageClick(imgPath: Uri) {

    }

    override fun onImgRemove(position: Int) {
        viewModel.mediaImgList.removeAt(position)
        showPostImageAdapter.notifyDataSetChanged()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }
}