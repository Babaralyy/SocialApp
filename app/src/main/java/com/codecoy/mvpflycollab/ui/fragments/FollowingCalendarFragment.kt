package com.codecoy.mvpflycollab.ui.fragments

import android.Manifest
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
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
import com.applandeo.materialcalendarview.EventDay
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.ImageClickCallback
import com.codecoy.mvpflycollab.callbacks.ShareActivityCallback
import com.codecoy.mvpflycollab.callbacks.ToShareImageClickCallback
import com.codecoy.mvpflycollab.callbacks.ToShareVideoClickCallback
import com.codecoy.mvpflycollab.callbacks.playlistdetailsvideo.VideoClickCallback
import com.codecoy.mvpflycollab.databinding.BottomsheetAddCalendarBinding
import com.codecoy.mvpflycollab.databinding.BottomsheetCalendarBinding
import com.codecoy.mvpflycollab.databinding.BottomsheetToAddCalendarBinding
import com.codecoy.mvpflycollab.databinding.FragmentFollowingCalendarBinding
import com.codecoy.mvpflycollab.databinding.ShareWithMemnersDialogLayBinding
import com.codecoy.mvpflycollab.databinding.ShowImageDialogBinding
import com.codecoy.mvpflycollab.databinding.ShowVideoDialogBinding
import com.codecoy.mvpflycollab.datamodels.ActivityDetails
import com.codecoy.mvpflycollab.datamodels.AllActivitiesData
import com.codecoy.mvpflycollab.datamodels.AllActivitiesDateData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.CalendarAdapter
import com.codecoy.mvpflycollab.ui.adapters.ShareActivityAdapter
import com.codecoy.mvpflycollab.ui.adapters.ShareActivityImageAdapter
import com.codecoy.mvpflycollab.ui.adapters.ShareActivityVideoAdapter
import com.codecoy.mvpflycollab.ui.adapters.ShowActivityImageAdapter
import com.codecoy.mvpflycollab.ui.adapters.ShowActivityVideoAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.ActivityViewModel
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class FollowingCalendarFragment : Fragment(), ShareActivityCallback, VideoClickCallback,
    ImageClickCallback,
    ToShareImageClickCallback, ToShareVideoClickCallback {

    private lateinit var activity: MainActivity

    private lateinit var viewModel: ActivityViewModel
    private var dialog: Dialog? = null
    private val calendar = Calendar.getInstance()

    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var shareActivityAdapter: ShareActivityAdapter
    private lateinit var showActivityImageAdapter: ShowActivityImageAdapter
    private lateinit var showActivityVideoAdapter: ShowActivityVideoAdapter

    private lateinit var shareActivityImageAdapter: ShareActivityImageAdapter
    private lateinit var shareActivityVideoAdapter: ShareActivityVideoAdapter

    private lateinit var eventsList: MutableList<String>
    private lateinit var shareActivityList: MutableList<String>

    private var shareBinding: ShareWithMemnersDialogLayBinding? = null
    private var shareBottomSheetDialog: BottomSheetDialog? = null

    private var bottomBinding: BottomsheetToAddCalendarBinding? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    private var toShareBottomBinding: BottomsheetAddCalendarBinding? = null
    private var toShareBottomSheetDialog: BottomSheetDialog? = null

    private var currentUser: UserLoginData? = null
    private var formattedDate: String? = null

    private lateinit var videoPartList: MutableList<MultipartBody.Part>
    private lateinit var imagePartList: MutableList<MultipartBody.Part>

    private val requestImgPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickImgMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            imagePermission()
        }
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

    private val pickImgMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                showSingleImage(uri)
            } else {
                Toast.makeText(requireContext(), "No media selected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun showSingleImage(uri: Uri) {
        viewModel.mediaImgList.clear()
        viewModel.mediaImgList.add(0, uri)
        viewModel.mediaImgList.distinct()
        showActivityImageAdapter =
            ShowActivityImageAdapter(viewModel.mediaImgList, requireContext(), this)
        bottomBinding?.rvMediaImage?.adapter = showActivityImageAdapter
    }

    private val pickVidMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                showSingleVideo(uri)
            } else {
                Toast.makeText(requireContext(), "No media selected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun showSingleVideo(uri: Uri) {
        viewModel.mediaVidList.clear()
        viewModel.mediaVidList.add(0, uri)
        viewModel.mediaVidList.distinct()
        showActivityVideoAdapter =
            ShowActivityVideoAdapter(viewModel.mediaVidList, requireContext(), this)
        bottomBinding?.rvMediaVideo?.adapter = showActivityVideoAdapter
    }


    private lateinit var mBinding: FragmentFollowingCalendarBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFollowingCalendarBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        dialog = Constant.getDialog(requireContext())

        currentUser = Utils.getUserFromSharedPreferences(requireContext())
        eventsList = arrayListOf()

        imagePartList = arrayListOf()
        videoPartList = arrayListOf()

        shareActivityList = arrayListOf()

        mBinding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvEvents.setHasFixedSize(true)

        setUpViewModel()
        setUpBottomShareDialog()
        setUpBottomDialog()
        setUpShareActivityBottomDialog()

        mBinding.floatingActionButton.setOnClickListener {
            showAddActivityBottomDialog()
        }

        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        formattedDate = Utils.formatDate(currentDate)
        getActivitiesAgainstDate(formattedDate)

        onDateClick()
    }

    override fun onResume() {
        super.onResume()
        responseFromViewModel()
        getActivitiesAgainstDate(formattedDate)

    }

    private fun onDateClick() {

        mBinding.calendarView.setOnDayClickListener {

            val clickedDayCalendar: Calendar = it.calendar
            getActivitiesAgainstDate(Utils.formatGeorgiaDate(clickedDayCalendar))

            Log.i(Constant.TAG, "onDayClick:: date:$formattedDate")
        }
    }

    private fun setUpBottomDialog() {
        bottomBinding = BottomsheetToAddCalendarBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomBinding?.root?.let { bottomSheetDialog?.setContentView(it) }

        bottomBinding?.rvMediaImage?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        bottomBinding?.rvMediaVideo?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setUpShareActivityBottomDialog() {
        toShareBottomBinding = BottomsheetAddCalendarBinding.inflate(layoutInflater)
        toShareBottomSheetDialog = BottomSheetDialog(requireContext())
        toShareBottomBinding?.root?.let { toShareBottomSheetDialog?.setContentView(it) }


        toShareBottomBinding?.rvMediaImage?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        toShareBottomBinding?.rvMediaVideo?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

    }

    private fun getActivitiesAgainstDate(date: String?) {
        viewModel.allActivities(
            "Bearer " + currentUser?.token.toString(),
            Utils.storyDetail?.id.toString(),
            date.toString()
        )

        viewModel.allActivitiesDates(
            "Bearer " + currentUser?.token.toString(),
            Utils.storyDetail?.id.toString()
        )
    }

    private fun setUpViewModel() {

        val mApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val userRepository = MvpRepository(mApi)

        viewModel = ViewModelProvider(
            this,
            MvpViewModelFactory(userRepository)
        )[ActivityViewModel::class.java]
    }


    private fun responseFromViewModel() {
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                dialog?.show()
            } else {
                dialog?.dismiss()
            }
        }

        viewModel.allActivitiesResponseLiveData.observe(this) { response ->
            if (response.code() == 200) {


                val allActivitiesList = response.body()
                if (allActivitiesList != null && allActivitiesList.success == true) {

                    setUpRecyclerView(allActivitiesList.allActivitiesData)

                } else {

                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(requireContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.allActivitiesDateLiveData.observe(this) { response ->
            if (response.code() == 200) {
                val allActivitiesList = response.body()
                if (allActivitiesList != null && allActivitiesList.success == true) {

                    setActivitiesOnCalendar(allActivitiesList.response)

                } else {

                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(requireContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }


        viewModel.addActivityResponseLiveData.observe(this) { response ->

            Log.i(TAG, "responseFromViewModel:: $response ")

            if (response.code() == 200) {
                val activityData = response.body()
                if (activityData != null && activityData.success == true) {

                    viewModel.allActivitiesDates(
                        "Bearer " + currentUser?.token.toString(),
                        currentUser?.id.toString()
                    )

                    bottomSheetDialog?.dismiss()

                    try {
                        Utils.isFromFollowingCalendar = true
                        findNavController().navigate(FollowingCalendarFragmentDirections.actionFollowingCalendarFragmentToMainFragment())
                    }catch (e: Exception){
                        Log.i(TAG, "responseFromViewModel:: ${e.message}")
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
                dialog?.dismiss()
            }
        }
    }


    private fun setUpRecyclerView(allActivitiesData: ArrayList<AllActivitiesData>) {
        calendarAdapter = CalendarAdapter(allActivitiesData, requireContext(), this)
        mBinding.rvEvents.adapter = calendarAdapter
    }


    private fun setActivitiesOnCalendar(response: ArrayList<AllActivitiesDateData>) {

        val appDays: MutableList<EventDay> = arrayListOf()

        for (item in response) {
            Log.i(
                Constant.TAG,
                "setEventsOnCalendar:: test: date:" + item.activityDate
            )
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

            Log.i(
                Constant.TAG,
                "setEventsOnCalendar:: test: format:$format"
            )

            var d: Date? = null
            try {

                d = format.parse(item.activityDate.toString())

            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val c = Calendar.getInstance()
            if (d != null) {
                c.time = d
            }
            val eventDay = EventDay(c, R.drawable.dot_)
            appDays.add(eventDay)
        }

        mBinding.calendarView.setEvents(appDays)
    }

    private fun showShareWithMembersDialog() {

        shareActivityList.clear()

        shareActivityList.add("")
        shareActivityList.add("")
        shareActivityList.add("")
        shareActivityList.add("")

        shareActivityAdapter = ShareActivityAdapter(shareActivityList, requireContext())
        shareBinding?.rvActivityMembers?.adapter = shareActivityAdapter

        shareBottomSheetDialog?.show()
    }

    private fun setUpBottomShareDialog() {

        shareBinding = ShareWithMemnersDialogLayBinding.inflate(layoutInflater)
        shareBottomSheetDialog = BottomSheetDialog(requireContext())
        shareBinding?.root?.let { shareBottomSheetDialog?.setContentView(it) }

        shareBinding?.rvActivityMembers?.layoutManager = LinearLayoutManager(requireContext())
        shareBinding?.rvActivityMembers?.setHasFixedSize(true)

    }

    private fun showAddActivityBottomDialog() {

        bottomBinding?.etEventName?.setText(Utils.allActivitiesData?.activityName)
        bottomBinding?.etNote?.setText(Utils.allActivitiesData?.activityDescription)
        bottomBinding?.etDate?.text = Utils.allActivitiesData?.activityDate
        bottomBinding?.etStartTime?.text = Utils.allActivitiesData?.startTime
        bottomBinding?.etEndTime?.text = Utils.allActivitiesData?.endTime

        bottomBinding?.tvMediaImage?.setOnClickListener {
            try {
                imagePermission()
            } catch (e: Exception) {
                Log.i(Constant.TAG, "showAddActivityBottomDialog:: ${e.message}")
            }
        }

        bottomBinding?.tvMediaVideo?.setOnClickListener {
            videoPermission()
        }


        /*        bottomBinding?.etStartTime?.setOnClickListener {
                    showTimePickerDialog(activity, true)
                }
                bottomBinding?.etEndTime?.setOnClickListener {
                    showTimePickerDialog(activity, false)
                }
                bottomBinding?.etDate?.setOnClickListener {
                    showDatePickerDialog()
                }*/

        bottomBinding?.btnAddActivity?.setOnClickListener {
            checkBottomCredentials()
        }

        bottomSheetDialog?.show()
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
                Toast.makeText(requireContext(), "Permission not granted", Toast.LENGTH_SHORT)
                    .show()
                // Request the permission
                requestImgPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            } else {
                pickImgMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Update calendar with the selected date
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Update button text with the selected date
                bottomBinding?.etDate?.text = Utils.formatDate(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }


    private fun showTimePickerDialog(context: Context, flag: Boolean) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val is24HourFormat = false // Change to true if you want to use 24-hour format

        val timePickerDialog = TimePickerDialog(
            context,
            { view, hourOfDay, minute ->
                val amPm: String
                val hourFormatted: Int
                if (hourOfDay >= 12) {
                    amPm = "PM"
                    hourFormatted = if (hourOfDay == 12) hourOfDay else hourOfDay - 12
                } else {
                    amPm = "AM"
                    hourFormatted = if (hourOfDay == 0) 12 else hourOfDay
                }
                if (flag) {
                    bottomBinding?.etStartTime?.text =
                        String.format("%02d:%02d %s", hourFormatted, minute, amPm)
                } else {
                    bottomBinding?.etEndTime?.text =
                        String.format("%02d:%02d %s", hourFormatted, minute, amPm)
                }

            }, hour, minute, is24HourFormat
        )

        timePickerDialog.show()
    }

    private fun checkBottomCredentials() {

        val event = bottomBinding?.etEventName?.text.toString()
        val note = bottomBinding?.etNote?.text.toString()
        val date = bottomBinding?.etDate?.text.toString()
        val sTime = bottomBinding?.etStartTime?.text.toString()
        val eTime = bottomBinding?.etEndTime?.text.toString()


        if (event.isEmpty()) {
            bottomBinding?.etEventName?.error = "Event is required"
            return
        }
        if (note.isEmpty()) {
            bottomBinding?.etNote?.error = "Note is required"
            return
        }
        if (date.isEmpty()) {
            Toast.makeText(requireContext(), "Please select date", Toast.LENGTH_SHORT).show()
            return
        }

        if (sTime.isEmpty()) {
            Toast.makeText(requireContext(), "Please select start time", Toast.LENGTH_SHORT).show()
            return
        }
        if (eTime.isEmpty()) {
            Toast.makeText(requireContext(), "Please select end time", Toast.LENGTH_SHORT).show()
            return
        }

        if (event.isNotEmpty() && note.isNotEmpty() && date.isNotEmpty() && sTime.isNotEmpty() && eTime.isNotEmpty()) {
            addNewActivity(event, note, date, sTime, eTime)
        }
    }

    private fun addNewActivity(
        event: String,
        note: String,
        date: String,
        sTime: String,
        eTime: String
    ) {

        imagePartList.clear()

        for (item in viewModel.mediaImgList) {

            Utils.getRealPathFromImgURI(requireContext(), item)
                .let {
                    Log.i(Constant.TAG, "mediaImgList:: getRealPathFromURI $it")

                    if (it != null) {
                        Utils.getFileFromPath(it)?.let { file ->

                            Log.i(Constant.TAG, "mediaImgList:: getFileFromPath $file")

                            val part = Utils.getPartFromFile("image/*", "img_url[]", file)


                            Log.i(Constant.TAG, "mediaImgList:: getPartFromFile $part")

                            imagePartList.add(part)
                        }
                    }
                }
        }
        videoPartList.clear()

        for (item in viewModel.mediaVidList) {

            Utils.getRealPathFromVidURI(requireContext(), item)
                .let {
                    Log.i(Constant.TAG, "mediaVidList:: getRealPathFromURI $it")

                    if (it != null) {
                        Utils.getFileFromPath(it)?.let { file ->

                            Log.i(Constant.TAG, "mediaVidList:: getFileFromPath $file")

                            val part = Utils.getPartFromFile("video/*", "video_url[]", file)


                            Log.i(Constant.TAG, "mediaVidList:: getPartFromFile $part")

                            videoPartList.add(part)
                        }
                    }
                }
        }

        viewModel.addNewActivity(
            "Bearer " + currentUser?.token.toString(),
            Utils.createTextRequestBody(currentUser?.id.toString()),
            Utils.createTextRequestBody(event),
            Utils.createTextRequestBody(note),
            Utils.createTextRequestBody(date),
            Utils.createTextRequestBody(sTime),
            Utils.createTextRequestBody(eTime),
            imagePartList,
            videoPartList
        )
    }


    override fun onShareActivityClick(event: AllActivitiesData) {
        showShareActivityBottomDialog(event)
    }

    private fun showShareActivityBottomDialog(event: AllActivitiesData) {

        Utils.allActivitiesData = event

        toShareBottomBinding?.etEventName?.setText(event.activityName)
        toShareBottomBinding?.etNote?.setText(event.activityDescription)
        toShareBottomBinding?.etDate?.text = event.activityDate
        toShareBottomBinding?.etStartTime?.text = event.startTime
        toShareBottomBinding?.etEndTime?.text = event.endTime


        val filteredImgList = event.activityDetails.filter {
            !it.imgUrl.isNullOrEmpty()
        }
        val filteredVidList = event.activityDetails.filter {
            !it.videoUrl.isNullOrEmpty()
        }

        shareActivityImageAdapter = ShareActivityImageAdapter(
            filteredImgList as MutableList<ActivityDetails>,
            requireContext(),
            this
        )
        toShareBottomBinding?.rvMediaImage?.adapter = shareActivityImageAdapter
        shareActivityVideoAdapter = ShareActivityVideoAdapter(
            filteredVidList as MutableList<ActivityDetails>,
            requireContext(),
            this
        )
        toShareBottomBinding?.rvMediaVideo?.adapter = shareActivityVideoAdapter

        toShareBottomBinding?.ivShare?.setOnClickListener {
            bottomSheetDialog?.dismiss()
            showShareWithMembersDialog()
        }

        toShareBottomBinding?.btnAddToMyCalendar?.setOnClickListener {
            toShareBottomSheetDialog?.dismiss()
            showAddActivityBottomDialog()
        }

        toShareBottomSheetDialog?.show()
    }


    override fun onImageClick(imgUri: Uri) {
        showImageDialog(image = imgUri)
    }

    override fun onVideoClick(videoUri: Uri) {
        showVideoDialog(videoPath = videoUri)
    }

    override fun onShareImageClick(imgPath: String) {
        showImageDialog(imageUrl = imgPath)
    }

    override fun onShareVideoClick(videoPath: String) {
        showVideoDialog(videoUrl = videoPath)
    }

    private fun showVideoDialog(videoPath: Uri? = null, videoUrl: String? = null) {

        val videoBinding = ShowVideoDialogBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())
        dialog.setContentView(videoBinding.root)
        dialog.setCancelable(true)

        val window = dialog.window
        val height =
            (requireContext().resources.displayMetrics.widthPixels * 1.4).toInt() // 80% of screen width
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, height)

        if (videoPath != null) {
            videoBinding.videoView.visibility = View.VISIBLE
            videoBinding.videoView.setVideoURI(videoPath)
        } else {

            videoBinding.videoPlayer.visibility = View.VISIBLE

            val player = ExoPlayer.Builder(requireContext()).build()
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
                    Log.e(Constant.TAG, "Error preparing video playback: ${e.message}")
                }
            }
        }

        dialog.show()

    }


    private fun showImageDialog(image: Uri? = null, imageUrl: String? = null) {

        val imageBinding = ShowImageDialogBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())
        dialog.setContentView(imageBinding.root)
        dialog.setCancelable(true)

        val window = dialog.window
        val height =
            (requireContext().resources.displayMetrics.widthPixels * 01.4).toInt() // 80% of screen width
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, height)


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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }
}