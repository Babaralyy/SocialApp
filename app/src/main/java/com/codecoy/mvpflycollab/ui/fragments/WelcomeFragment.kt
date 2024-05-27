package com.codecoy.mvpflycollab.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.codecoy.mvpflycollab.databinding.FragmentWelcomeBinding
import com.codecoy.mvpflycollab.datamodels.UserLoginBody
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.UserLoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch


class WelcomeFragment : Fragment() {

    private lateinit var activity: MainActivity
    private var mGoogleSignInClient: GoogleSignInClient? = null

    private lateinit var viewModel: UserLoginViewModel
    private var deviceToken: String? = null

    private var dialog: Dialog? = null

    private lateinit var mBinding: FragmentWelcomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentWelcomeBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())

    }

    private fun inIt() {

        try {
            dialog = Constant.getDialog(requireContext())
        } catch (e: Exception){
            Log.i(TAG, "onResume: ${e.message}")
        }

        clickListeners()
        setUpViewModel()
        configureGoogleSignIn()
        responseFromViewModel()

        Handler(Looper.getMainLooper()).postDelayed({

            deviceToken = Utils.fetchDeviceTokenFromPref(requireContext(), "tokenInfo")

            Log.i(ContentValues.TAG, "deviceToken:: deviceToken $deviceToken")

            if (deviceToken == null) {
                getDeviceToken()
                Log.i(ContentValues.TAG, "deviceToken:: deviceToken --> getDeviceToken is called")
            }

        }, 2000)
    }

    private fun clickListeners() {
        mBinding.btnLogin.setOnClickListener {
            try {
                val action = WelcomeFragmentDirections.actionWelcomeFragmentToSignInFragment()
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.i(TAG, "navControllerException:: ${e.message}")
            }
        }
        mBinding.btnSignUp.setOnClickListener {
            try {
                val action = WelcomeFragmentDirections.actionWelcomeFragmentToSignUpFragment()
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.i(TAG, "navControllerException:: ${e.message}")
            }
        }
        mBinding.googleLay.setOnClickListener {
            dialog?.show()
            val signInIntent = mGoogleSignInClient?.signInIntent
            someActivityResultContract.launch(signInIntent)

        }
        mBinding.facebookLay.setOnClickListener {
            showSnackBar(mBinding.root, "Pending")
        }
    }

    private fun setUpViewModel() {

        val mApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val userRepository = MvpRepository(mApi)

        viewModel = ViewModelProvider(
            this,
            MvpViewModelFactory(userRepository)
        )[UserLoginViewModel::class.java]
    }

    private fun responseFromViewModel() {
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                dialog?.show()
            } else {
                dialog?.dismiss()
            }
        }
        viewModel.userLoginResponseLiveData.observe(this) { response ->

            Log.i(TAG, "registerUser:: response $response")

            if (response.code() == 200) {
                val userData = response.body()
                if (userData != null && userData.success == true && userData.user != null) {
                    lifecycleScope.launch {

                        try {
                            userData.user?.let {
                                Utils.saveUserToSharedPreferences(requireContext(), it)
                                Utils.saveNotificationStateIntoPref(requireContext(), true)
                            }

                            if (userData.user?.profileImg == "Google_Login"){
                                val action =
                                    WelcomeFragmentDirections.actionWelcomeFragmentToCompleteProfileFragment()
                                findNavController().navigate(action)
                            } else {
                                val action =
                                    WelcomeFragmentDirections.actionWelcomeFragmentToMainFragment()
                                findNavController().navigate(action)
                            }
                        } catch (e: Exception) {
                            Log.i(TAG, "navControllerException:: ${e.message}")
                        }
                    }
                } else {
                    showSnackBar(mBinding.root, response.body()?.message.toString())
                }
            } else if (response.code() == 401) {

            } else {
                showSnackBar(mBinding.root, response.body()?.message.toString())
            }
        }


        viewModel.exceptionLiveData.observe(this){ exception ->
            if (exception != null){
                showSnackBar(mBinding.root, exception.message.toString())
                dialog?.dismiss()
            }
        }
    }
    private fun configureGoogleSignIn() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }


    // Define your contract
    private val someActivityResultContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            dialog?.dismiss()
            // Handle the result here
            val data: Intent? = result.data
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)

        } else {
            dialog?.dismiss()
            showSnackBar(mBinding.root, "Canceled")
        }
    }


    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            val email = account.email
            val name = account.displayName

            // Signed in successfully, show authenticated UI.
            Log.i(TAG, "signInResult:successfully ${account.email}")

            userLogin(email, name)

        } catch (e: ApiException) {
            Log.i(TAG, "signInResult:failed code=" + e.statusCode)
            showSnackBar(mBinding.root, e.message.toString())
        }
    }

    private fun userLogin(email: String?, name: String?) {
        if (deviceToken != null){
            val userLoginBody = UserLoginBody(email = email, password = null, name = name, deviceToken = deviceToken, loginType = "google")
            viewModel.userLogin(userLoginBody)
        } else {
            showSnackBar(mBinding.root, "Device token not found")
        }

    }

    private fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun getDeviceToken() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                showSnackBar(mBinding.root,  task.exception?.message.toString())
                return@OnCompleteListener
            }

            // Get new FCM registration token
            deviceToken = task.result

            Utils.deviceTokenIntoPref(requireContext(), "tokenInfo", deviceToken.toString())

            Log.i(TAG, "deviceToken:: ----> $deviceToken")

        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }
}