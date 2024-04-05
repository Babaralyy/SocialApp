package com.codecoy.mvpflycollab.ui.adapters

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.codecoy.mvpflycollab.R
import com.codecoy.mvpflycollab.datamodels.UserPostsImages
import com.codecoy.mvpflycollab.utils.Constant
import com.codecoy.mvpflycollab.utils.Constant.TAG

class ImageSliderAdapter(private val context: Context, private val images: ArrayList<UserPostsImages>) : PagerAdapter() {

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(context)
//        imageView.setImageResource(images[position])

        Log.i(TAG, "instantiateItem:: $images")

        Glide
            .with(context)
            .load(Constant.MEDIA_BASE_URL + images[position].postImg)
            .placeholder(R.drawable.img)
            .into(imageView)

        container.addView(imageView)
        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}