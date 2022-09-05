package kr.co.tapplace.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import kr.co.tapplace.R
import kr.co.tapplace.databinding.OnboadingSliderBinding


class onBoardingAdapter(val context: Context) : PagerAdapter() {

    private lateinit var binding: OnboadingSliderBinding

    override fun getCount(): Int = titleArray.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    val titleArray = arrayOf(
        R.string.onboarding_step_1_message_1,
        R.string.onboarding_step_2_message_1,
        R.string.onboarding_step_3_message_1,
    )

    val contentArray = arrayOf(
        R.string.onboarding_step_1_message_2,
        R.string.onboarding_step_2_message_2,
        R.string.onboarding_step_3_message_2,
    )


    //position에 해당하는 페이지 생성
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context)
        binding = OnboadingSliderBinding.inflate(view)
        binding.onBoardingTitleTextView.text = context.resources.getString(titleArray[position])
        binding.onBoardingContentTextView.text = context.resources.getString(contentArray[position])
        container.addView(binding.root)

        return binding.root
    }

    //position에 위치한 페이지 제거
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }


}
