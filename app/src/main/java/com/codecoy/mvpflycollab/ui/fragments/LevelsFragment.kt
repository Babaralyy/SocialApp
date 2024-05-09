package com.codecoy.mvpflycollab.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.FragmentLevelsBinding
import com.codecoy.mvpflycollab.datamodels.UserLevelsData
import com.codecoy.mvpflycollab.datamodels.UserLoginData
import com.codecoy.mvpflycollab.utils.Utils
import com.google.android.material.snackbar.Snackbar


class LevelsFragment : Fragment() {

    private var currentUser: UserLoginData? = null
    private var levelsData: UserLevelsData? = null

    private lateinit var mBinding: FragmentLevelsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentLevelsBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        currentUser = Utils.getUserFromSharedPreferences(requireContext())
        levelsData = Utils.getLevelsFromSharedPreferences(requireContext())

        levelsData.let {
            setUpViews()
        }

        mBinding.tvLevelTwo.setOnClickListener {

            val text = mBinding.tvLevelTwo.text.toString()
            if (text.isEmpty()){
                showSnackBar(mBinding.root, "To reach level two, continue using the app consecutively for 50 days.")
            }
        }
        mBinding.tvLevelThree.setOnClickListener {
            val text = mBinding.tvLevelThree.text.toString()
            if (text.isEmpty()){
                showSnackBar(mBinding.root, "To reach level three, continue using the app consecutively for 70 days.")
            }
        }
        mBinding.tvLevelFour.setOnClickListener {
            val text = mBinding.tvLevelFour.text.toString()
            if (text.isEmpty()){
                showSnackBar(mBinding.root, "To reach level four, continue using the app consecutively for 70 days.")
            }
        }

    }

    /*private fun setUpViews() {

        when (levelsData?.level) {
            "1" -> {
                mBinding.tvLevelOne.setBackgroundResource(R.drawable.levels_back)
                mBinding.tvLevelOne.text = "1"
                mBinding.sbLevels.max = 50
                mBinding.tvCompleteDay.text = "${levelsData?.consecutiveDays}/50 Days"
                mBinding.tvNextLevel.text = "2"
            }
            "2" -> {

                mBinding.tvLevelOne.setBackgroundResource(R.drawable.levels_back)
                mBinding.tvLevelOne.text = "1"
                mBinding.tvLevelTwo.setBackgroundResource(R.drawable.levels_back)
                mBinding.tvLevelTwo.text = "2"

                mBinding.sbLevels.max = 70
                mBinding.tvCompleteDay.text = "${levelsData?.consecutiveDays}/70 Days"
                mBinding.tvNextLevel.text = "3"
            }
            "3" -> {

                mBinding.tvLevelOne.setBackgroundResource(R.drawable.levels_back)
                mBinding.tvLevelOne.text = "1"
                mBinding.tvLevelTwo.setBackgroundResource(R.drawable.levels_back)
                mBinding.tvLevelTwo.text = "2"
                mBinding.tvLevelThree.setBackgroundResource(R.drawable.levels_back)
                mBinding.tvLevelThree.text = "3"

                mBinding.sbLevels.max = 70
                mBinding.tvCompleteDay.text = "${levelsData?.consecutiveDays}/70 Days"
                mBinding.tvNextLevel.text = "4"
            }

            "4" -> {

                mBinding.tvLevelOne.setBackgroundResource(R.drawable.levels_back)
                mBinding.tvLevelOne.text = "1"
                mBinding.tvLevelTwo.setBackgroundResource(R.drawable.levels_back)
                mBinding.tvLevelTwo.text = "2"
                mBinding.tvLevelThree.setBackgroundResource(R.drawable.levels_back)
                mBinding.tvLevelThree.text = "3"
                mBinding.tvLevelFour.setBackgroundResource(R.drawable.levels_back)
                mBinding.tvLevelFour.text = "4"

                mBinding.sbLevels.max = 70
                mBinding.tvCompleteDay.text = "${levelsData?.consecutiveDays}/70 Days"
                mBinding.tvNextLevel.text = "4"
            }
        }

        mBinding.tvCurrentLevel.text = levelsData?.level
        mBinding.tvStartLevel.text = levelsData?.level
        mBinding.tvStartDay.text = "${levelsData?.consecutiveDays} Day"
        mBinding.sbLevels.progress = levelsData?.consecutiveDays.toString().toInt()
    }*/

    private fun setUpViews() {
        val level = levelsData?.level?.toIntOrNull() ?: return
        val maxDays = when (level) {
            1 -> 50
            else -> 70
        }

        val levelViews = listOf(
            mBinding.tvLevelOne to "1",
            mBinding.tvLevelTwo to "2",
            mBinding.tvLevelThree to "3",
            mBinding.tvLevelFour to "4"
        )

        levelViews.take(level).forEachIndexed { index, (textView, text) ->
            textView.setBackgroundResource(R.drawable.levels_back)
            textView.text = text
        }

        mBinding.sbLevels.max = maxDays
        mBinding.tvCompleteDay.text = "${levelsData?.consecutiveDays}/$maxDays Days"
        mBinding.tvNextLevel.text = (level + 1).coerceAtMost(4).toString()

        mBinding.tvCurrentLevel.text = levelsData?.level
        mBinding.tvStartLevel.text = levelsData?.level
        mBinding.tvStartDay.text = "${levelsData?.consecutiveDays} Day"
        mBinding.sbLevels.progress = levelsData?.consecutiveDays?.toInt() ?: 0
    }

    private fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

}

