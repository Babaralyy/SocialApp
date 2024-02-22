package com.codecoy.mvpflycollab.ui.fragments

import android.Manifest
import android.app.DatePickerDialog
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
import com.codecoy.mvpflycollab.databinding.AddJourneyPicBottomDialogLayBinding
import com.codecoy.mvpflycollab.databinding.FragmentJouneyDetailBinding
import com.codecoy.mvpflycollab.datamodels.AddJourneyDetailBody
import com.codecoy.mvpflycollab.datamodels.AddJourneyDetailData
import com.codecoy.mvpflycollab.datamodels.AllJourneyData
import com.codecoy.mvpflycollab.datamodels.ImageBody
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
import com.codecoy.mvpflycollab.viewmodels.MvpRepository
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.Calendar
import java.util.Date
import java.util.Locale


class JourneyDetailFragment : Fragment() {

    private lateinit var activity: MainActivity
    private lateinit var journeyDetailAdapter: JourneyDetailAdapter
    private lateinit var journeyDetailImageAdapter: JourneyDetailImageAdapter

    private lateinit var journeyDetailDataList: MutableList<AddJourneyDetailData>


    private lateinit var viewModel: JourneyViewModel
    private var dialog: Dialog? = null
    private var currentUser: UserLoginData? = null

    private var allJourneyData: AllJourneyData? = null
    private val calendar = Calendar.getInstance()

    private lateinit var bottomBinding: AddJourneyPicBottomDialogLayBinding
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

        journeyDetailDataList = arrayListOf()
        currentUser = Utils.getUserFromSharedPreferences(activity)

        setUpViewModel()
        getJourneyData()

        setUpBottomDialog()
        clickListeners()

        mBinding.rvJourneyDetail.layoutManager = LinearLayoutManager(activity)

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
        bottomSheetDialog = BottomSheetDialog(activity)
        bottomSheetDialog.setContentView(bottomBinding.root)

        bottomBinding.rvJourneyDetailsImages.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        bottomBinding.rvJourneyDetailsImages.setHasFixedSize(true)
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

        viewModel.imageResponseLiveData.observe(this) { response ->
            if (response.code() == 200) {
                val imageData = response.body()
                if (imageData != null && imageData.success == true && imageData.response != null) {

                    viewModel.imagesList.add(imageData.response.toString())

                    viewModel.imagesList = viewModel.imagesList.distinct().toMutableList()


                    journeyDetailImageAdapter =
                        JourneyDetailImageAdapter(viewModel.imagesList, activity)
                    bottomBinding.rvJourneyDetailsImages.adapter = journeyDetailImageAdapter

                } else {

                    Toast.makeText(activity, response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(activity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }


        viewModel.journeyDetailsResponseLiveData.observe(this) { response ->
            if (response.code() == 200) {
                val journeyDetailsList = response.body()
                if (journeyDetailsList != null && journeyDetailsList.success == true) {

                    setUpDetailData(journeyDetailsList.journeyDetailsData)

                } else {

                    Toast.makeText(activity, response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(activity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
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


    private fun showDatePickerDialog(bottomBinding: AddJourneyPicBottomDialogLayBinding) {
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
            Toast.makeText(activity, "Please select date", Toast.LENGTH_SHORT).show()
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

        Log.i(TAG, "addNewJourneyDetail:: size ${viewModel.imagesList.size}")


        val detailsImages = arrayListOf<ImageBody>()


        detailsImages.clear()

        for(item in  viewModel.imagesList){
            detailsImages.add(ImageBody(item))
        }

        val addJourneyDetail = AddJourneyDetailBody(
            allJourneyData?.id,
            eventName,
            eventDescription,
            eventDate,
            detailsImages
        )

        viewModel.addJourneyDetail(
            "Bearer " + currentUser?.token.toString(), addJourneyDetail
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

        val file = File(Constant.getRealPathFromURI(activity,imageUri))
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val imagePart = MultipartBody.Part.createFormData("img", file.name, requestFile)

        viewModel.uploadImage(imagePart)

    }



    private fun setUpDetailData(journeyDetailsData: ArrayList<JourneyDetailsData>) {
        journeyDetailAdapter = JourneyDetailAdapter(journeyDetailsData, activity)
        mBinding.rvJourneyDetail.adapter = journeyDetailAdapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }
}