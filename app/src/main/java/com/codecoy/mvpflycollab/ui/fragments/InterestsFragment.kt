package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.FragmentInterestsBinding
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.interest.InterestsAdapter
import com.codecoy.mvpflycollab.utils.Constant.TAG


class InterestsFragment : Fragment() {

    private lateinit var activity: MainActivity

    private lateinit var interestsAdapter: InterestsAdapter
    private lateinit var interestsList: MutableList<String>

    private lateinit var mBinding: FragmentInterestsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentInterestsBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        interestsList = arrayListOf()

        mBinding.rvInterests.layoutManager = LinearLayoutManager(activity)
        mBinding.rvInterests.setHasFixedSize(true)


        setDataList()

        mBinding.btnSave.setOnClickListener {
            try {
                val action = InterestsFragmentDirections.actionInterestsFragmentToMainFragment()
                findNavController().navigate(action)
            } catch (e: Exception){

            }

        }

    }

    private fun setDataList() {

        interestsList.add("Cardio Exercises:")
        interestsList.add("Strength Training Exercises:")
        interestsList.add("Muscle Groups Targeted:")
        interestsList.add("High-Intensity Interval Training (HIIT):")
        interestsList.add("Flexibility and Mobility:")
        interestsList.add("Functional Training:")


        interestsAdapter = InterestsAdapter(interestsList, activity)
        mBinding.rvInterests.adapter = interestsAdapter

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }

}