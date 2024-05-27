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
import androidx.appcompat.app.AlertDialog
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
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.journey.JourneyAdapter
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.JourneyViewModel
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class JourneyFragment : Fragment(), JourneyCallback {

//    private lateinit var activity: MainActivity

    private lateinit var viewModel: JourneyViewModel
    private var dialog: Dialog? = null
    private var currentUser: UserLoginData? = null

    private lateinit var journeyAdapter: JourneyAdapter
    private lateinit var journeyList: MutableList<AllJourneyData>

    private lateinit var bottomBinding: NewJourneyBottomDialogLayBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog


    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(2)) { uris ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uris != null) {
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

        dialog = Constant.getDialog(requireContext())
        journeyList = arrayListOf()
        currentUser = Utils.getUserFromSharedPreferences(requireContext())

        mBinding.rvJourney.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvJourney.setHasFixedSize(true)

        journeyAdapter = JourneyAdapter(mutableListOf(), requireContext(), this)
        mBinding.rvJourney.adapter = journeyAdapter

        clickListeners()
        setUpViewModel()

        setUpBottomDialog()
        responseFromViewModel()
        getAllJourney()


        mBinding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        if (currentUser?.id.toString() != Utils.userId){
           mBinding.floatingActionButton.visibility = View.GONE
        } else {
            mBinding.floatingActionButton.visibility = View.VISIBLE
        }

    }

    private fun getAllJourney() {
        viewModel.allJourneyList(
            "Bearer " + currentUser?.token.toString(),
            Utils.userId.toString()
        )
    }

    private fun setUpBottomDialog() {
        bottomBinding = NewJourneyBottomDialogLayBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomBinding.root)
    }

    override fun onResume() {
        super.onResume()


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

                }
                else {
                    mBinding.tvNoDataFound.visibility = View.VISIBLE
                    showSnackBar(mBinding.root, response.body()?.message ?: "Unknown error")
                }
            } else if (response.code() == 401) {

            } else {
                showSnackBar(mBinding.root, response.errorBody().toString())
            }
        }

        viewModel.imageResponseLiveData.observe(this) { response ->

            Log.i(TAG, "responseFromViewModel:: $response  ${response.body()}")

            if (response.code() == 200) {
                val imageData = response.body()
                if (imageData != null && imageData.success == true && imageData.response != null) {
                    viewModel.selectedImage = imageData.response

                    Glide
                        .with(requireContext())
                        .load(Constant.MEDIA_BASE_URL + viewModel.selectedImage)
                        .placeholder(R.drawable.img)
                        .into(bottomBinding.ivJourneyImg)

                } else {
                    showSnackBar(mBinding.root, response.body()?.message ?: "Unknown error")
                }
            } else if (response.code() == 401) {

            } else {
                showSnackBar(mBinding.root, response.errorBody().toString())
            }
        }


        viewModel.addJourneyResponseLiveData.observe(this) { response ->

            Log.i(TAG, "addJourneyResponseLiveData:: response $response")

            if (response.code() == 200) {
                val addJourneyResponse = response.body()
                if (addJourneyResponse != null && addJourneyResponse.success == true) {

                    viewModel.selectedImage = null
                    viewModel.allJourneyList("Bearer " + currentUser?.token.toString(),
                       currentUser?.id.toString())

                    clearBottomView()

                    bottomSheetDialog.dismiss()

                }
                else {
                    showSnackBar(mBinding.root, response.body()?.message ?: "Unknown error")
                }
            } else if (response.code() == 401) {

            } else {
                showSnackBar(mBinding.root, response.errorBody().toString())
            }
        }

        viewModel.deleteJourneyLiveData.observe(this) { response ->

            Log.i(TAG, "addJourneyResponseLiveData:: response $response")

            if (response.code() == 200) {
                val deleteJourneyResponse = response.body()
                if (deleteJourneyResponse != null && deleteJourneyResponse.success == true) {
                    getAllJourney()
                } else {
                    showSnackBar(mBinding.root, response.body()?.message ?: "Unknown error")
                }

            } else if (response.code() == 401) {

            } else {
                showSnackBar(mBinding.root, response.errorBody().toString())
            }
        }

        viewModel.exceptionLiveData.observe(this) { exception ->
            if (exception != null) {
                showSnackBar(mBinding.root, exception.message.toString())
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
        } catch (e: Exception){

        }
    }

    private fun setUpAdapter(allJourneyData: ArrayList<AllJourneyData>) {

        if (allJourneyData.isEmpty()) {
            mBinding.tvNoDataFound.visibility = View.VISIBLE
        } else {
            mBinding.tvNoDataFound.visibility = View.GONE
        }

        journeyAdapter.setItemList(allJourneyData)

    }



    private fun showAddJourneyBottomDialog() {


        bottomBinding.tvActivity.text = "${currentUser?.name}' Activity"

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
            Toast.makeText(requireContext(), "No media selected", Toast.LENGTH_SHORT).show()
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
            try {
                bottomSheetDialog.dismiss()
            }catch (e: Exception){
                Log.i(TAG, "checkBottomCredentials:: ${e.message}")
            }
            addNewJourney(title, description)
        }
    }

    private fun addNewJourney(title: String, description: String) {


        viewModel.addJourney(
            "Bearer " + currentUser?.token.toString(),
            currentUser?.id.toString(),
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
                Toast.makeText(requireContext(), "Permission not granted", Toast.LENGTH_SHORT).show()
                // Request the permission
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            } else {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }


    private fun showSingleImage(uris: List<Uri>) {
        try {
            val file = File(Utils.getRealPathFromImgURI(requireContext(), uris[0]))
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val imagePart = MultipartBody.Part.createFormData("img", file.name, requestFile)
            viewModel.uploadImage(imagePart)
        } catch (e: Exception){

        }

    }


    override fun onJourneyClick(journeyData: AllJourneyData) {
        try {

            val action = JourneyFragmentDirections.actionJourneyFragmentToJourneyDetailFragment(journeyData)
            findNavController().navigate(action)

        } catch (e: Exception) {

        }
    }

    override fun onJourneyDeleteClick(journeyData: AllJourneyData) {
        showAlertDialog(journeyData)
    }

    private fun showAlertDialog(journeyData: AllJourneyData) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(journeyData.title)
        builder.setMessage("Are you sure you want to delete?")
        builder.setPositiveButton("Yes") { dialog, which ->
            viewModel.deleteJourney("Bearer " + currentUser?.token.toString(), journeyData.id.toString())
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, which ->

            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

/*    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }*/

}