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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.ImageClickCallback
import com.codecoy.mvpflycollab.callbacks.JourneyDetailCallback
import com.codecoy.mvpflycollab.callbacks.playlistdetailsvideo.VideoClickCallback
import com.codecoy.mvpflycollab.databinding.AddJourneyPicBottomDialogLayBinding
import com.codecoy.mvpflycollab.databinding.FragmentJouneyDetailBinding
import com.codecoy.mvpflycollab.databinding.ShowImageDialogBinding
import com.codecoy.mvpflycollab.databinding.ShowVideoDialogBinding
import com.codecoy.mvpflycollab.datamodels.AddJourneyDetailData
import com.codecoy.mvpflycollab.datamodels.AllJourneyData
import com.codecoy.mvpflycollab.datamodels.JourneyDetailsData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.JourneyDetailImageAdapter
import com.codecoy.mvpflycollab.ui.adapters.journey.JourneyDetailAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.JourneyViewModel
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.adapters.journey.ShowJourneyVideoAdapter
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import java.util.Calendar
import java.util.Date
import java.util.Locale


class JourneyDetailFragment : Fragment(),JourneyDetailCallback, VideoClickCallback,
    ImageClickCallback {

//    private lateinit var activity: MainActivity

    private lateinit var journeyDetailAdapter: JourneyDetailAdapter
    private lateinit var journeyDetailImageAdapter: JourneyDetailImageAdapter
    private lateinit var showJourneyVideoAdapter: ShowJourneyVideoAdapter

    private lateinit var journeyDetailDataList: MutableList<AddJourneyDetailData>

    private lateinit var videoPartList: MutableList<MultipartBody.Part>
    private lateinit var imagePartList: MutableList<MultipartBody.Part>

    private lateinit var viewModel: JourneyViewModel
    private var dialog: Dialog? = null
    private var currentUser: UserLoginData? = null

    private var allJourneyData: AllJourneyData? = null
    private val calendar = Calendar.getInstance()

    private lateinit var bottomBinding: AddJourneyPicBottomDialogLayBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog

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
        journeyDetailImageAdapter = JourneyDetailImageAdapter(viewModel.mediaImgList, requireContext(), this)
        bottomBinding.rvJourneyDetailsImages.adapter = journeyDetailImageAdapter

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

        showJourneyVideoAdapter = ShowJourneyVideoAdapter(viewModel.mediaVidList, requireContext(), this)
        bottomBinding.rvMediaVideo.adapter = showJourneyVideoAdapter
    }

    private lateinit var mBinding: FragmentJouneyDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentJouneyDetailBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        imagePartList = arrayListOf()
        videoPartList = arrayListOf()

        dialog = Constant.getDialog(requireContext())
        journeyDetailDataList = arrayListOf()
        currentUser = Utils.getUserFromSharedPreferences(requireContext())

        setUpViewModel()
        getJourneyData()

        setUpBottomDialog()
        clickListeners()

        mBinding.rvJourneyDetail.layoutManager = LinearLayoutManager(requireContext())

        if (currentUser?.id.toString() != Utils.userId){
            mBinding.floatingActionButton.visibility = View.GONE
        } else {
            mBinding.floatingActionButton.visibility = View.VISIBLE
        }

    }

    private fun clickListeners() {

        mBinding.ivBack.setOnClickListener {
            try {
                findNavController().popBackStack()
            } catch (e: Exception) {
            }
        }

        mBinding.floatingActionButton.setOnClickListener {
            showAddJourneyImageBottomDialog()
        }

    }

    private fun setUpBottomDialog() {
        bottomBinding = AddJourneyPicBottomDialogLayBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomBinding.root)

        bottomBinding.rvJourneyDetailsImages.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true)
        bottomBinding.rvJourneyDetailsImages.setHasFixedSize(true)

        bottomBinding.rvMediaVideo.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true)
        bottomBinding.rvMediaVideo.setHasFixedSize(true)
    }


    override fun onResume() {
        super.onResume()
        viewModel.allJourneyDetailsList("Bearer " + currentUser?.token.toString(), allJourneyData?.id.toString())
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


        viewModel.journeyDetailsResponseLiveData.observe(this) { response ->
            if (response.code() == 200) {
                val journeyDetailsList = response.body()
                if (journeyDetailsList != null && journeyDetailsList.success == true) {

                    setUpDetailData(journeyDetailsList.journeyDetailsData)

                } else {

                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(requireContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }


        viewModel.addJourneyDetailLiveData.observe(this) { response ->

            Log.i(TAG, "responseFromViewModel:: $response ")

            if (response.code() == 200) {
                val journeyDetailData = response.body()
                if (journeyDetailData != null && journeyDetailData.success == true && journeyDetailData.addJourneyDetailData != null) {
                    viewModel.allJourneyDetailsList("Bearer " + currentUser?.token.toString(), allJourneyData?.id.toString())
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
        )[JourneyViewModel::class.java]
    }

    private fun getJourneyData() {
        allJourneyData = arguments?.getParcelable("journeyData")

        if (allJourneyData != null) {
            mBinding.tvName.text = allJourneyData?.title
        }

    }

    private fun showAddJourneyImageBottomDialog() {

        bottomBinding.tvMediaVideo.setOnClickListener {
            videoPermission()
        }

        bottomBinding.btnAddJourneyDetails.setOnClickListener {
            checkBottomCredentials(bottomBinding)
        }
        bottomBinding.tvDate.setOnClickListener {
            showDatePickerDialog(bottomBinding)
        }

        bottomBinding.tvNewImage.setOnClickListener {
            imagePermission()
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
                pickVidMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
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
                pickVidMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            }
        }
    }
    private fun showDatePickerDialog(bottomBinding: AddJourneyPicBottomDialogLayBinding) {
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

    private fun checkBottomCredentials(bottomBinding: AddJourneyPicBottomDialogLayBinding) {

        val eventName = bottomBinding.etEventName.text.toString()
        val eventDescription = bottomBinding.etEventDes.text.toString()
        val eventDate = bottomBinding.tvDate.text.toString()


        if (eventName.isEmpty()) {
            bottomBinding.etEventName.error = "Name is required"
            return
        }
        if (eventDescription.isEmpty()) {
            bottomBinding.etEventDes.error = "Description is required"
            return
        }
        if (eventDate.isEmpty()) {
            Toast.makeText(requireContext(), "Please select date", Toast.LENGTH_SHORT).show()
            return
        }

        if (viewModel.mediaImgList.isEmpty() && viewModel.mediaVidList.isEmpty()){
            Toast.makeText(requireContext(), "Please add media", Toast.LENGTH_SHORT).show()
            return
        }

        if (eventName.isNotEmpty() && eventDescription.isNotEmpty() && eventDate.isNotEmpty()) {
            addNewJourneyDetail(eventName, eventDescription, eventDate)
        }
    }

    private fun addNewJourneyDetail(
        eventName: String,
        eventDescription: String,
        eventDate: String
    ) {

        imagePartList.clear()

        for (item in viewModel.mediaImgList) {

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

        for (item in viewModel.mediaVidList) {

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

        viewModel.addJourneyDetail(
            "Bearer " + currentUser?.token.toString(),
            Utils.createTextRequestBody(allJourneyData?.id.toString()),
            Utils.createTextRequestBody(eventName),
            Utils.createTextRequestBody(eventDescription),
            Utils.createTextRequestBody(eventDate),
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
                Toast.makeText(requireContext(), "Permission not granted", Toast.LENGTH_SHORT).show()
                // Request the permission
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            } else {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }



    private fun setUpDetailData(journeyDetailsData: ArrayList<JourneyDetailsData>) {

        if (journeyDetailsData.isEmpty()) {
            mBinding.tvNoDataFound.visibility = View.VISIBLE
        } else {
            mBinding.tvNoDataFound.visibility = View.GONE
        }

        journeyDetailAdapter = JourneyDetailAdapter(journeyDetailsData, requireContext(), this)
        mBinding.rvJourneyDetail.adapter = journeyDetailAdapter
    }

    override fun onImageClick(imgPath: Uri) {
//        showImageDialog(imgPath)
    }
    override fun onImgRemove(position: Int) {
        viewModel.mediaImgList.removeAt(position)
        journeyDetailImageAdapter.notifyDataSetChanged()
    }
    override fun onVideoClick(videoPath: Uri) {

    }
    override fun onVidRemove(position: Int) {
        viewModel.mediaVidList.removeAt(position)
        showJourneyVideoAdapter.notifyDataSetChanged()
    }

    override fun onImgClick(imageData: String) {
        showImageDialog(imageUrl = imageData)
    }

    override fun onVidClick(imageData: String) {
        showVideoDialog(videoUrl = imageData)
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