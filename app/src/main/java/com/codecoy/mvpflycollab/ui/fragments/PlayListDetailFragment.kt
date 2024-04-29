package com.codecoy.mvpflycollab.ui.fragments

import android.Manifest
import android.app.DatePickerDialog
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
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.ImageClickCallback
import com.codecoy.mvpflycollab.callbacks.PlaylistDetailCallback
import com.codecoy.mvpflycollab.callbacks.playlistdetailsvideo.VideoClickCallback
import com.codecoy.mvpflycollab.databinding.AddPlaylistDetailsBottomLayBinding
import com.codecoy.mvpflycollab.databinding.FragmentPlayListDetailBinding
import com.codecoy.mvpflycollab.databinding.ShowImageDialogBinding
import com.codecoy.mvpflycollab.databinding.ShowVideoDialogBinding
import com.codecoy.mvpflycollab.datamodels.AllPlaylistData
import com.codecoy.mvpflycollab.datamodels.AllPlaylistDetailsData
import com.codecoy.mvpflycollab.datamodels.PlaylistDetailData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.playlist.PlaylistDetailVideoAdapter
import com.codecoy.mvpflycollab.ui.adapters.playlist.PlaylistDetailAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.utils.RealPathUtil
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.adapters.JourneyDetailImageAdapter
import com.codecoy.mvpflycollab.ui.adapters.playlist.PlaylistDetailImageAdapter
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.PlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import java.util.Calendar
import java.util.Date
import java.util.Locale


class PlayListDetailFragment : Fragment(), VideoClickCallback, PlaylistDetailCallback,
    ImageClickCallback {

//    private lateinit var activity: MainActivity

    private lateinit var bottomBinding: AddPlaylistDetailsBottomLayBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var viewModel: PlaylistViewModel

    private lateinit var mediaController: MediaController

    private val calendar = Calendar.getInstance()

    private var allPlaylistData: AllPlaylistData? = null
    private var dialog: Dialog? = null

    private lateinit var playlistDetailAdapter: PlaylistDetailAdapter
    private lateinit var playlistDetailVideoAdapter: PlaylistDetailVideoAdapter
    private lateinit var playlistDetailImageAdapter: PlaylistDetailImageAdapter

    private lateinit var playDetailList: MutableList<PlaylistDetailData>
    private lateinit var videoPartList: MutableList<MultipartBody.Part>
    private lateinit var imagePartList: MutableList<MultipartBody.Part>

    private var currentUser: UserLoginData? = null


    private val requestVidPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickVideo.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
        } else {
            videoPermission()
        }
    }

    private val pickVideo =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(2)) { uris ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uris != null) {
                setVideoAdapter(uris)
            } else {
                Toast.makeText(requireContext(), "No media selected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun setVideoAdapter(uris: List<Uri>) {
        for (item in uris) {
            viewModel.videoList.add(item)
        }

        playlistDetailVideoAdapter = PlaylistDetailVideoAdapter(viewModel.videoList, requireContext(), this)
        bottomBinding.rvMediaVideo.adapter = playlistDetailVideoAdapter
    }

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
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uris != null) {
                showSingleImage(uris)
            } else {
                Toast.makeText(requireContext(), "No media selected", Toast.LENGTH_SHORT).show()
            }
        }
    private fun showSingleImage(uris: List<Uri>) {

        for (item in uris) {
            viewModel.imagesList.add(item)
        }
        playlistDetailImageAdapter = PlaylistDetailImageAdapter(viewModel.imagesList, requireContext(), this)
        bottomBinding.rvMediaImage.adapter = playlistDetailImageAdapter

    }

    private lateinit var mBinding: FragmentPlayListDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPlayListDetailBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        playDetailList = arrayListOf()
        videoPartList = arrayListOf()
        imagePartList = arrayListOf()

        currentUser = Utils.getUserFromSharedPreferences(requireContext())
        dialog = Constant.getDialog(requireContext())

        mBinding.rvPlayListDetail.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvPlayListDetail.setHasFixedSize(true)

        getPlaylistData()
        setUpViewModel()
        setUpBottomDialog()

        mBinding.floatingActionButton.setOnClickListener {
            showBottomDialog()
        }
        mBinding.btnBackPress.setOnClickListener {
            try {
                findNavController().popBackStack()
            } catch (e: Exception) {

            }
        }

        if (currentUser?.id.toString() != Utils.userId){
            mBinding.floatingActionButton.visibility = View.GONE
        } else {
            mBinding.floatingActionButton.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.allPlaylistDetailsList(
            "Bearer " + currentUser?.token.toString(),
            allPlaylistData?.id.toString()
        )
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

        viewModel.allPlaylistDetailsResponseLiveData.observe(this) { response ->

            Log.i(TAG, "responseFromViewModel:: ${response.body()}")

            if (response.code() == 200) {
                val playDetailsList = response.body()
                if (playDetailsList != null && playDetailsList.success == true) {

                    setUpDetailData(playDetailsList.response)

                } else {

                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(requireContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }


        viewModel.addPlaylistDetailsLiveData.observe(this) { response ->

            Log.i(TAG, "responseFromViewModel:: $response ")

            if (response.code() == 200) {
                val playlistDetailData = response.body()
                if (playlistDetailData != null && playlistDetailData.success == true && playlistDetailData.addPlaylistDetailsData != null) {
                    viewModel.allPlaylistDetailsList(
                        "Bearer " + currentUser?.token.toString(),
                        allPlaylistData?.id.toString()
                    )
                    bottomSheetDialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(requireContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }


        viewModel.videoResponseLiveData.observe(this) { response ->
            if (response.code() == 200) {
                val videoData = response.body()
                if (videoData != null && videoData.success == true && videoData.response != null) {

                    viewModel.selectedVideo = videoData.response

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
                Log.i(Constant.TAG, "addJourneyResponseLiveData:: exception $exception")
                dialog?.dismiss()
            }
        }
    }

    private fun setUpDetailData(response: ArrayList<AllPlaylistDetailsData>) {

        if (response.isEmpty()) {
            mBinding.tvNoDataFound.visibility = View.VISIBLE
        } else {
            mBinding.tvNoDataFound.visibility = View.GONE
        }
        playlistDetailAdapter = PlaylistDetailAdapter(response, requireContext(), this)
        mBinding.rvPlayListDetail.adapter = playlistDetailAdapter
    }

    private fun getPlaylistData() {
        allPlaylistData = arguments?.getParcelable("playlistData")

        if (allPlaylistData != null) {
            mBinding.tvPlaylistName.text = allPlaylistData?.title
        }

    }

    private fun setUpBottomDialog() {
        bottomBinding = AddPlaylistDetailsBottomLayBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomBinding.root)

        bottomBinding.rvMediaImage.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true)
        bottomBinding.rvMediaVideo.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true)
    }

    private fun setUpViewModel() {

        val mApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val userRepository = MvpRepository(mApi)

        viewModel = ViewModelProvider(
            this,
            MvpViewModelFactory(userRepository)
        )[PlaylistViewModel::class.java]
    }


    private fun showBottomDialog() {

        bottomBinding.btnAddPlayDetails.setOnClickListener {
            checkBottomCredentials(bottomBinding)
        }

        bottomBinding.tvDate.setOnClickListener {
            showDatePickerDialog()
        }

        bottomBinding.tvNewImage.setOnClickListener {
            imagePermission()
        }

        bottomBinding.tvAddVideo.setOnClickListener {
            videoPermission()
        }

        bottomSheetDialog.show()
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
                Toast.makeText(requireContext(), "Permission not granted", Toast.LENGTH_SHORT).show()
                // Request the permission
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            } else {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
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
                pickVideo.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(requireContext(), "Permission not granted", Toast.LENGTH_SHORT).show()
                // Request the permission
                requestVidPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            } else {
                pickVideo.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            }
        }
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Update calendar with the selected date
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Update button text with the selected date
                bottomBinding.tvDate.text = formatDate(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }


    private fun formatDate(date: Date): String {
        val dateFormat = "yyyy-MM-dd"
        val sdf = java.text.SimpleDateFormat(dateFormat, Locale.getDefault())
        return sdf.format(date)
    }

    private fun checkBottomCredentials(bottomBinding: AddPlaylistDetailsBottomLayBinding) {

        val title = bottomBinding.etTitle.text.toString()
        val description = bottomBinding.etDes.text.toString()
        val date = bottomBinding.tvDate.text.toString()


        if (title.isEmpty()) {
            bottomBinding.etTitle.error = "Title is required"
            return
        }
        if (description.isEmpty()) {
            bottomBinding.etDes.error = "Description is required"
            return
        }
        if (date.isEmpty()) {
            Toast.makeText(requireContext(), "Please select date", Toast.LENGTH_SHORT).show()
            return
        }
        if (title.isNotEmpty() && description.isNotEmpty() && date.isNotEmpty() && viewModel.videoList.isNotEmpty()) {
            addNewPlaylistDetail(title, description, date)
        }
    }

    private fun addNewPlaylistDetail(title: String, description: String, date: String) {

        imagePartList.clear()

        for (item in viewModel.imagesList) {

            Utils.getRealPathFromImgURI(requireContext(), item)
                .let {
                    Log.i(TAG, "mediaImgList:: getRealPathFromURI $it")

                    Utils.getFileFromPath(it)?.let { file ->

                        Log.i(TAG, "mediaImgList:: getFileFromPath $file")

                        val part = Utils.getPartFromFile("image/*", "img_url[]", file)


                        Log.i(TAG, "mediaImgList:: getPartFromFile $part")

                        imagePartList.add(part)
                    }
                }
        }
        videoPartList.clear()

        for (item in viewModel.videoList) {

            Utils.getRealPathFromVidURI(requireContext(), item)
                .let {
                    Log.i(TAG, "mediaVidList:: getRealPathFromURI $it")

                    Utils.getFileFromPath(it)?.let { file ->

                        Log.i(TAG, "mediaVidList:: getFileFromPath $file")

                        val part = Utils.getPartFromFile("video/*", "video_url[]", file)


                        Log.i(TAG, "mediaVidList:: getPartFromFile $part")

                        videoPartList.add(part)
                    }
                }
        }

        viewModel.addPlaylistDetails(
            "Bearer " + currentUser?.token.toString(),
            Utils.createTextRequestBody(allPlaylistData?.id.toString()),
            Utils.createTextRequestBody(title),
            Utils.createTextRequestBody(description),
            Utils.createTextRequestBody(date),
            videoPartList,
            imagePartList
        )

    }

    override fun onImageClick(imgPath: Uri) {

    }
    override fun onImgRemove(position: Int) {
        viewModel.imagesList.removeAt(position)
        playlistDetailImageAdapter.notifyDataSetChanged()
    }
    override fun onVidRemove(position: Int) {
        viewModel.videoList.removeAt(position)
        playlistDetailVideoAdapter.notifyDataSetChanged()
    }

    override fun onVideoClick(videoPath: Uri) {
        showVideoDialog(videoPath)
    }

    private fun showVideoDialog(videoPath: Uri) {

        val videoBinding = ShowVideoDialogBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())
        dialog.setContentView(videoBinding.root)
        dialog.setCancelable(true)

        // Initialize MediaController
        mediaController = MediaController(requireContext())
        mediaController.setAnchorView(videoBinding.videoView)

        // Set MediaController to VideoView
        videoBinding.videoView.setMediaController(mediaController)

        videoBinding.videoView.setVideoURI(videoPath)
        videoBinding.videoView.start()

        dialog.show()
    }

    override fun onImageUrlClick(s: String) {
        showImageDialog(imageUrl = s)
    }

    override fun onVideoUrlClick(mediaUrl: String) {
        showVideoDialog(videoUrl = mediaUrl)
    }

    private fun showImageDialog(image: Uri? = null, imageUrl: String? = null) {

        val imageBinding = ShowImageDialogBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())
        dialog.setContentView(imageBinding.root)
        dialog.setCancelable(false)

        // Set window flags to make the dialog full screen
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)


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

    private fun showVideoDialog(videoPath: Uri? = null, videoUrl: String? = null) {

        val videoBinding = ShowVideoDialogBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())
        dialog.setContentView(videoBinding.root)
        dialog.setCancelable(false)

        // Set window flags to make the dialog full screen
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

        val player = ExoPlayer.Builder(requireContext()).build()
        videoBinding.videoPlayer.player = player

        videoBinding.btnClose.setOnClickListener {
            player.release()
            dialog.dismiss()
        }

        if (videoPath != null) {
            videoBinding.videoView.visibility = View.VISIBLE
            videoBinding.videoView.setVideoURI(videoPath)
        } else {

            videoBinding.videoPlayer.visibility = View.VISIBLE


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

/*    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }*/
}