package com.codecoy.mvpflycollab.ui.fragments

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.JourneyCallback
import com.codecoy.mvpflycollab.databinding.FragmentJourneyBinding
import com.codecoy.mvpflycollab.databinding.NewJourneyBottomDialogLayBinding
import com.codecoy.mvpflycollab.datamodels.AllJourneyData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.journey.JourneyAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.viewmodels.JourneyViewModel
import com.codecoy.mvpflycollab.viewmodels.MvpRepository
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class JourneyFragment : Fragment(), JourneyCallback {

    private lateinit var activity: MainActivity

    private lateinit var viewModel: JourneyViewModel
    private var dialog: Dialog? = null

    private lateinit var journeyAdapter: JourneyAdapter
    private lateinit var journeyList: MutableList<AllJourneyData>

    private lateinit var bottomBinding: NewJourneyBottomDialogLayBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog


    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                showSingleImage(uri)
            } else {
                Toast.makeText(activity, "No media selected", Toast.LENGTH_SHORT).show()
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


    private lateinit var mBinding: FragmentJourneyBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentJourneyBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        journeyList = arrayListOf()

        mBinding.rvJourney.layoutManager = LinearLayoutManager(activity)
        mBinding.rvJourney.setHasFixedSize(true)

        clickListeners()
        setUpViewModel()

        setUpBottomDialog()

        viewModel.allJourneyList(
            "Bearer " + Constant.currentUser?.token.toString(),
            Constant.currentUser?.id.toString()
        )

    }

    private fun setUpBottomDialog() {
        bottomBinding = NewJourneyBottomDialogLayBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(activity)
        bottomSheetDialog.setContentView(bottomBinding.root)
    }

    override fun onResume() {
        super.onResume()

        responseFromViewModel()
    }

    private fun clickListeners() {
        mBinding.floatingActionButton.setOnClickListener {
            showAddJourneyBottomDialog()
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


    private fun responseFromViewModel() {
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                dialog?.show()
            } else {
                dialog?.dismiss()
            }
        }

        viewModel.allJourneyResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val journeyResponse = response.body()
                if (journeyResponse != null && journeyResponse.success == true) {


                    try {
                        setUpAdapter(journeyResponse.allJourneyData)
                    } catch (e: Exception) {
                        Log.i(TAG, "navControllerException:: ${e.message}")
                    }

                } else {
                    Toast.makeText(activity, response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(activity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }


        viewModel.profileImageResponseLiveData.observe(this) { response ->
            if (response.code() == 200) {
                val imageData = response.body()
                if (imageData != null && imageData.success == true && imageData.response != null) {
                    viewModel.selectedImage = imageData.response

                    Glide
                        .with(activity)
                        .load(Constant.MEDIA_BASE_URL + imageData.response)
                        .placeholder(R.drawable.img)
                        .into(bottomBinding.ivJourneyImg)

                } else {

                    Toast.makeText(activity, response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(activity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }


        viewModel.addJourneyResponseLiveData.observe(this) { response ->

            Log.i(TAG, "addJourneyResponseLiveData:: response $response")

            if (response.code() == 200) {
                val addJourneyResponse = response.body()
                if (addJourneyResponse != null && addJourneyResponse.success == true) {

                    viewModel.selectedImage = null
                    Toast.makeText(activity, response.body()?.message, Toast.LENGTH_SHORT)
                        .show()

                    viewModel.allJourneyList("Bearer " + Constant.currentUser?.token.toString(),
                        Constant.currentUser?.id.toString())

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
                Log.i(TAG, "addJourneyResponseLiveData:: exception $exception")
                dialog?.dismiss()
            }
        }
    }

    private fun setUpAdapter(allJourneyData: ArrayList<AllJourneyData>) {

        if (allJourneyData.isEmpty()) {
            mBinding.tvNoDataFound.visibility = View.VISIBLE
        } else {
            mBinding.tvNoDataFound.visibility = View.GONE
        }

        journeyAdapter = JourneyAdapter(allJourneyData, activity, this)
        mBinding.rvJourney.adapter = journeyAdapter

    }



    private fun showAddJourneyBottomDialog() {


        bottomBinding.tvActivity.text = "${Constant.currentUser?.name}' Activity"

        bottomBinding.ivJourneyImg.setOnClickListener {
            // Launch the photo picker and let the user choose only images.
            imagePermission()

        }

        bottomBinding.btnCreate.setOnClickListener {
            checkBottomCredentials()
        }

        bottomSheetDialog.show()
    }

    private fun checkBottomCredentials() {

        val title = bottomBinding.etTitle.text.toString()
        val description = bottomBinding.etDes.text.toString()


        if (viewModel.selectedImage == null) {
            Toast.makeText(activity, "No media selected", Toast.LENGTH_SHORT).show()
            return
        }
        if (title.isEmpty()) {
            bottomBinding.etTitle.error = "Title is required"
            return
        }
        if (description.isEmpty()) {
            bottomBinding.etDes.error = "Description is required"
            return
        }
        if (viewModel.selectedImage != null && title.isNotEmpty() && description.isNotEmpty()) {
            addNewJourney(title, description)
        }
    }

    private fun addNewJourney(title: String, description: String) {

        Toast.makeText(activity, "addNewJourney", Toast.LENGTH_SHORT).show()

        viewModel.addJourney(
            "Bearer " + Constant.currentUser?.token.toString(),
            Constant.currentUser?.id.toString(),
            title,
            description,
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
                Toast.makeText(activity, "Permission not granted", Toast.LENGTH_SHORT).show()
                // Request the permission
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            } else {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }


    private fun showSingleImage(imageUri: Uri) {

        val file = File(getRealPathFromURI(imageUri))
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val imagePart = MultipartBody.Part.createFormData("img", file.name, requestFile)

        viewModel.uploadImage(imagePart)

    }

    private fun getRealPathFromURI(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity.contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val filePath = cursor?.getString(columnIndex!!)
        cursor?.close()
        return filePath ?: ""
    }


    override fun onJourneyClick(journeyData: AllJourneyData) {
        try {

            val action = JourneyFragmentDirections.actionJourneyFragmentToJourneyDetailFragment(journeyData)
            findNavController().navigate(action)

        } catch (e: Exception) {

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }

}