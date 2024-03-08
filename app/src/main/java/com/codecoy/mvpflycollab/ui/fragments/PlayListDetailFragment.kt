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
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.callbacks.PlaylistDetailCallback
import com.codecoy.mvpflycollab.callbacks.playlistdetailsvideo.VideoClickCallback
import com.codecoy.mvpflycollab.databinding.AddPlaylistDetailsBottomLayBinding
import com.codecoy.mvpflycollab.databinding.FragmentPlayListDetailBinding
import com.codecoy.mvpflycollab.databinding.ShowVideoDialogBinding
import com.codecoy.mvpflycollab.datamodels.AllPlaylistData
import com.codecoy.mvpflycollab.datamodels.AllPlaylistDetailsData
import com.codecoy.mvpflycollab.datamodels.PlaylistDetailData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.PlaylistDetailVideoAdapter
import com.codecoy.mvpflycollab.ui.adapters.playlist.PlaylistDetailAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.utils.RealPathUtil
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.repo.MvpRepository
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


class PlayListDetailFragment : Fragment(), VideoClickCallback, PlaylistDetailCallback {

    private lateinit var activity: MainActivity

    private lateinit var bottomBinding: AddPlaylistDetailsBottomLayBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var viewModel: PlaylistViewModel

    private lateinit var mediaController: MediaController

    private val calendar = Calendar.getInstance()

    private var allPlaylistData: AllPlaylistData? = null
    private var dialog: Dialog? = null

    private lateinit var playlistDetailAdapter: PlaylistDetailAdapter
    private lateinit var playlistDetailVideoAdapter: PlaylistDetailVideoAdapter

    private lateinit var playDetailList: MutableList<PlaylistDetailData>
    private lateinit var videoPartList: MutableList<MultipartBody.Part>

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
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                setVideoAdapter(uri)
            } else {
                Toast.makeText(activity, "No media selected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun setVideoAdapter(uri: Uri) {
        viewModel.videoList.add(0, uri)
        playlistDetailVideoAdapter = PlaylistDetailVideoAdapter(viewModel.videoList, activity, this)
        bottomBinding.rvPlayDetailVideo.adapter = playlistDetailVideoAdapter
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

        currentUser = Utils.getUserFromSharedPreferences(activity)
        dialog = Constant.getDialog(activity)

        mBinding.rvPlayListDetail.layoutManager = LinearLayoutManager(activity)
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

                    Toast.makeText(activity, response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(activity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(activity, response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(activity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }


        viewModel.videoResponseLiveData.observe(this) { response ->
            if (response.code() == 200) {
                val videoData = response.body()
                if (videoData != null && videoData.success == true && videoData.response != null) {

                    viewModel.selectedVideo = videoData.response

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

    private fun setUpDetailData(response: ArrayList<AllPlaylistDetailsData>) {

        if (response.isEmpty()) {
            mBinding.tvNoDataFound.visibility = View.VISIBLE
        } else {
            mBinding.tvNoDataFound.visibility = View.GONE
        }
        playlistDetailAdapter = PlaylistDetailAdapter(response, activity, this)
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
        bottomSheetDialog = BottomSheetDialog(activity)
        bottomSheetDialog.setContentView(bottomBinding.root)

        bottomBinding.rvPlayDetailVideo.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
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

        bottomBinding.tvAddVideo.setOnClickListener {
            videoPermission()
        }

        bottomSheetDialog.show()
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
                Toast.makeText(activity, "Permission not granted", Toast.LENGTH_SHORT).show()
                // Request the permission
                requestVidPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            } else {
                pickVideo.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            }
        }
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            activity,
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
        val dateFormat = "dd/MM/yyyy"
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
            Toast.makeText(activity, "Please select date", Toast.LENGTH_SHORT).show()
            return
        }
        if (title.isNotEmpty() && description.isNotEmpty() && date.isNotEmpty() && viewModel.videoList.isNotEmpty()) {
            addNewPlaylistDetail(title, description, date)
        }
    }

    private fun addNewPlaylistDetail(title: String, description: String, date: String) {

        Log.i(
            TAG,
            "addNewPlaylistDetail:: viewModel.videoList ${
                RealPathUtil.getFilePathFromUri(
                    requireContext(),
                    viewModel.videoList[0]
                )
            }"
        )

        videoPartList.clear()

        for (item in viewModel.videoList) {

            Utils.getRealPathFromVidURI(activity, item)
                .let {
                    Log.i(TAG, "addNewPlaylistDetail:: getRealPathFromURI $it")

                    if (it != null) {
                        Utils.getFileFromPath(it)?.let { file ->

                            Log.i(TAG, "addNewPlaylistDetail:: getFileFromPath $file")

                            val part = Utils.getPartFromFile("video/*", "videos[]", file)


                            Log.i(TAG, "addNewPlaylistDetail:: getPartFromFile $part")

                            videoPartList.add(part)
                        }
                    }
                }
        }

        viewModel.addPlaylistDetails(
            "Bearer " + currentUser?.token.toString(),
            Utils.createTextRequestBody(allPlaylistData?.id.toString()),
            Utils.createTextRequestBody(title),
            Utils.createTextRequestBody(description),
            Utils.createTextRequestBody("20-02-2024"),
            videoPartList
        )


        Log.i(TAG, "addNewPlaylistDetail:: $videoPartList ${allPlaylistData?.id.toString()}")
    }

    override fun onVideoClick(videoPath: Uri) {
        showVideoDialog(videoPath)
    }

    private fun showVideoDialog(videoPath: Uri) {

        val videoBinding = ShowVideoDialogBinding.inflate(layoutInflater)

        val dialog = Dialog(activity)
        dialog.setContentView(videoBinding.root)
        dialog.setCancelable(true)

        // Initialize MediaController
        mediaController = MediaController(activity)
        mediaController.setAnchorView(videoBinding.videoView)

        // Set MediaController to VideoView
        videoBinding.videoView.setMediaController(mediaController)

        videoBinding.videoView.setVideoURI(videoPath)
        videoBinding.videoView.start()

        dialog.show()
    }

    override fun onVideoUrlClick(videoUrl: String) {
        showVideoUrlDialog(videoUrl)
    }

    private fun showVideoUrlDialog(videoUrl: String? = null) {

        val videoBinding = ShowVideoDialogBinding.inflate(layoutInflater)

        val dialog = Dialog(activity)
        dialog.setContentView(videoBinding.root)
        dialog.setCancelable(true)

        val window = dialog.window
        val height =
            (activity.resources.displayMetrics.widthPixels * 1.4).toInt() // 80% of screen width
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, height)


        videoBinding.videoPlayer.visibility = View.VISIBLE

        val player = ExoPlayer.Builder(activity).build()
        videoBinding.videoPlayer.player = player
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

        dialog.show()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }


}