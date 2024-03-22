package com.codecoy.mvpflycollab.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.FragmentSavedPostsBinding
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.UserProfilePostDetailAdapter
import com.codecoy.mvpflycollab.ui.adapters.UserSavedPostsAdapter
import com.codecoy.mvpflycollab.utils.Constant.TAG
import com.codecoy.mvpflycollab.viewmodels.UserViewModel


class SavedPostsFragment : Fragment() {

    private lateinit var activity: MainActivity
    private lateinit var viewModel: UserViewModel
    private var dialog: Dialog? = null

    private var currentUser: UserLoginData? = null

    private lateinit var userSavedPostsAdapter: UserSavedPostsAdapter

    private lateinit var mBinding: FragmentSavedPostsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSavedPostsBinding.inflate(inflater)
        inIt()
        return mBinding.root
    }

    private fun inIt() {
        mBinding.btnBackPress.setOnClickListener {
            try {
                findNavController().popBackStack()
            }catch (e: Exception){
                Log.i(TAG, "inIt:: ${e.message}")
            }
        }
    }
}