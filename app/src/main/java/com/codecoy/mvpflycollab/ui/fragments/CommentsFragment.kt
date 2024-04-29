package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.callbacks.LikesCallback
import com.codecoy.mvpflycollab.databinding.FragmentCommentsBinding
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.CommentsAdapter
import java.io.StringReader


class CommentsFragment : Fragment(), LikesCallback{


//    private lateinit var activity: MainActivity

    private lateinit var commentsList: MutableList<String>
    private lateinit var commentsAdapter: CommentsAdapter

    private lateinit var mBinding: FragmentCommentsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCommentsBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        commentsList = arrayListOf()

        mBinding.rvComments.layoutManager = LinearLayoutManager(activity)
        mBinding.rvComments.setHasFixedSize(true)

        setUpData()

        mBinding.btnBackpress.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun setUpData() {

        commentsList.add("https://menshaircuts.com/wp-content/uploads/2022/06/male-models-jon-kortajarena-683x1024.jpg")
        commentsList.add("https://images.unsplash.com/photo-1488426862026-3ee34a7d66df?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8bW9kZWwlMjBpbWFnZXxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60")
        commentsList.add("https://manofmany.com/wp-content/uploads/2019/04/David-Gandy.jpg")
        commentsList.add("https://img.freepik.com/free-photo/portrait-handsome-confident-model-sexy-stylish-man-dressed-sweater-jeans-fashion-hipster-male-with-curly-hairstyle-posing-near-blue-wall-studio-isolated_158538-26600.jpg?w=2000")
        commentsList.add("https://menshaircuts.com/wp-content/uploads/2022/06/male-models-jon-kortajarena-683x1024.jpg")

        commentsAdapter = CommentsAdapter(commentsList, requireContext(), this)
        mBinding.rvComments.adapter = commentsAdapter

    }


    override fun onLikesClick() {
        try {
            val action = CommentsFragmentDirections.actionCommentsFragmentToLikesFragment()
            findNavController().navigate(action)
        }catch (e: Exception){

        }
    }

/*    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }*/



}