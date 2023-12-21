package com.codecoy.mvpflycollab.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.databinding.FragmentMembersBinding
import com.codecoy.mvpflycollab.ui.activities.MainActivity
import com.codecoy.mvpflycollab.ui.adapters.MembersAdapter


class MembersFragment : Fragment() {

    private lateinit var activity: MainActivity
    private lateinit var membersAdapter: MembersAdapter
    private lateinit var membersList: MutableList<String>

    private lateinit var mBinding: FragmentMembersBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMembersBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        membersList = arrayListOf()

        mBinding.rvMembers.layoutManager = GridLayoutManager(activity, 3)
        mBinding.rvMembers.setHasFixedSize(true)



    }

    override fun onResume() {
        setUpAdapter()
        super.onResume()
    }

    private fun setUpAdapter() {


        membersList.add("https://images.unsplash.com/photo-1488426862026-3ee34a7d66df?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8bW9kZWwlMjBpbWFnZXxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60")
        membersList.add("https://img.freepik.com/free-photo/portrait-handsome-confident-model-sexy-stylish-man-dressed-sweater-jeans-fashion-hipster-male-with-curly-hairstyle-posing-near-blue-wall-studio-isolated_158538-26600.jpg?w=2000")
        membersList.add("https://manofmany.com/wp-content/uploads/2019/04/David-Gandy.jpg")
        membersList.add("https://menshaircuts.com/wp-content/uploads/2022/06/male-models-jon-kortajarena-683x1024.jpg")
        membersList.add("https://images.unsplash.com/photo-1494790108377-be9c29b29330?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1374&q=80")
        membersList.add("https://images.unsplash.com/photo-1517841905240-472988babdf9?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1374&q=80")
        membersList.add("https://images.unsplash.com/photo-1564485377539-4af72d1f6a2f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTZ8fG1vZGVsfGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60")
        membersList.add("https://images.unsplash.com/photo-1501196354995-cbb51c65aaea?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1471&q=80")
        membersList.add("https://images.unsplash.com/photo-1492288991661-058aa541ff43?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1374&q=80")
        membersList.add("https://menshaircuts.com/wp-content/uploads/2022/06/male-models-jon-kortajarena-683x1024.jpg")
        membersList.add("https://images.unsplash.com/photo-1494790108377-be9c29b29330?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1374&q=80")
        membersList.add("https://images.unsplash.com/photo-1517841905240-472988babdf9?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1374&q=80")


        membersAdapter = MembersAdapter(membersList, activity)
        mBinding.rvMembers.adapter = membersAdapter

    }



    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as MainActivity).also { activity = it }
    }

}