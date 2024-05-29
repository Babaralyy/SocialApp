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
import android.view.WindowManager
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
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.ImageClickCallback
import com.codecoy.mvpflycollab.callbacks.playlistdetailsvideo.VideoClickCallback
import com.codecoy.mvpflycollab.chat.socket.SocketManager
import com.codecoy.mvpflycollab.databinding.FragmentMainBinding
import com.codecoy.mvpflycollab.databinding.NewPostBottomDialogLayBinding
import com.codecoy.mvpflycollab.databinding.ShowImageDialogBinding
import com.codecoy.mvpflycollab.databinding.ShowVideoDialogBinding
import com.codecoy.mvpflycollab.datamodels.MessageEvent
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.AddPostImagesAdapter
import com.codecoy.mvpflycollab.ui.adapters.AddPostVideosAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.utils.MyApplicationClass
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.PostsViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.net.URISyntaxException
import kotlin.coroutines.resumeWithException


class MainFragment : Fragment(), ImageClickCallback, VideoClickCallback {

    private lateinit var placesClient: PlacesClient

    private lateinit var viewModel: PostsViewModel
    private lateinit var activity: MainActivity



    private lateinit var addPostImagesAdapter: AddPostImagesAdapter
    private lateinit var addPostVideosAdapter: AddPostVideosAdapter

    private var currentUser: UserLoginData? = null

    private var bottomBinding: NewPostBottomDialogLayBinding? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    private lateinit var imagePartList: MutableList<MultipartBody.Part>
    private lateinit var videoPartList: MutableList<MultipartBody.Part>

    private var dialog: Dialog? = null

    private lateinit var textViewList: ArrayList<TextView>
    private lateinit var fragmentList: ArrayList<Fragment>

    private var adapter: ArrayAdapter<String>? = null

    private val placeNames = mutableListOf<String>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            imagePermission()
        }
    }

    private val pickMedia =
        registerForActivityResult((ActivityResultContracts.PickMultipleVisualMedia(5))) { uris ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uris != null) {
                showSingleImage(uris)
            } else {
                Toast.makeText(requireContext(), "No media selected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun showSingleImage(uris: List<Uri>) {

        for (item in uris) {
            viewModel.mediaImgList.add(item)
        }
        addPostImagesAdapter = AddPostImagesAdapter(viewModel.mediaImgList, requireContext(), this)
        bottomBinding?.rvPostImage?.adapter = addPostImagesAdapter

    }

    private val requestVidPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickVidMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
        } else {
            videoPermission()
        }
    }

    private val pickVidMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(2)) { uris ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uris != null) {
                showSingleVideo(uris)
            } else {
                Toast.makeText(requireContext(), "No media selected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun showSingleVideo(uris: List<Uri>) {

        for (item in uris) {
            viewModel.mediaVidList.add(item)
        }

        addPostVideosAdapter = AddPostVideosAdapter(viewModel.mediaVidList, requireContext(), this)
        bottomBinding?.rvPostVideo?.adapter = addPostVideosAdapter
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

    @OptIn(DelicateCoroutinesApi::class)
    private fun inIt() {

        textViewList = arrayListOf()
        fragmentList = arrayListOf()
        imagePartList = arrayListOf()
        videoPartList = arrayListOf()

        dialog = Constant.getDialog(requireContext())
        currentUser = Utils.getUserFromSharedPreferences(requireContext())

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

        MyApplicationClass.socketManager = SocketManager()
        MyApplicationClass.socketManager?.connect()

        try {
            GlobalScope.launch {
                try {
                    connectToSocket()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

    }

    private suspend fun connectToSocket() {
        withContext<Socket?>(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                try {

                    SocketManager.socket?.on("user_got_online") {

                        CoroutineScope(Dispatchers.Main).launch {
                            val jsonObject = it[0] as JSONObject
                            Log.i(TAG, "main socket:: message $jsonObject")
                        }
                    }
                    SocketManager.socket?.on(Socket.EVENT_CONNECT_ERROR) { error ->
                        CoroutineScope(Dispatchers.Main).launch {
                            Log.i(TAG, "main socket:: $error ")
                        }
                    }
                    SocketManager.socket?.on(Socket.EVENT_DISCONNECT) {
                        CoroutineScope(Dispatchers.Main).launch {
                            Log.i(TAG, "main socket:: message $it")
                        }
                    }
                } catch (e: URISyntaxException) {
                    continuation.resumeWithException(e)
                }
            }
        }
    }


    private fun emmitOnlineEvent() {
        val jSONObject = JSONObject()
        jSONObject.put("message", "Online")
        jSONObject.put("user_id", currentUser?.id.toString())
        jSONObject.put(
            "token",
            "Bearer " + currentUser?.token.toString()
        )
        SocketManager.socket?.emit("set_online", jSONObject)
        Log.i(TAG, "main socket:: emit")
    }


    private fun videoPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // If Android version is 13 or above
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestVidPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                pickVidMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(requireContext(), "Permission not granted", Toast.LENGTH_SHORT)
                    .show()
                // Request the permission
                requestVidPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            } else {
                pickVidMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            }
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
                    EventBus.getDefault().post(MessageEvent("Main Stop"))
                    activity.replaceFragment(CalendarFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_add_post -> {
                    EventBus.getDefault().post(MessageEvent("Main Stop"))
                    showAddPostBottomDialog()
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_requests -> {
                    EventBus.getDefault().post(MessageEvent("Main Stop"))
                    activity.replaceFragment(RequestsFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_notification -> {
                    EventBus.getDefault().post(MessageEvent("Main Stop"))
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

        emmitOnlineEvent()
    }

    private fun responseFromViewModel() {

        viewModel.loading.observe(this) { isLoading ->
            dialog?.apply { if (isLoading) show() else dismiss() }
        }

        viewModel.addNewPostResponseLiveData.observe(this) { response ->
            Log.i(TAG, "registerUser:: response ${response.body()}")

            when (response.code()) {
                200 -> {
                    val userResponse = response.body()
                    if (userResponse?.success == true) {
                        clearNewPostViews()
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

                else -> showSnackBar(mBinding.root, response.errorBody().toString())
            }
        }


        viewModel.exceptionLiveData.observe(this) { exception ->

            exception?.let {
                dialog?.dismiss()
                showSnackBar(mBinding.root, exception.message.toString())
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

        bottomBinding?.rvPostImage?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true)
        bottomBinding?.rvPostVideo?.layoutManager =
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

        bottomBinding?.tvName?.text = currentUser?.name

        bottomBinding?.tvNewImage?.setOnClickListener {
            imagePermission()
        }
        bottomBinding?.tvNewVideo?.setOnClickListener {
            videoPermission()
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

        if (bottomSheetDialog != null && bottomSheetDialog?.isShowing == false) {
            bottomSheetDialog?.show()
        }

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
                            Log.i(TAG, "searchLocation:: ${place.name}")

                        }
                        .addOnFailureListener { exception ->
                            showSnackBar(mBinding.root, "Failed to fetch place details: $exception")
                        }


                } else {
                    showSnackBar(mBinding.root, "No results found")
                }
            }
            .addOnFailureListener { exception ->
                showSnackBar(mBinding.root, exception.message.toString())
            }

    }

    private fun checkBottomCredentials() {

        val description = bottomBinding?.etDes?.text.toString().trim()
        val loc = bottomBinding?.etLoc?.text.toString().trim()

        if (description.isEmpty()) {
            bottomBinding?.etDes?.error = "Description is required"
            return
        }
        if (viewModel.mediaImgList.isEmpty() && viewModel.mediaVidList.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please add at least one image or video.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        try {
            bottomSheetDialog?.dismiss()
        } catch (e: Exception) {
            Log.i(TAG, "checkBottomCredentials:: ${e.message}")
        }
        addNewPost(description, loc)

        bottomBinding?.etDes?.setText("")

    }

    private fun addNewPost(description: String, loc: String) {
        imagePartList.clear()

        imagePartList = viewModel.mediaImgList.mapNotNull { item ->
            Utils.getRealPathFromImgURI(requireContext(), item).let { path ->
                Utils.getFileFromPath(path)?.let { file ->
                    Utils.getPartFromFile("image/*", "img_url[]", file)
                }
            }
        }.toMutableList()
        videoPartList.clear()
        videoPartList = viewModel.mediaVidList.mapNotNull { item ->
            Utils.getRealPathFromImgURI(requireContext(), item).let { path ->
                Utils.getFileFromPath(path)?.let { file ->
                    Utils.getPartFromFile("video/*", "video_url[]", file)
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
            videoPartList
        )
    }

    private fun imagePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // If Android version is 13 or above
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(requireContext(), "Permission not granted", Toast.LENGTH_SHORT)
                    .show()
                // Request the permission
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            } else {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }

    private fun clearNewPostViews() {
        try {
            bottomBinding?.etDes?.clearFocus()
            bottomBinding?.etLoc?.clearFocus()

            bottomBinding?.etDes?.text = null
            bottomBinding?.etLoc?.text = null

            viewModel.mediaImgList.clear()
            viewModel.mediaVidList.clear()
            imagePartList.clear()
            videoPartList.clear()

            addPostImagesAdapter =
                AddPostImagesAdapter(viewModel.mediaImgList, requireContext(), this)
            bottomBinding?.rvPostImage?.adapter = addPostImagesAdapter

            addPostVideosAdapter =
                AddPostVideosAdapter(viewModel.mediaVidList, requireContext(), this)
            bottomBinding?.rvPostVideo?.adapter = addPostVideosAdapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onImageClick(imgPath: Uri) {
        showImageDialog(image = imgPath)
    }

    override fun onImgRemove(position: Int) {
        viewModel.mediaImgList.removeAt(position)
        addPostImagesAdapter.notifyDataSetChanged()
    }

    override fun onVideoClick(videoPath: Uri) {
        showVideoDialog(videoPath = videoPath)
    }

    override fun onVidRemove(position: Int) {
        viewModel.mediaVidList.removeAt(position)
        addPostVideosAdapter.notifyDataSetChanged()
    }

    private fun showVideoDialog(videoPath: Uri? = null, videoUrl: String? = null) {

        val videoBinding = ShowVideoDialogBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())
        dialog.setContentView(videoBinding.root)
        dialog.setCancelable(false)

        // Set window flags to make the dialog full screen
        val window = dialog.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        val player = ExoPlayer.Builder(requireContext()).build()
        videoBinding.videoPlayer.player = player

        videoBinding.btnClose.setOnClickListener {
            player.release()
            dialog.dismiss()
        }

        Log.i(TAG, "showVideoDialog:: videoPath $videoPath videoUrl $videoUrl ")

        if (videoPath != null) {
            videoBinding.videoView.visibility = View.VISIBLE
            videoBinding.videoPlayer.visibility = View.GONE
            videoBinding.videoView.setVideoURI(videoPath)
            videoBinding.videoView.start()
        } else {

            videoBinding.videoPlayer.visibility = View.VISIBLE
            videoBinding.videoView.visibility = View.GONE

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val mediaItem = withContext(Dispatchers.IO) {
                        MediaItem.fromUri(videoUrl.toString())
                    }
                    withContext(Dispatchers.Main) {
                        player.setMediaItem(mediaItem)
                        player.repeatMode = Player.REPEAT_MODE_ONE
                        player.prepare()
                        player.play()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error preparing video playback: ${e.message}")
                }
            }
        }

        dialog.show()

    }


    private fun showImageDialog(image: Uri? = null, imageUrl: String? = null) {

        val imageBinding = ShowImageDialogBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())
        dialog.setContentView(imageBinding.root)
        dialog.setCancelable(false)

        // Set window flags to make the dialog full screen
        val window = dialog.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )


        imageBinding.btnClose.setOnClickListener {
            dialog.dismiss()
        }

        if (imageUrl != null) {
            Glide
                .with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.img)
                .into(imageBinding.imageView)
        } else {
            imageBinding.imageView.setImageURI(image)
        }

        dialog.show()
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().post(MessageEvent("Main Stop"))
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }


}