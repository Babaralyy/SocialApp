package com.codecoy.mvpflycollab.ui.fragments

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.codecoy.mvpflycollab.databinding.FragmentSignInBinding
import com.codecoy.mvpflycollab.datamodels.UserLoginBody
import com.codecoy.mvpflycollab.network.ApiCall
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Utils
import com.codecoy.mvpflycollab.repo.MvpRepository
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.viewmodels.MvpViewModelFactory
import com.codecoy.mvpflycollab.viewmodels.UserLoginViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch


class SignInFragment : Fragment() {

//    private lateinit var activity: MainActivity

    private lateinit var viewModel: UserLoginViewModel
    private var deviceToken: String? = null

    private var dialog: Dialog? = null

    private lateinit var mBinding: FragmentSignInBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSignInBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        setUpViewModel()
        clickListeners()


        Handler(Looper.getMainLooper()).postDelayed({

            deviceToken = Utils.fetchDeviceTokenFromPref(requireContext(), "tokenInfo")

            Log.i(ContentValues.TAG, "deviceToken:: deviceToken $deviceToken")

            if (deviceToken == null) {
                getDeviceToken()
                Log.i(ContentValues.TAG, "deviceToken:: deviceToken --> getDeviceToken is called")
            }

        }, 2000)

    }

    private fun getDeviceToken() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.i(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            deviceToken = task.result

            Utils.deviceTokenIntoPref(requireContext(), "tokenInfo", deviceToken.toString())

            Log.i(ContentValues.TAG, "deviceToken:: ----> $deviceToken")

        })
    }

    override fun onResume() {
        super.onResume()
        try {
            dialog = Constant.getDialog(requireContext())
        } catch (e: Exception){
            Log.i(TAG, "onResume: ${e.message}")
        }

        responseFromViewModel()
    }
    private fun clickListeners() {

        mBinding.tvForgot.setOnClickListener {
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
        }

        mBinding.btnSignIn.setOnClickListener {
            checkCredentials()
        }

        mBinding.tvSignUp.setOnClickListener {
            try {
                val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
                findNavController().navigate(action)
            }catch (e: Exception){
                Log.i(Constant.TAG, "navControllerException:: ${e.message}")
            }
        }

    }

    private fun checkCredentials() {

        val email = mBinding.etEmail.text.toString()
        val password = mBinding.etPassword.text.toString()

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
        if (email.isNotEmpty() && password.isNotEmpty()) {
            userLogin(email, password)
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

                            val action =
                                SignInFragmentDirections.actionSignInFragmentToMainFragment()
                            findNavController().navigate(action)
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


        viewModel.exceptionLiveData.observe(this){ exception ->
            if (exception != null){
                Log.i(TAG, "signinLiveData:: exception $exception")
                dialog?.dismiss()
            }
        }
    }

    private fun userLogin(email: String, password: String) {
        if (deviceToken != null){
            val userLoginBody = UserLoginBody(email, password, deviceToken)
            viewModel.userLogin(userLoginBody)
        } else {
            showSnackBar(mBinding.root, "Something went wrong")
        }

    }

    private fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

/*    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainrequireContext()).also { activity = it }
    }*/
}