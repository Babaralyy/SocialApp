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
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
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
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
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
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uris != null) {
                showSingleImage(uris)
            } else {
                Toast.makeText(activity, "No media selected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun showSingleImage(uris: List<Uri>) {

        for (item in uris) {
            viewModel.mediaImgList.add(item)
        }
        showActivityImageAdapter =
            ShowActivityImageAdapter(viewModel.mediaImgList, requireContext(), this)
        bottomBinding?.rvMediaImage?.adapter = showActivityImageAdapter
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
        showActivityVideoAdapter =
            ShowActivityVideoAdapter(viewModel.mediaVidList, requireContext(), this)
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
        responseFromViewModel()


        mBinding.floatingActionButton.setOnClickListener {
            try {
                showAddActivityBottomDialog()
            } catch (e: Exception) {
                Log.i(TAG, "showAddActivityBottomDialog: ${e.message}")
            }

        }

        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        formattedDate = Utils.formatDate(currentDate)
        getActivitiesAgainstDate(formattedDate)

        onDateClick()


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
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomBinding?.root?.let { bottomSheetDialog?.setContentView(it) }

        bottomBinding?.rvMediaImage?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true)

        bottomBinding?.rvMediaVideo?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true)
    }

    private fun setUpShareActivityBottomDialog() {
        toShareBottomBinding = BottomsheetShareCalendarBinding.inflate(layoutInflater)
        toShareBottomSheetDialog = BottomSheetDialog(requireContext())
        toShareBottomBinding?.root?.let { toShareBottomSheetDialog?.setContentView(it) }


        toShareBottomBinding?.rvMediaImage?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        toShareBottomBinding?.rvMediaVideo?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

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

                } else {
                    showSnackBar(mBinding.root, response.body()?.message.toString())
                }
            } else if (response.code() == 401) {

            } else {
                showSnackBar(mBinding.root, response.errorBody().toString())
            }
        }

        viewModel.allActivitiesDateLiveData.observe(this) { response ->
            if (response.code() == 200) {
                val allActivitiesList = response.body()
                if (allActivitiesList != null && allActivitiesList.success == true) {

                    setActivitiesOnCalendar(allActivitiesList.response)

                } else {
                    showSnackBar(mBinding.root, response.body()?.message.toString())

                }
            } else if (response.code() == 401) {

            } else {
                showSnackBar(mBinding.root, response.errorBody().toString())
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

                    clearNewActivityViews()
                    bottomSheetDialog?.dismiss()

                } else {
                    showSnackBar(mBinding.root, response.body()?.message.toString())
                }
            } else if (response.code() == 401) {

            } else {
                showSnackBar(mBinding.root, response.errorBody().toString())
            }
        }

        viewModel.exceptionLiveData.observe(this) { exception ->
            if (exception != null) {
                Log.i(TAG, "addJourneyResponseLiveData:: exception $exception")
                showSnackBar(mBinding.root, exception.message.toString())
                dialog?.dismiss()
            }
        }
    }

    private fun clearNewActivityViews() {
        try {
            bottomBinding?.etEventName?.clearFocus()
            bottomBinding?.etNote?.clearFocus()

            bottomBinding?.etEventName?.text = null
            bottomBinding?.etNote?.text = null
            bottomBinding?.etDate?.text = ""
            bottomBinding?.etStartTime?.text = ""
            bottomBinding?.etEndTime?.text = ""

            viewModel.mediaImgList.clear()
            viewModel.mediaVidList.clear()
            imagePartList.clear()
            videoPartList.clear()

            showActivityImageAdapter =
                ShowActivityImageAdapter(viewModel.mediaImgList, requireContext(), this)
            bottomBinding?.rvMediaImage?.adapter = showActivityImageAdapter

            showActivityVideoAdapter =
                ShowActivityVideoAdapter(viewModel.mediaVidList, requireContext(), this)
            bottomBinding?.rvMediaVideo?.adapter = showActivityVideoAdapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setUpRecyclerView(allActivitiesData: ArrayList<AllActivitiesData>) {
        if (allActivitiesData.isNotEmpty()) {
            mBinding.doesNotExist.visibility = View.GONE
        } else {
            mBinding.doesNotExist.visibility = View.VISIBLE
        }
        calendarAdapter = CalendarAdapter(allActivitiesData, requireContext(), this)
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
            showTimePickerDialog(requireContext(), true)
        }
        bottomBinding?.etEndTime?.setOnClickListener {
            showTimePickerDialog(requireContext(), false)
        }
        bottomBinding?.etDate?.setOnClickListener {
            showDatePickerDialog()
        }
        bottomBinding?.btnAddActivity?.setOnClickListener {
            checkBottomCredentials()
        }

        if (bottomSheetDialog != null && bottomSheetDialog?.isShowing == false) {
            bottomSheetDialog?.show()
        }

    }

    private fun imagePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            requestImgPermissionLauncher.launch(permission)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                Toast.makeText(requireContext(), "Permission not granted", Toast.LENGTH_SHORT).show()
            }
        } else {
            pickImgMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }


    private fun videoPermission() {
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
            Toast.makeText(requireContext(), "Permission not granted", Toast.LENGTH_SHORT).show()
            requestVidPermissionLauncher.launch(permission)
        } else {
            pickVidMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
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
            { _, hourOfDay, minute ->
                val (amPm, hourFormatted) = when {
                    hourOfDay == 0 -> "AM" to 12
                    hourOfDay < 12 -> "AM" to hourOfDay
                    hourOfDay == 12 -> "PM" to 12
                    else -> "PM" to (hourOfDay - 12)
                }

                val formattedTime = String.format("%02d:%02d %s", hourFormatted, minute, amPm)
                if (flag) {
                    bottomBinding?.etStartTime?.text = formattedTime
                } else {
                    bottomBinding?.etEndTime?.text = formattedTime
                }
            },
            hour, minute, is24HourFormat
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
            Toast.makeText(
                requireContext(),
                getString(R.string.please_select_start_time), Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (eTime.isEmpty()) {
            Toast.makeText(requireContext(), "Please select end time", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Utils.isSecondTimeAhead(sTime, eTime)) {
            Toast.makeText(
                requireContext(),
                "The second time is not ahead of the first time.",
                Toast.LENGTH_SHORT
            ).show()
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
        addNewActivity(event, note, date, sTime, eTime)


    }

    private fun addNewActivity(
        event: String,
        note: String,
        date: String,
        sTime: String,
        eTime: String
    ) {

        imagePartList.clear()
        viewModel.mediaImgList.mapNotNull { item ->
            Utils.getRealPathFromImgURI(requireContext(), item).let { realPath ->
                Utils.getFileFromPath(realPath)?.let { file ->
                    Utils.getPartFromFile("image/*", "img_url[]", file).also {
                        imagePartList.add(it)
                        Log.i(TAG, "mediaImgList:: getPartFromFile $it")
                    }
                }
            }
        }

        videoPartList.clear()
        viewModel.mediaVidList.mapNotNull { item ->
            Utils.getRealPathFromVidURI(requireContext(), item).let { realPath ->
                Utils.getFileFromPath(realPath)?.let { file ->
                    Utils.getPartFromFile("video/*", "video_url[]", file).also {
                        videoPartList.add(it)
                        Log.i(TAG, "mediaVidList:: getPartFromFile $it")
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


    private fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }
}