package com.codecoy.mvpflycollab.ui.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.MvpRepository
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.PlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MultipartBody
import java.io.File
import java.util.Calendar
import java.util.Date
import java.util.Locale


class PlayListDetailFragment : Fragment(), VideoClickCallback {

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

    private val pickVideo =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    setVideoAdapter(uri)
                }
            } else {
                Toast.makeText(activity, "No video selected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun setVideoAdapter(uri: Uri) {
        viewModel.videoList.add(uri)
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


    }

    override fun onResume() {
        super.onResume()
        viewModel.allPlayList(currentUser?.token.toString(), currentUser?.id.toString())
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

            Log.i(Constant.TAG, "responseFromViewModel:: $response ")

            if (response.code() == 200) {
                val playlistDetailData = response.body()
                if (playlistDetailData != null && playlistDetailData.success == true && playlistDetailData.addPlaylistDetailsData != null) {
//                    viewModel.allJourneyDetailsList("Bearer " + currentUser?.token.toString(), allJourneyData?.id.toString())
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

        viewModel.exceptionLiveData.observe(this) { exception ->
            if (exception != null) {
                Log.i(Constant.TAG, "addJourneyResponseLiveData:: exception $exception")
                dialog?.dismiss()
            }
        }
    }

    private fun setUpDetailData(response: ArrayList<AllPlaylistDetailsData>) {
        playlistDetailAdapter = PlaylistDetailAdapter(response, activity)
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
            openVideoPicker()
        }

        bottomSheetDialog.show()
    }

    private fun openVideoPicker() {
        pickVideo.launch(Intent().setType("video/*").setAction(Intent.ACTION_GET_CONTENT))
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

        Log.i(TAG, "addNewPlaylistDetail:: viewModel.videoList ${viewModel.videoList}")

        for (item in viewModel.videoList) {

            val uri = Uri.parse(item.toString())
            val file = uri.path?.let { File(it) }
            val part = file?.let { Utils.getPartFromFile(it) }
            Log.i(TAG, "addNewPlaylistDetail:: file $part}")
            part?.let { videoPartList.add(it) }

//            Utils.getFilePathFromUri(activity, item)
//                ?.let {
//
//                    Log.i(TAG, "addNewPlaylistDetail:: getRealPathFromURI $it")
//
//                    Utils.getFileFromPath(it)?.let { file ->
//
//                        Log.i(TAG, "addNewPlaylistDetail:: getFileFromPath $file")
//
//                       val part = Utils.getPartFromFile(file)
//
//
//                        Log.i(TAG, "addNewPlaylistDetail:: getPartFromFile $part")
//
//                        videoPartList.add(part)
//                    }
//                }
        }

        viewModel.addPlaylistDetails(currentUser?.token.toString(), allPlaylistData?.id.toString(), title, description, date, videoPartList)

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

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }

}