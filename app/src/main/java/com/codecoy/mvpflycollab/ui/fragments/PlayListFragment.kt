package com.codecoy.mvpflycollab.ui.fragments

import android.Manifest
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
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.PlaylistCallback
import com.codecoy.mvpflycollab.databinding.BottomsheetNewplaylistBinding
import com.codecoy.mvpflycollab.databinding.FragmentPlayListBinding
import com.codecoy.mvpflycollab.datamodels.AllPlaylistData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.playlist.PlayListAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.PlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class PlayListFragment : Fragment(), PlaylistCallback {

    private lateinit var bottomBinding: BottomsheetNewplaylistBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private var dialog: Dialog? = null
    private var currentUser: UserLoginData? = null

    private lateinit var viewModel: PlaylistViewModel

//    private lateinit var activity: MainActivity

    private lateinit var playListAdapter: PlayListAdapter
    private lateinit var playList: MutableList<String>


    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (!uris.isNullOrEmpty()) {
                showSingleImage(uris)
            } else {
                Toast.makeText(requireContext(), "No media selected", Toast.LENGTH_SHORT).show()
            }
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


    private lateinit var mBinding: FragmentPlayListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPlayListBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        dialog = Constant.getDialog(requireContext())
        playList = arrayListOf()

        currentUser = Utils.getUserFromSharedPreferences(requireContext())

        mBinding.rvPlayList.layoutManager = GridLayoutManager(requireContext(), 2)
        mBinding.rvPlayList.setHasFixedSize(true)

        clickListeners()
        setUpViewModel()
        setUpBottomDialog()

        viewModel.allPlayList(
            "Bearer " + currentUser?.token.toString(),
            Utils.userId.toString()
        )
        mBinding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        if (currentUser?.id.toString() != Utils.userId) {
            mBinding.floatingActionButton.visibility = View.GONE
        } else {
            mBinding.floatingActionButton.visibility = View.VISIBLE
        }

    }

    override fun onResume() {
        super.onResume()

        responseFromViewModel()
    }

    private fun clickListeners() {
        mBinding.floatingActionButton.setOnClickListener {
            showBottomDialog()
        }
    }

    private fun setUpBottomDialog() {
        bottomBinding = BottomsheetNewplaylistBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomBinding.root)
    }

    private fun showBottomDialog() {

        bottomBinding.btnCreate.setOnClickListener {
            checkBottomCredentials(bottomBinding)
        }

        bottomBinding.tvAddImg.setOnClickListener {
            imagePermission()
        }
        bottomSheetDialog.show()
    }


    private fun checkBottomCredentials(bottomBinding: BottomsheetNewplaylistBinding) {

        val title = bottomBinding.etTitle.text.toString()
        val des = bottomBinding.etDes.text.toString()

        if (title.isEmpty()) {
            bottomBinding.etTitle.error = "Title is required"
            return
        }
        if (des.isEmpty()) {
            bottomBinding.etDes.error = "Description is required"
            return
        }
        if (viewModel.selectedImage.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please attach Playlist image", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (title.isNotEmpty() && des.isNotEmpty() && !viewModel.selectedImage.isNullOrEmpty()) {
            addNewPlaylist(title, des)
        }
    }

    private fun addNewPlaylist(title: String, des: String) {
        viewModel.addPlaylist(
            "Bearer " + currentUser?.token.toString(), currentUser?.id.toString(),
            title,
            des,
            viewModel.selectedImage.toString()
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

    private fun showSingleImage(uris: List<Uri>) {

        val file = File(Constant.getRealPathFromURI(requireContext(), uris[0]))
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val imagePart = MultipartBody.Part.createFormData("img", file.name, requestFile)

        viewModel.uploadImage(imagePart)

    }

    private fun setUpViewModel() {

        val mApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val userRepository = MvpRepository(mApi)

        viewModel = ViewModelProvider(
            this,
            MvpViewModelFactory(userRepository)
        )[PlaylistViewModel::class.java]

    }


    private fun responseFromViewModel() {
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                dialog?.show()
            } else {
                dialog?.dismiss()
            }
        }

        viewModel.allPlaylistResponseLiveData.observe(this) { response ->

            Log.i(Constant.TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val playListResponse = response.body()
                if (playListResponse != null && playListResponse.success == true) {

                    try {
                        setUpAdapter(playListResponse.allPlaylistData)
                    } catch (e: Exception) {
                        Log.i(Constant.TAG, "navControllerException:: ${e.message}")
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


        viewModel.imageResponseLiveData.observe(this) { response ->
            if (response.code() == 200) {
                val imageData = response.body()
                if (imageData != null && imageData.success == true && imageData.response != null) {
                    viewModel.selectedImage = imageData.response

                    Glide
                        .with(requireContext())
                        .load(Constant.MEDIA_BASE_URL + imageData.response)
                        .placeholder(R.drawable.img)
                        .into(bottomBinding.ivPlayImg)

                } else {

                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(requireContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }


        viewModel.addPlaylistResponseLiveData.observe(this) { response ->

            Log.i(Constant.TAG, "addJourneyResponseLiveData:: response $response")

            if (response.code() == 200) {
                val addPlaylistResponse = response.body()
                if (addPlaylistResponse != null && addPlaylistResponse.success == true) {

                    viewModel.selectedImage = null

                    viewModel.allPlayList(
                        "Bearer " + currentUser?.token.toString(),
                        currentUser?.id.toString()
                    )
                    clearBottomView()
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
                Log.i(Constant.TAG, "addJourneyResponseLiveData:: exception $exception")
                dialog?.dismiss()
            }
        }
    }

    private fun clearBottomView() {
        try {
            bottomBinding.etTitle.clearFocus()
            bottomBinding.etDes.clearFocus()
            bottomBinding.etTitle.setText("")
            bottomBinding.etDes.setText("")
            viewModel.selectedImage = null
        } catch (e: Exception) {

        }
    }

    private fun setUpAdapter(allPlaylistData: ArrayList<AllPlaylistData>) {

        if (allPlaylistData.isEmpty()) {
            mBinding.tvNoDataFound.visibility = View.VISIBLE
        } else {
            mBinding.tvNoDataFound.visibility = View.GONE
        }


        playListAdapter = PlayListAdapter(allPlaylistData, requireContext(), this)
        mBinding.rvPlayList.adapter = playListAdapter
    }

    override fun onPlaylistClick(playData: AllPlaylistData) {
        try {

            val action =
                PlayListFragmentDirections.actionPlayListFragmentToPlayListDetailFragment(playData)
            findNavController().navigate(action)

        } catch (e: Exception) {

        }
    }

    /*    override fun onAttach(context: Context) {
            super.onAttach(context)

            (context as MainActivity).also { activity = it }
        }*/


}