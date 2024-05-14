package com.codecoy.mvpflycollab.ui.fragments

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.FragmentCompleteProfileBinding
import com.codecoy.mvpflycollab.datamodels.UpdateProfileBody
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.UserRegisterViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class CompleteProfileFragment : Fragment() {

    private lateinit var viewModel: UserRegisterViewModel

    private var dialog: Dialog? = null
    private var currentUser: UserLoginData? = null


    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                showSingleImage(uri)
            } else {
                Toast.makeText(requireContext(), "No media selected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun showSingleImage(imageUri: Uri) {

        val file = File(Utils.getRealPathFromImgURI(requireContext(), imageUri))
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val imagePart = MultipartBody.Part.createFormData("img", file.name, requestFile)

        viewModel.uploadProfileImage(imagePart)

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


    private lateinit var mBinding: FragmentCompleteProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCompleteProfileBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        currentUser = Utils.getUserFromSharedPreferences(requireContext())
        dialog = Constant.getDialog(requireContext())
        setUpViewModel()

        mBinding.ivProfile.setOnClickListener {
            // Launch the photo picker and let the user choose only images.
            imagePermission()
        }

        mBinding.tvChangeProfile.setOnClickListener {
            // Launch the photo picker and let the user choose only images.
            imagePermission()
        }

        mBinding.btnUpdateProfile.setOnClickListener {
            // Launch the photo picker and let the user choose only images.
            checkCredentials()
        }
    }

    private fun checkCredentials() {
        val fullName = mBinding.etName.text.toString()
        val mobileNumber = mBinding.etPhone.text.toString()
        val url = mBinding.etWebsite.text.toString()
        val me = mBinding.etAbout.text.toString()

        if (viewModel.selectedImage == null) {
            showSnackBar(mBinding.root, "Please select profile image")
            return
        }
        if (fullName.isEmpty()) {
            showSnackBar(mBinding.root, "Full name is required")
            return
        }
        if (mobileNumber.isEmpty()) {
            showSnackBar(mBinding.root, "Phone number is required")
            return
        } else {
            if (!Patterns.PHONE.matcher(mobileNumber).matches()) {
                mBinding.etPhone.error = "Please provide a valid number"
                return
            }
        }


        val updateProfileBody =
            UpdateProfileBody(
                currentUser?.id.toString(),
                fullName,
                mobileNumber,
                viewModel.selectedImage,
                url,
                me
            )
        Log.i(Constant.TAG, "checkCredentials:: ${currentUser?.token.toString()} $updateProfileBody")
        viewModel.updateProfile("Bearer " + currentUser?.token.toString(), updateProfileBody)

    }

    private fun setUpViewModel() {
        val mApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val userRepository = MvpRepository(mApi)

        viewModel = ViewModelProvider(
            this,
            MvpViewModelFactory(userRepository)
        )[UserRegisterViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
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
        viewModel.updateProfileResponseLiveData.observe(this) { response ->

            Log.i(Constant.TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val userData = response.body()
                if (userData != null && userData.success == true && userData.userList != null) {
                    val data = userData.userList
                    val updatedData= UserLoginData(data?.id, data?.profileImg, data?.name, data?.username, data?.phone, data?.email, data?.emailVerifiedAt, data?.deviceToken, data?.websiteUrl, data?.aboutMe, data?.createdAt, data?.updatedAt, currentUser?.token.toString())
                    Utils.saveUserToSharedPreferences(requireContext(), updatedData)
                    lifecycleScope.launch {
                        try {
                            findNavController().navigate(CompleteProfileFragmentDirections.actionCompleteProfileFragmentToMainFragment())
                        } catch (e: Exception) {
                            Log.i(Constant.TAG, "navControllerException:: ${e.message}")
                        }
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

        viewModel.profileImageResponseLiveData.observe(this) { response ->
            if (response.code() == 200) {
                val imageData = response.body()
                if (imageData != null && imageData.success == true && imageData.response != null) {
                    viewModel.selectedImage = imageData.response

                    Glide
                        .with(requireContext())
                        .load(Constant.MEDIA_BASE_URL + imageData.response)
                        .placeholder(R.drawable.img)
                        .into(mBinding.ivProfile)

                } else {

                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (response.code() == 401) {

            } else {
                Toast.makeText(requireContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.exceptionLiveData.observe(this){ exception ->
            if (exception != null){
                dialog?.dismiss()
            }
        }
    }

    private fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }
}