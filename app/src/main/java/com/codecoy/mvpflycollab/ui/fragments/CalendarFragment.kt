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
import com.applandeo.materialcalendarview.EventDay
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.ImageClickCallback
import com.codecoy.mvpflycollab.callbacks.ShareActivityCallback
import com.codecoy.mvpflycollab.callbacks.ToShareImageClickCallback
import com.codecoy.mvpflycollab.callbacks.ToShareVideoClickCallback
import com.codecoy.mvpflycollab.callbacks.playlistdetailsvideo.VideoClickCallback
import com.codecoy.mvpflycollab.databinding.BottomsheetCalendarBinding
import com.codecoy.mvpflycollab.databinding.BottomsheetShareCalendarBinding
import com.codecoy.mvpflycollab.databinding.FragmentCalendarBinding
import com.codecoy.mvpflycollab.databinding.ShareWithMemnersDialogLayBinding
import com.codecoy.mvpflycollab.databinding.ShowImageDialogBinding
import com.codecoy.mvpflycollab.databinding.ShowVideoDialogBinding
import com.codecoy.mvpflycollab.datamodels.ActivityDetails
import com.codecoy.mvpflycollab.datamodels.AllActivitiesData
import com.codecoy.mvpflycollab.datamodels.AllActivitiesDateData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.network.ApiCall
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
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.utils.Constant.MEDIA_BASE_URL
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
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


class CalendarFragment : Fragment(), ShareActivityCallback, VideoClickCallback, ImageClickCallback,
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

    private var bottomBinding: BottomsheetCalendarBinding? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    private var toShareBottomBinding: BottomsheetShareCalendarBinding? = null
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
                Toast.makeText(activity, "No media selected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun showSingleImage(uri: Uri) {
        viewModel.mediaImgList.add(uri)
        viewModel.mediaImgList.distinct()
        showActivityImageAdapter = ShowActivityImageAdapter(viewModel.mediaImgList, activity, this)
        bottomBinding?.rvMediaImage?.adapter = showActivityImageAdapter
    }

    private val pickVidMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                showSingleVideo(uri)
            } else {
                Toast.makeText(activity, "No media selected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun showSingleVideo(uri: Uri) {
        viewModel.mediaVidList.add(uri)
        viewModel.mediaVidList.distinct()
        showActivityVideoAdapter = ShowActivityVideoAdapter(viewModel.mediaVidList, activity, this)
        bottomBinding?.rvMediaVideo?.adapter = showActivityVideoAdapter
    }


    private lateinit var mBinding: FragmentCalendarBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCalendarBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        dialog = Constant.getDialog(activity)

        currentUser = Utils.getUserFromSharedPreferences(activity)
        eventsList = arrayListOf()

        imagePartList = arrayListOf()
        videoPartList = arrayListOf()

        shareActivityList = arrayListOf()

        mBinding.rvEvents.layoutManager = LinearLayoutManager(activity)
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

        responseFromViewModel()
//        getActivitiesAgainstDate(formattedDate)

    }

    private fun onDateClick() {

        mBinding.calendarView.setOnDayClickListener {

            val clickedDayCalendar: Calendar = it.calendar
            getActivitiesAgainstDate(Utils.formatGeorgiaDate(clickedDayCalendar))

            Log.i(TAG, "onDayClick:: date:$formattedDate")
        }
    }

    private fun setUpBottomDialog() {
        bottomBinding = BottomsheetCalendarBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(activity)
        bottomBinding?.root?.let { bottomSheetDialog?.setContentView(it) }

        bottomBinding?.rvMediaImage?.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, true)

        bottomBinding?.rvMediaVideo?.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, true)
    }

    private fun setUpShareActivityBottomDialog() {
        toShareBottomBinding = BottomsheetShareCalendarBinding.inflate(layoutInflater)
        toShareBottomSheetDialog = BottomSheetDialog(activity)
        toShareBottomBinding?.root?.let { toShareBottomSheetDialog?.setContentView(it) }


        toShareBottomBinding?.rvMediaImage?.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        toShareBottomBinding?.rvMediaVideo?.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

    }

    override fun onResume() {
        super.onResume()


    }

    private fun getActivitiesAgainstDate(date: String?) {
        viewModel.allActivities(
            "Bearer " + currentUser?.token.toString(),
            currentUser?.id.toString(),
            date.toString()
        )

        viewModel.allActivitiesDates(
            "Bearer " + currentUser?.token.toString(),
            currentUser?.id.toString()
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

                }
                /*    else {

                        Toast.makeText(activity, response.body()?.message, Toast.LENGTH_SHORT)
                            .show()
                    }*/
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(activity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.allActivitiesDateLiveData.observe(this) { response ->
            if (response.code() == 200) {
                val allActivitiesList = response.body()
                if (allActivitiesList != null && allActivitiesList.success == true) {

                    setActivitiesOnCalendar(allActivitiesList.response)

                }
                /*   else {
                       Toast.makeText(activity, response.body()?.message, Toast.LENGTH_SHORT)
                           .show()
                   }*/
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(activity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
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


    private fun setUpRecyclerView(allActivitiesData: ArrayList<AllActivitiesData>) {
        if (allActivitiesData.isNotEmpty()) {
            mBinding.doesNotExist.visibility = View.GONE
        } else {
            mBinding.doesNotExist.visibility = View.VISIBLE
        }
        calendarAdapter = CalendarAdapter(allActivitiesData, activity, this)
        mBinding.rvEvents.adapter = calendarAdapter
    }


    private fun setActivitiesOnCalendar(response: ArrayList<AllActivitiesDateData>) {

        val appDays: MutableList<EventDay> = arrayListOf()

        for (item in response) {
            Log.i(
                TAG,
                "setEventsOnCalendar:: test: date:" + item.activityDate
            )
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

            Log.i(
                TAG,
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

        shareActivityAdapter = ShareActivityAdapter(shareActivityList, activity)
        shareBinding?.rvActivityMembers?.adapter = shareActivityAdapter

        shareBottomSheetDialog?.show()
    }

    private fun setUpBottomShareDialog() {

        shareBinding = ShareWithMemnersDialogLayBinding.inflate(layoutInflater)
        shareBottomSheetDialog = BottomSheetDialog(activity)
        shareBinding?.root?.let { shareBottomSheetDialog?.setContentView(it) }

        shareBinding?.rvActivityMembers?.layoutManager = LinearLayoutManager(activity)
        shareBinding?.rvActivityMembers?.setHasFixedSize(true)

    }

    private fun showAddActivityBottomDialog() {

        bottomBinding?.tvActivity?.text = "${currentUser?.username}' activity"

        bottomBinding?.tvMediaImage?.setOnClickListener {
            try {
                imagePermission()
            } catch (e: Exception) {
                Log.i(TAG, "showAddActivityBottomDialog:: ${e.message}")
            }
        }

        bottomBinding?.tvMediaVideo?.setOnClickListener {
            videoPermission()
        }


        bottomBinding?.etStartTime?.setOnClickListener {
            showTimePickerDialog(activity, true)
        }
        bottomBinding?.etEndTime?.setOnClickListener {
            showTimePickerDialog(activity, false)
        }
        bottomBinding?.etDate?.setOnClickListener {
            showDatePickerDialog()
        }
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
                Toast.makeText(activity, "Permission not granted", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(activity, "Permission not granted", Toast.LENGTH_SHORT).show()
                // Request the permission
                requestVidPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            } else {
                pickVidMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
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
            Toast.makeText(activity, "Please select date", Toast.LENGTH_SHORT).show()
            return
        }

        if (sTime.isEmpty()) {
            Toast.makeText(activity, "Please select start time", Toast.LENGTH_SHORT).show()
            return
        }
        if (eTime.isEmpty()) {
            Toast.makeText(activity, "Please select end time", Toast.LENGTH_SHORT).show()
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

            Utils.getRealPathFromImgURI(activity, item)
                .let {
                    Log.i(TAG, "mediaImgList:: getRealPathFromURI $it")

                    if (it != null) {
                        Utils.getFileFromPath(it)?.let { file ->

                            Log.i(TAG, "mediaImgList:: getFileFromPath $file")

                            val part = Utils.getPartFromFile("image/*", "img_url[]", file)


                            Log.i(TAG, "mediaImgList:: getPartFromFile $part")

                            imagePartList.add(part)
                        }
                    }
                }
        }
        videoPartList.clear()

        for (item in viewModel.mediaVidList) {

            Utils.getRealPathFromVidURI(activity, item)
                .let {
                    Log.i(TAG, "mediaVidList:: getRealPathFromURI $it")

                    if (it != null) {
                        Utils.getFileFromPath(it)?.let { file ->

                            Log.i(TAG, "mediaVidList:: getFileFromPath $file")

                            val part = Utils.getPartFromFile("video/*", "video_url[]", file)


                            Log.i(TAG, "mediaVidList:: getPartFromFile $part")

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
        toShareBottomBinding?.etEventName?.setText(event.activityName)
        toShareBottomBinding?.etNote?.setText(event.activityDescription)
        toShareBottomBinding?.etDate?.text = event.activityDate
        toShareBottomBinding?.etStartTime?.text = event.startTime
        toShareBottomBinding?.etEndTime?.text = event.endTime

        toShareBottomBinding?.tvActivity?.text = event.activityName


        val filteredImgList = event.activityDetails.filter {
            !it.imgUrl.isNullOrEmpty()
        }
        val filteredVidList = event.activityDetails.filter {
            !it.videoUrl.isNullOrEmpty()
        }

        shareActivityImageAdapter = ShareActivityImageAdapter(
            filteredImgList as MutableList<ActivityDetails>,
            activity,
            this
        )
        toShareBottomBinding?.rvMediaImage?.adapter = shareActivityImageAdapter
        shareActivityVideoAdapter = ShareActivityVideoAdapter(
            filteredVidList as MutableList<ActivityDetails>,
            activity,
            this
        )
        toShareBottomBinding?.rvMediaVideo?.adapter = shareActivityVideoAdapter

        toShareBottomBinding?.ivShare?.setOnClickListener {
            bottomSheetDialog?.dismiss()
            showShareWithMembersDialog()
        }


        toShareBottomSheetDialog?.show()
    }


    override fun onImageClick(imgUri: Uri) {
        showImageDialog(image = imgUri)
    }

    override fun onImgRemove(position: Int) {
        viewModel.mediaImgList.removeAt(position)
        showActivityImageAdapter.notifyDataSetChanged()
    }

    override fun onVideoClick(videoUri: Uri) {
        showVideoDialog(videoPath = videoUri)
    }

    override fun onVidRemove(position: Int) {
        viewModel.mediaVidList.removeAt(position)
        showActivityVideoAdapter.notifyDataSetChanged()
    }

    override fun onShareImageClick(imgPath: String) {
        showImageDialog(imageUrl = imgPath)
    }

    override fun onShareVideoClick(videoPath: String) {
        showVideoDialog(videoUrl = videoPath)
    }

    private fun showVideoDialog(videoPath: Uri? = null, videoUrl: String? = null) {

        val videoBinding = ShowVideoDialogBinding.inflate(layoutInflater)

        val dialog = Dialog(activity)
        dialog.setContentView(videoBinding.root)
        dialog.setCancelable(false)

        // Set window flags to make the dialog full screen
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

        val player = ExoPlayer.Builder(activity).build()
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


    private fun showImageDialog(image: Uri? = null, imageUrl: String? = null) {

        val imageBinding = ShowImageDialogBinding.inflate(layoutInflater)

        val dialog = Dialog(activity)
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
                .with(activity)
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