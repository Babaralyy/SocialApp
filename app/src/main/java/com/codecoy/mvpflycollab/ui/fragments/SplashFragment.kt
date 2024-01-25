package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.codecoy.mvpflycollab.databinding.FragmentSplashBinding
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.utils.DataStoreManager
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private lateinit var activity: MainActivity
    private val dataStoreManager: DataStoreManager by lazy { DataStoreManager(activity) }

    private lateinit var mBinding: FragmentSplashBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSplashBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        Handler(Looper.getMainLooper()).postDelayed({




            // Retrieve user using Flow
            lifecycleScope.launch {
                dataStoreManager.getUserData(UserLoginData(id = 0, name = "", username = "", phone = "", email = "",
                    emailVerifiedAt = "", deviceToken = "", createdAt = "", updatedAt = "", token = "")).collect { user ->

                    if (user.id != 0){
                        val action = SplashFragmentDirections.actionSplashFragmentToInterestsFragment()
                        findNavController().navigate(action)
                    } else {
                        val action = SplashFragmentDirections.actionSplashFragmentToWelcomeFragment()
                        findNavController().navigate(action)
                    }
                }
            }


        }, 1500)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).also { activity = it }
    }

}