package com.codecoy.mvpflycollab.ui.fragments

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.app.Dialog
import android.content.Context
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
import com.codecoy.mvpflycollab.databinding.FragmentSignUpBinding
import com.codecoy.mvpflycollab.datamodels.UserRegisterBody
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.UserRegisterViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class SignUpFragment : Fragment() {

//    private lateinit var activity: MainActivity

    private lateinit var viewModel: UserRegisterViewModel

    private var dialog: Dialog? = null


    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                showSingleImage(uri)
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


    private lateinit var mBinding: FragmentSignUpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSignUpBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        dialog = Constant.getDialog(requireContext())
        setUpViewModel()
        clickListeners()
    }
    override fun onResume() {
        super.onResume()

        if (viewModel.selectedImage != null) {
            Glide
                .with(requireContext())
                .load(Constant.MEDIA_BASE_URL + viewModel.selectedImage)
                .placeholder(R.drawable.img)
                .into(mBinding.ivProfile)
        }

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
        viewModel.userRegisterResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val userData = response.body()
                if (userData != null && userData.success == true && userData.data != null) {


                    lifecycleScope.launch {
                        try {
                            val action =
                                SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
                            findNavController().navigate(action)
                        } catch (e: Exception) {
                            Log.i(TAG, "navControllerException:: ${e.message}")
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

    private fun setUpViewModel() {
        val mApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val userRepository = MvpRepository(mApi)

        viewModel = ViewModelProvider(
            this,
            MvpViewModelFactory(userRepository)
        )[UserRegisterViewModel::class.java]
    }

    private fun clickListeners() {
        mBinding.ivAddProfile.setOnClickListener {
            // Launch the photo picker and let the user choose only images.
            imagePermission()

        }
        mBinding.btnSignUp.setOnClickListener {
            checkCredentials()
        }

        mBinding.tvSignIn.setOnClickListener {
            try {
                val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
                findNavController().navigate(action)
            }catch (e: Exception){
                Log.i(Constant.TAG, "navControllerException:: ${e.message}")
            }
        }

    }

    private fun imagePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // If Android version is 13 or above
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(READ_MEDIA_IMAGES)
            } else {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(requireContext(), "Permission not granted", Toast.LENGTH_SHORT).show()
                // Request the permission
                requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)

            } else {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }


    private fun checkCredentials() {

        val fullName = mBinding.etFullName.text.toString()
        val username = mBinding.etUserName.text.toString()
        val mobileNumber = mBinding.etNumber.text.toString()
        val email = mBinding.etEmail.text.toString()
        val password = mBinding.etPassword.text.toString()
        val confirmPassword = mBinding.etConfirmPassword.text.toString()

        if (viewModel.selectedImage == null) {
            Toast.makeText(requireContext(), "No media selected", Toast.LENGTH_SHORT).show()
            return
        }
        if (fullName.isEmpty()) {
            mBinding.etFullName.error = "Full name is required"
            return
        }
        if (username.isEmpty()) {
            mBinding.etUserName.error = "Username is required"
            return
        }
        if (mobileNumber.isNotEmpty()) {
            if (!Patterns.PHONE.matcher(mobileNumber).matches()) {
                mBinding.etNumber.error = "Please provide a valid number"
                return
            }
        } else {
            mBinding.etNumber.error = "Number is required"
            return
        }
        if (email.isNotEmpty()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mBinding.etEmail.error = "Please provide a valid email"
                return
            }
        } else {
            mBinding.etEmail.error = "Email is required"
            return
        }
        if (password.isEmpty()) {
            mBinding.etPassword.error = "Password is required"
            return
        }
        if (confirmPassword.isEmpty()) {
            mBinding.etConfirmPassword.error = "Confirm password is required"
            return
        }
        if (password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (password.equals(confirmPassword, ignoreCase = false)) {
//                registerUser(fullName, username, mobileNumber, email, password)

                val userRegisterBody =
                    UserRegisterBody(
                        viewModel.selectedImage,
                        fullName,
                        username,
                        mobileNumber,
                        email,
                        password
                    )
                viewModel.registerUser(userRegisterBody)
            } else {
                mBinding.etPassword.error = "Both passwords must be same"
                mBinding.etConfirmPassword.error = "Both passwords must be same"
            }
        }

    }

    private fun showSingleImage(imageUri: Uri) {

        val file = File(Utils.getRealPathFromImgURI(requireContext(), imageUri))
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val imagePart = MultipartBody.Part.createFormData("img", file.name, requestFile)

        viewModel.uploadProfileImage(imagePart)

    }



    override fun onStop() {
        super.onStop()
    }

/*    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }*/
}