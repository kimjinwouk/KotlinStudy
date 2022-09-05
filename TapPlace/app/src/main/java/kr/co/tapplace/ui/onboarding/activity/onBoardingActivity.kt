package kr.co.tapplace.ui.onboarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kr.co.tapplace.R
import kr.co.tapplace.adapter.onBoardingAdapter
import kr.co.tapplace.databinding.ActivityOnboardingBinding

class onBoardingActivity : AppCompatActivity() {


    // ActivityOnboardingBinding 선언
    private lateinit var binding: ActivityOnboardingBinding

    private lateinit var viewPager : ViewPager
    private lateinit var viewPagerAdapter: PagerAdapter
    private lateinit var dotsIndicator1 : DotsIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)


        viewPagerAdapter = onBoardingAdapter(this)
        binding.apply {
            viewPager = viewpager
            viewPager.adapter = viewPagerAdapter

            dotsIndicator1 = dotsIndicator

            dotsIndicator1.attachTo(viewPager)
            viewPager.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {

                }

                override fun onPageSelected(position: Int) {
                }

                override fun onPageScrollStateChanged(state: Int) {

                }
            })
        }




    }
}